package vn.edu.uit.devorbit_api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.dto.publicapi.*;
import vn.edu.uit.devorbit_api.entity.*;
import vn.edu.uit.devorbit_api.repository.*;
import vn.edu.uit.devorbit_api.service.ai.CrawlerService;
import vn.edu.uit.devorbit_api.service.ai.OpenCodeAiService;
import vn.edu.uit.devorbit_api.service.ai.WebSearchService;
import vn.edu.uit.devorbit_api.service.knowledge.CourseKnowledgeBootstrapService;
import vn.edu.uit.devorbit_api.service.knowledge.FirecrawlClient;
import vn.edu.uit.devorbit_api.service.knowledge.KnowledgeRetrievalService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service orchestrating AI Q&A operations for course selection and advice.
 * Resolves intents, fetches DB contexts, scrapes web sites, and calls OpenCode Go.
 */
@Service
@RequiredArgsConstructor
public class SubjectQaService {

    private final CourseRepository courseRepository;
    private final GithubRepoRepository githubRepoRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final WebSearchService webSearchService;
    private final CrawlerService crawlerService;
    private final OpenCodeAiService openCodeAiService;
    private final FirecrawlClient firecrawlClient;
    private final CourseKnowledgeBootstrapService courseKnowledgeBootstrapService;
    private final KnowledgeRetrievalService knowledgeRetrievalService;
    private final ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(SubjectQaService.class);
    private static final Pattern COURSE_CODE_PATTERN = Pattern.compile("\\b([A-Z]{2,4}\\d{3,4})\\b");

    private ChatSession resolveSession(UUID sessionId, String userMessage) {
        if (sessionId == null) {
            ChatSession session = ChatSession.builder()
                .title(userMessage.length() > 30 ? userMessage.substring(0, 27) + "..." : userMessage)
                .build();
            return chatSessionRepository.save(session);
        }
        return chatSessionRepository.findById(sessionId)
            .orElseGet(() -> {
                ChatSession newS = ChatSession.builder()
                    .title(userMessage.length() > 30 ? userMessage.substring(0, 27) + "..." : userMessage)
                    .build();
                return chatSessionRepository.save(newS);
            });
    }

