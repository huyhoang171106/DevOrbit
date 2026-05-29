package vn.edu.uit.devorbit_api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.publicapi.*;
import vn.edu.uit.devorbit_api.entity.*;
import vn.edu.uit.devorbit_api.repository.*;
import vn.edu.uit.devorbit_api.service.ai.CrawlerService;
import vn.edu.uit.devorbit_api.service.ai.OpenCodeAiService;
import vn.edu.uit.devorbit_api.service.ai.WebSearchService;

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

    @Transactional
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

        // 3. Build DB Context (Courses, Prerequisites, Repositories)
        List<Long> relevantNodeIds = new ArrayList<>();
        StringBuilder dbContext = new StringBuilder();
        
        for (String code : detectedCodes) {
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
                if (repos != null && !repos.isEmpty()) {
                    dbContext.append("- Đồ án tiêu biểu của sinh viên khóa trước trên DevOrbit:\n");
                    for (GithubRepo repo : repos) {
                        dbContext.append(String.format("  * [%s](%s) - %s (Stars: %d)\n", 
                            repo.getRepoName(), repo.getGithubUrl(), repo.getDescription(), repo.getStars()));
                    }
                }
                dbContext.append("\n");
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
                              detectedCodes.isEmpty();

        if (needsSearch) {
            queryType = "SEARCH";
            WebSearchResponse searchResponse = webSearchService.search(userMessage);
            if (searchResponse != null && searchResponse.web() != null) {
                // Take top 3 text/article links for crawling to save context tokens
                List<WebSearchResponse.WebSearchResult> searchResults = searchResponse.web().stream()
                    .filter(res -> !res.url().contains("youtube.com") && !res.url().contains("tiktok.com"))
                    .limit(3)
                    .collect(Collectors.toList());

                for (WebSearchResponse.WebSearchResult result : searchResults) {
                    sources.add(result.url());
                    String scrapedText = crawlerService.crawl(result.url());
                    webContext.append(String.format("--- Nguồn từ internet: %s (Tiêu đề: %s) ---\n", result.url(), result.title()));
                    webContext.append(scrapedText).append("\n\n");
                }
            }
        }

        // 5. Build System Prompt for DeepSeek
        String systemPrompt = "Bạn là Trợ lý Cố vấn Học tập thông minh tại hệ thống DevOrbit dành cho sinh viên trường Đại học Công nghệ Thông tin (UIT).\n" +
                "Nhiệm vụ của bạn là tư vấn môn học, chia sẻ tài liệu ôn thi, giải đáp về đề cương chi tiết (mục tiêu học phần, cách chấm điểm) và gợi ý đồ án xuất sắc của sinh viên khóa trước trên DevOrbit.\n\n" +
                "Dưới đây là thông tin chính xác từ hệ thống DevOrbit để làm ngữ cảnh trả lời (ƯU TIÊN TUYỆT ĐỐI):\n" +
                dbContext.toString() + "\n" +
                "Thông tin bổ trợ thu thập từ các bài viết/diễn đàn (sử dụng để tư vấn kinh nghiệm học tập):\n" +
                webContext.toString() + "\n" +
                "Quy tắc khi viết câu trả lời:\n" +
                "1. Trả lời bằng tiếng Việt chi tiết, cấu trúc rõ ràng, sử dụng định dạng Markdown.\n" +
                "2. Khi nhắc đến bất kỳ mã môn học nào (ví dụ: SE104, MA006), hãy viết HOA ĐÚNG mã môn để giao diện người dùng tự động render thành thẻ liên kết.\n" +
                "3. Luôn dẫn link các đồ án mẫu hoặc tài liệu được cung cấp trong ngữ cảnh để sinh viên truy cập.\n" +
                "4. Không bịa đặt mã môn học hoặc thông tin điểm số nằm ngoài ngữ cảnh.\n";

        // 6. Generate Response from OpenCode Go
        String answer = openCodeAiService.generateCompletion(systemPrompt, userMessage);

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
}