    public SubjectQaResponse processQuery(SubjectQaRequest request) {
        String userMessage = request.message();
        UUID sessionId = request.sessionId();

        // 1. Resolve or Create Chat Session
        ChatSession session = null;
        try {
            session = resolveSession(sessionId, userMessage);
            sessionId = session.getId();

            // Save student query message
            ChatMessage studentMsg = ChatMessage.builder()
                .session(session)
                .sender("STUDENT")
                .content(userMessage)
                .build();
            chatMessageRepository.save(studentMsg);
        } catch (Exception e) {
            log.warn("SubjectQaService: DB error during session setup, continuing without persistence: {}", e.getMessage());
            // Continue without DB persistence — return answer anyway
        }

        // 2. Scan for Course Codes in User Query
        Set<String> detectedCodes = new HashSet<>();
        Matcher matcher = COURSE_CODE_PATTERN.matcher(userMessage.toUpperCase());
        while (matcher.find()) {
            detectedCodes.add(matcher.group(1));
        }

        if (isGreeting(userMessage)) {
            return new SubjectQaResponse(
                buildGroundedGreeting(),
                sessionId != null ? sessionId : UUID.randomUUID(),
                List.of(),
                List.of(),
                "DIRECT"
            );
        }

        if (detectedCodes.isEmpty() && asksForInternalResources(userMessage)) {
            return new SubjectQaResponse(
                buildNeedsCourseCodeResponse(),
                sessionId != null ? sessionId : UUID.randomUUID(),
                List.of(),
                List.of(),
                "DIRECT"
            );
        }

        // 3. Build DB Context (Courses, Prerequisites, Repositories)
        List<Long> relevantNodeIds = new ArrayList<>();
        StringBuilder dbContext = new StringBuilder();
        
        for (String code : detectedCodes) {
            try {
                Optional<Course> courseOpt = courseRepository.findByMaMH(code);
                if (courseOpt.isPresent()) {
                    Course course = courseOpt.get();
                    relevantNodeIds.add(course.getId());

                    dbContext.append(String.format("=== MÔN HỌC: %s (%s) ===\n", course.getTenMH(), course.getMaMH()));
                    dbContext.append(String.format("- Số tín chỉ: %d (LT: %d, TH: %d)\n", course.getSoTC(), course.getLt(), course.getTh()));
                    dbContext.append(String.format("- Loại môn học: %s\n", course.getLoaiMonHoc()));
                    dbContext.append(String.format("- Đơn vị quản lý: %s\n", course.getManagementUnit()));
                    if (course.getDescription() != null) {
                        dbContext.append(String.format("- Tóm tắt: %s\n", course.getDescription()));
                    }
                    if (course.getLearningObjectives() != null) {
                        dbContext.append(String.format("- Mục tiêu học phần: %s\n", course.getLearningObjectives()));
                    }
                    if (course.getGradingCriteria() != null) {
                        dbContext.append(String.format("- Cơ chế đánh giá/tính điểm: %s\n", course.getGradingCriteria()));
                    }
                    if (course.getTopics() != null && !course.getTopics().isEmpty()) {
                        dbContext.append(String.format("- Các chủ đề học tập: %s\n", course.getTopics().toString()));
                    }

                    // Fetch linked projects
                    List<GithubRepo> repos = githubRepoRepository.findByCourseIdAndActiveTrue(course.getId());
                    courseKnowledgeBootstrapService.ensureCourseIndexed(course, repos);
                    if (repos != null && !repos.isEmpty()) {
                        dbContext.append("- Repository GitHub đã liên kết với môn học trên DevOrbit:\n");
                        for (GithubRepo repo : repos) {
                            dbContext.append(String.format("  * [%s](%s) - %s (Stars: %d)\n",
                                repo.getRepoName(), repo.getGithubUrl(), repo.getDescription(), repo.getStars()));
                        }
                    }
                    dbContext.append("\n");
                }
            } catch (Exception e) {
                log.warn("SubjectQaService: DB error building course context for {}, continuing without it: {}", code, e.getMessage());
            }
        }

        // 4. Perform Web Search & Crawl if required
        List<String> sources = new ArrayList<>();
        StringBuilder webContext = new StringBuilder();
        String queryType = "DIRECT";

        // Determine if web search is needed (e.g. asking for learning advice, tutorials, materials)
        boolean needsSearch = userMessage.toLowerCase().contains("làm sao") ||
                              userMessage.toLowerCase().contains("học tốt") ||
                              userMessage.toLowerCase().contains("kinh nghiệm") ||
                              userMessage.toLowerCase().contains("tài liệu") ||
                              userMessage.toLowerCase(Locale.ROOT).contains("lam sao") ||
                              userMessage.toLowerCase(Locale.ROOT).contains("hoc tot") ||
                              userMessage.toLowerCase(Locale.ROOT).contains("kinh nghiem") ||
                              userMessage.toLowerCase(Locale.ROOT).contains("tai lieu");

        if (needsSearch) {
            queryType = "SEARCH";
            try {
                WebSearchResponse searchResponse = webSearchService.search(userMessage);
                if (searchResponse != null && searchResponse.web() != null) {
                    // Take top 3 text/article links for crawling to save context tokens
                    List<WebSearchResponse.WebSearchResult> searchResults = searchResponse.web().stream()
                        .filter(res -> !res.url().contains("youtube.com") && !res.url().contains("tiktok.com"))
                        .limit(3)
                        .collect(Collectors.toList());

                    for (WebSearchResponse.WebSearchResult result : searchResults) {
                        sources.add(result.url());
                        String scrapedText = scrapeWithFirecrawl(result.url());
                        webContext.append(String.format("--- Nguồn từ internet: %s (Tiêu đề: %s) ---\n", result.url(), result.title()));
                        webContext.append(scrapedText).append("\n\n");
                    }
                }
            } catch (Exception e) {
                log.warn("SubjectQaService: web search/crawl failed, continuing without web context: {}", e.getMessage());
            }
        }

        String ragContext = buildSemanticKnowledgeContext(userMessage, detectedCodes);

        // 5. Build System Prompt for DeepSeek
        String systemPrompt = "Bạn là Trợ lý Cố vấn Học tập thông minh tại hệ thống DevOrbit dành cho sinh viên trường Đại học Công nghệ Thông tin (UIT).\n" +
                "Nhiệm vụ của bạn là tư vấn môn học, giải đáp đề cương chi tiết khi DevOrbit có dữ liệu, và giới thiệu repository GitHub đã liên kết với môn học trên DevOrbit.\n\n" +
                "Dưới đây là thông tin chính xác từ hệ thống DevOrbit để làm ngữ cảnh trả lời (ƯU TIÊN TUYỆT ĐỐI):\n" +
                dbContext.toString() + "\n" +
                "Tri thức truy xuất bằng embedding từ kho Knowledge RAG của DevOrbit:\n" +
                ragContext + "\n" +
                "Thông tin bổ trợ thu thập từ các bài viết/diễn đàn (sử dụng để tư vấn kinh nghiệm học tập):\n" +
                webContext.toString() + "\n" +
                "Quy tắc khi viết câu trả lời:\n" +
                "1. Trả lời bằng tiếng Việt chi tiết, cấu trúc rõ ràng, sử dụng định dạng Markdown.\n" +
                "2. Khi nhắc đến bất kỳ mã môn học nào (ví dụ: SE104, MA006), hãy viết HOA ĐÚNG mã môn để giao diện người dùng tự động render thành thẻ liên kết.\n" +
                "3. Chỉ dẫn link repository GitHub hoặc tài liệu thật sự xuất hiện trong ngữ cảnh để sinh viên truy cập.\n" +
                "4. Không bịa đặt mã môn học hoặc thông tin điểm số nằm ngoài ngữ cảnh.\n" +
                "5. Không tự nhận DevOrbit có ngân hàng đề thi, bài giảng, đồ án tiêu biểu, nhãn điểm A/A+, bộ lọc điểm số, hay mục tài nguyên học tập nếu các thứ đó không xuất hiện trong ngữ cảnh.\n" +
                "6. Nếu ngữ cảnh không có dữ liệu thật, hãy nói rõ DevOrbit hiện chỉ có thể tra cứu môn học, đề cương/tiêu chí đánh giá khi có trong DB, và repository GitHub đã liên kết theo môn; sau đó hỏi lại mã môn học hoặc repo cụ thể.\n";

        // 6. Generate Response from OpenCode Go
        String answer = openCodeAiService.generateCompletion(systemPrompt, userMessage);
        if (answer == null || answer.isBlank()) {
            log.warn("SubjectQaService: LLM returned blank answer, retrying with compact grounded prompt");
            answer = openCodeAiService.generateCompletion(
                buildCompactSystemPrompt(dbContext, ragContext, webContext),
                userMessage
            );
        }
        if (answer == null || answer.isBlank()) {
            answer = "Mình đã lấy được ngữ cảnh thật từ DevOrbit nhưng LLM không trả về nội dung trả lời. "
                + "Mình sẽ không bịa thêm thông tin. Bạn hãy thử hỏi lại với mã môn cụ thể hoặc câu hỏi hẹp hơn.";
        }

        // 7. Save AI response (best-effort)
        if (session != null) {
            try {
                ArrayNode sourcesArray = objectMapper.createArrayNode();
                for (String source : sources) {
                    sourcesArray.add(source);
                }

                ChatMessage aiMsg = ChatMessage.builder()
                    .session(session)
                    .sender("AI")
                    .content(answer)
                    .sources(sourcesArray)
                    .build();
                chatMessageRepository.save(aiMsg);

                session.setUpdatedAt(java.time.LocalDateTime.now());
                chatSessionRepository.save(session);
            } catch (Exception e) {
                log.warn("SubjectQaService: DB error saving AI response, continuing: {}", e.getMessage());
            }
        }

        return new SubjectQaResponse(
            answer,
            sessionId != null ? sessionId : UUID.randomUUID(),
            relevantNodeIds,
            sources,
            queryType
        );
    }

    private boolean isGreeting(String message) {
        String normalized = normalizeForIntent(message);
        return normalized.equals("hi") ||
            normalized.equals("hello") ||
            normalized.equals("helo") ||
            normalized.equals("chao") ||
            normalized.equals("xin chao") ||
            normalized.equals("chao ban");
    }

    private String buildSemanticKnowledgeContext(String userMessage, Set<String> detectedCodes) {
        StringBuilder context = new StringBuilder();
        List<String> scopes = new ArrayList<>();
        if (detectedCodes.isEmpty()) {
            scopes.add(null);
        } else {
            scopes.addAll(detectedCodes.stream().sorted().toList());
        }

        for (String courseCode : scopes) {
            try {
                KnowledgeRetrievalService.SearchResult result =
                    knowledgeRetrievalService.search(courseCode, userMessage, 5);
                if (result == null || result.chunks() == null) {
                    continue;
                }
                log.info("SubjectQaService: semantic retrieval returned {} chunks for course {}",
                    result.chunks().size(), courseCode);
                for (KnowledgeRetrievalService.ChunkResult chunkResult : result.chunks()) {
                    KnowledgeChunk chunk = chunkResult.chunk();
                    context.append(String.format(
                        "--- Chunk RAG: course=%s, section=%s, score=%.3f ---\n%s\n\n",
                        chunk.getCourseCode(),
                        chunk.getSectionTitle(),
                        chunkResult.score(),
                        trimForPrompt(chunk.getChunkText(), 1200)
                    ));
                }
            } catch (Exception e) {
                log.warn("SubjectQaService: semantic retrieval failed for course {}: {}", courseCode, e.getMessage());
            }
        }

        if (context.isEmpty()) {
            return "(Không tìm thấy chunk Knowledge RAG phù hợp hoặc embedding/search chưa sẵn sàng.)";
        }
        return context.toString();
    }

    private String scrapeWithFirecrawl(String url) {
        try {
            FirecrawlClient.FirecrawlResult result = firecrawlClient.scrape(url);
            if (result.isSuccess()) {
                log.info("SubjectQaService: Firecrawl scrape succeeded for {}", url);
                return trimForPrompt(result.markdown(), 2000);
            }
        } catch (Exception e) {
            log.warn("SubjectQaService: Firecrawl scrape failed for {}, falling back to JSoup: {}", url, e.getMessage());
        }
        return crawlerService.crawl(url);
    }

    private String buildCompactSystemPrompt(StringBuilder dbContext, String ragContext, StringBuilder webContext) {
        return "Bạn là trợ lý học tập DevOrbit. Chỉ trả lời dựa trên ngữ cảnh thật dưới đây, không bịa tính năng hoặc dữ liệu DevOrbit không có.\n"
            + "Nếu thiếu dữ liệu, nói rõ thiếu gì và hỏi lại mã môn/repo cụ thể.\n\n"
            + "DevOrbit DB context:\n" + trimForPrompt(dbContext.toString(), 2500) + "\n\n"
            + "Knowledge RAG context:\n" + trimForPrompt(ragContext, 2500) + "\n\n"
            + "Web/Firecrawl context:\n" + trimForPrompt(webContext.toString(), 2500);
    }

    private String trimForPrompt(String text, int maxChars) {
        if (text == null) {
            return "";
        }
        return text.length() <= maxChars ? text : text.substring(0, maxChars);
    }

    private boolean asksForInternalResources(String message) {
        String normalized = normalizeForIntent(message);
        return normalized.contains("do an") ||
            normalized.contains("project") ||
            normalized.contains("tai lieu") ||
            normalized.contains("on thi") ||
            normalized.contains("de thi") ||
            normalized.contains("repo");
    }

    private String normalizeForIntent(String message) {
        if (message == null) {
            return "";
        }
        return java.text.Normalizer.normalize(message, java.text.Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .replace('đ', 'd')
            .replace('Đ', 'D')
            .toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9\\s]", " ")
            .replaceAll("\\s+", " ")
            .trim();
    }

    private String buildGroundedGreeting() {
        return "Chào bạn! Mình là trợ lý học tập của DevOrbit. "
            + "Mình có thể tra cứu thông tin môn học UIT, đề cương/tiêu chí đánh giá khi có trong dữ liệu, "
            + "và các repository GitHub đã được liên kết với từng môn. "
            + "Hãy hỏi bằng mã môn học cụ thể, ví dụ SE104 hoặc MA006.";
    }

    private String buildNeedsCourseCodeResponse() {
        return "Mình chưa có dữ liệu đủ để gợi ý tài liệu hoặc đồ án chung chung. "
            + "DevOrbit chỉ nên trả lời dựa trên dữ liệu thật: môn học, đề cương/tiêu chí đánh giá nếu có, "
            + "và repository GitHub đã liên kết theo môn. "
            + "Bạn hãy gửi mã môn học cụ thể, ví dụ SE104, MA006, IS201 hoặc CS106, để mình tra đúng dữ liệu.";
    }
}
