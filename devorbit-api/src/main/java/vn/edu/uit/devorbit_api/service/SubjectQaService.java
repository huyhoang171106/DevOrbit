package vn.edu.uit.devorbit_api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import vn.edu.uit.devorbit_api.constant.CurriculumConstants;
import vn.edu.uit.devorbit_api.dto.publicapi.*;
import vn.edu.uit.devorbit_api.entity.*;
import vn.edu.uit.devorbit_api.repository.*;
import vn.edu.uit.devorbit_api.service.ai.CrawlerService;
import vn.edu.uit.devorbit_api.service.ai.OpenCodeAiService;
import vn.edu.uit.devorbit_api.service.ai.WebSearchService;
import vn.edu.uit.devorbit_api.service.knowledge.CourseKnowledgeBootstrapService;
import vn.edu.uit.devorbit_api.service.knowledge.FirecrawlClient;
import vn.edu.uit.devorbit_api.service.knowledge.KnowledgeRetrievalService;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service orchestrating AI Q&A operations for course selection and advice.
 * Resolves intents, fetches DB contexts, scrapes web sites, and calls OpenCode Go.
 */
@Service
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
    private final Executor subjectQaStreamExecutor;

    private static final Logger log = LoggerFactory.getLogger(SubjectQaService.class);
    private static final Pattern COURSE_CODE_PATTERN = Pattern.compile("\\b([A-Z]{2,4}\\d{3,4})\\b");

    public SubjectQaService(
            CourseRepository courseRepository,
            GithubRepoRepository githubRepoRepository,
            ChatSessionRepository chatSessionRepository,
            ChatMessageRepository chatMessageRepository,
            WebSearchService webSearchService,
            CrawlerService crawlerService,
            OpenCodeAiService openCodeAiService,
            FirecrawlClient firecrawlClient,
            CourseKnowledgeBootstrapService courseKnowledgeBootstrapService,
            KnowledgeRetrievalService knowledgeRetrievalService,
            ObjectMapper objectMapper,
            @Qualifier("subjectQaStreamExecutor") Executor subjectQaStreamExecutor) {
        this.courseRepository = courseRepository;
        this.githubRepoRepository = githubRepoRepository;
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.webSearchService = webSearchService;
        this.crawlerService = crawlerService;
        this.openCodeAiService = openCodeAiService;
        this.firecrawlClient = firecrawlClient;
        this.courseKnowledgeBootstrapService = courseKnowledgeBootstrapService;
        this.knowledgeRetrievalService = knowledgeRetrievalService;
        this.objectMapper = objectMapper;
        this.subjectQaStreamExecutor = subjectQaStreamExecutor;
    }

    // ─── Progress Sink ───

    @FunctionalInterface
    public interface SubjectQaProgressSink {
        void emit(SubjectQaStreamEvent event);

        SubjectQaProgressSink NOOP = event -> {};
    }

    // ─── Preparation result ───

    public record SubjectQaPreparation(
        String userMessage,
        UUID sessionId,
        ChatSession session,
        SubjectQaResponse directResponse,
        List<Long> relevantNodeIds,
        Set<String> sources,
        List<WebSearchResponse.WebSearchResult> searchResults,
        String queryType,
        String systemPrompt,
        String fallbackSystemPrompt
    ) {}

    // ─── Session ───

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

    // ─── One-shot query (existing contract) ───

    public SubjectQaResponse processQuery(SubjectQaRequest request) {
        SubjectQaPreparation preparation = prepareQuery(request, SubjectQaProgressSink.NOOP);

        if (preparation.directResponse() != null) {
            return preparation.directResponse();
        }

        // Generate LLM response
        String answer = openCodeAiService.generateCompletion(preparation.systemPrompt(), preparation.userMessage());
        if (answer == null || answer.isBlank()) {
            log.warn("SubjectQaService: LLM returned blank answer, retrying with compact grounded prompt");
            answer = openCodeAiService.generateCompletion(
                preparation.fallbackSystemPrompt(),
                preparation.userMessage()
            );
        }
        if (answer == null || answer.isBlank()) {
            answer = "Mình đã lấy được ngữ cảnh thật từ DevOrbit nhưng LLM không trả về nội dung trả lời. "
                + "Mình sẽ không bịa thêm thông tin. Bạn hãy thử hỏi lại với mã môn cụ thể hoặc câu hỏi hẹp hơn.";
        }

        saveAiResponseBestEffort(preparation.session(), answer, preparation.sources());

        return new SubjectQaResponse(
            answer,
            preparation.sessionId() != null ? preparation.sessionId() : UUID.randomUUID(),
            preparation.relevantNodeIds(),
            new ArrayList<>(preparation.sources()),
            preparation.queryType(),
            List.copyOf(preparation.searchResults())
        );
    }

    // ─── Streaming query ───

    public SseEmitter streamQuery(SubjectQaRequest request) {
        SseEmitter emitter = new SseEmitter(120_000L);

        subjectQaStreamExecutor.execute(() -> {
            StringBuilder answerBuffer = new StringBuilder();
            try {
                SubjectQaPreparation preparation = prepareQuery(request, event -> emit(emitter, event));

                // Direct response (greeting, needs-course-code) → emit as single delta
                if (preparation.directResponse() != null) {
                    emit(emitter, SubjectQaStreamEvent.status("answer", "Đang soạn câu trả lời"));
                    emit(emitter, SubjectQaStreamEvent.delta(preparation.directResponse().answer()));
                    emit(emitter, SubjectQaStreamEvent.complete(preparation.directResponse()));
                    emitter.complete();
                    return;
                }

                emit(emitter, SubjectQaStreamEvent.status("answer", "Đang soạn câu trả lời"));

                // Stream from LLM
                openCodeAiService.streamCompletion(preparation.systemPrompt(), preparation.userMessage())
                    .subscribe(
                        delta -> {
                            answerBuffer.append(delta);
                            emit(emitter, SubjectQaStreamEvent.delta(delta));
                        },
                        error -> {
                            log.error("SubjectQaService: streaming error: {}", error.getMessage());
                            // If no deltas emitted yet, fall back to one-shot
                            if (answerBuffer.isEmpty()) {
                                String fallbackAnswer = openCodeAiService.generateCompletion(
                                    preparation.systemPrompt(), preparation.userMessage()
                                );
                                if (fallbackAnswer == null || fallbackAnswer.isBlank()) {
                                    fallbackAnswer = openCodeAiService.generateCompletion(
                                        preparation.fallbackSystemPrompt(), preparation.userMessage()
                                    );
                                }
                                if (fallbackAnswer == null || fallbackAnswer.isBlank()) {
                                    fallbackAnswer = "Mình đã lấy được ngữ cảnh thật từ DevOrbit nhưng LLM không trả về nội dung trả lời. "
                                        + "Mình sẽ không bịa thêm thông tin. Bạn hãy thử hỏi lại với mã môn cụ thể hoặc câu hỏi hẹp hơn.";
                                }
                                answerBuffer.append(fallbackAnswer);
                                emit(emitter, SubjectQaStreamEvent.delta(fallbackAnswer));
                            }
                            completeStream(emitter, preparation, answerBuffer.toString());
                        },
                        () -> {
                            // Stream completed normally
                            String finalAnswer = answerBuffer.toString();
                            if (finalAnswer.isBlank()) {
                                // LLM returned no deltas, fall back to one-shot
                                log.warn("SubjectQaService: streaming returned blank, falling back to one-shot");
                                finalAnswer = openCodeAiService.generateCompletion(
                                    preparation.systemPrompt(), preparation.userMessage()
                                );
                                if (finalAnswer == null || finalAnswer.isBlank()) {
                                    finalAnswer = openCodeAiService.generateCompletion(
                                        preparation.fallbackSystemPrompt(), preparation.userMessage()
                                    );
                                }
                                if (finalAnswer == null || finalAnswer.isBlank()) {
                                    finalAnswer = "Mình đã lấy được ngữ cảnh thật từ DevOrbit nhưng LLM không trả về nội dung trả lời. "
                                        + "Mình sẽ không bịa thêm thông tin. Bạn hãy thử hỏi lại với mã môn cụ thể hoặc câu hỏi hẹp hơn.";
                                }
                                emit(emitter, SubjectQaStreamEvent.delta(finalAnswer));
                                answerBuffer.setLength(0);
                                answerBuffer.append(finalAnswer);
                            }
                            completeStream(emitter, preparation, answerBuffer.toString());
                        }
                    );
            } catch (Exception e) {
                log.error("SubjectQaService: stream query failed: {}", e.getMessage());
                try {
                    emit(emitter, SubjectQaStreamEvent.error("Không thể stream câu trả lời. Vui lòng thử lại."));
                } catch (Exception ignored) {
                    // Emitter may already be closed
                }
                emitter.completeWithError(e);
            }
        });

        emitter.onTimeout(() -> {
            try {
                emit(emitter, SubjectQaStreamEvent.error("Stream timeout. Vui lòng thử lại."));
            } catch (Exception ignored) {}
            emitter.complete();
        });

        return emitter;
    }

    private void completeStream(SseEmitter emitter, SubjectQaPreparation preparation, String finalAnswer) {
        saveAiResponseBestEffort(preparation.session(), finalAnswer, preparation.sources());

        SubjectQaResponse response = new SubjectQaResponse(
            finalAnswer,
            preparation.sessionId() != null ? preparation.sessionId() : UUID.randomUUID(),
            preparation.relevantNodeIds(),
            new ArrayList<>(preparation.sources()),
            preparation.queryType(),
            List.copyOf(preparation.searchResults())
        );
        emit(emitter, SubjectQaStreamEvent.complete(response));
        emitter.complete();
    }

    private void emit(SseEmitter emitter, SubjectQaStreamEvent event) {
        try {
            emitter.send(SseEmitter.event().name(event.type()).data(event));
        } catch (IOException e) {
            throw new RuntimeException("Failed to send SSE event: " + e.getMessage(), e);
        }
    }

    // ─── Shared preparation (both one-shot and streaming) ───

    SubjectQaPreparation prepareQuery(SubjectQaRequest request, SubjectQaProgressSink sink) {
        String userMessage = request.message();
        UUID sessionId = request.sessionId();

        sink.emit(SubjectQaStreamEvent.status("session", "Đang mở phiên chat"));

        // 1. Resolve or Create Chat Session
        ChatSession session = null;
        try {
            session = resolveSession(sessionId, userMessage);
            sessionId = session.getId();

            ChatMessage studentMsg = ChatMessage.builder()
                .session(session)
                .sender("STUDENT")
                .content(userMessage)
                .build();
            chatMessageRepository.save(studentMsg);
        } catch (Exception e) {
            log.warn("SubjectQaService: DB error during session setup, continuing without persistence: {}", e.getMessage());
        }

        // 2. Scan for Course Codes in User Query
        Set<String> detectedCodes = new HashSet<>();
        Matcher matcher = COURSE_CODE_PATTERN.matcher(userMessage.toUpperCase());
        while (matcher.find()) {
            detectedCodes.add(matcher.group(1));
        }

        List<String> sortedCodes = detectedCodes.stream().sorted().toList();
        sink.emit(SubjectQaStreamEvent.status("analyze",
            sortedCodes.isEmpty() ? "Đang phân tích câu hỏi" : "Tìm thấy mã môn: " + String.join(", ", sortedCodes)));

        // Greeting check
        if (detectedCodes.isEmpty() && isGreeting(userMessage)) {
            SubjectQaResponse direct = new SubjectQaResponse(
                buildGroundedGreeting(),
                sessionId != null ? sessionId : UUID.randomUUID(),
                List.of(),
                List.of(),
                "DIRECT",
                List.of()
            );
            return new SubjectQaPreparation(userMessage, sessionId, session, direct, List.of(), Set.of(), List.of(), "DIRECT", null, null);
        }

        // Career/course orientation without a concrete course code
        if (detectedCodes.isEmpty() && asksForCareerCourseAdvice(userMessage)) {
            SubjectQaResponse direct = new SubjectQaResponse(
                buildCareerCourseAdviceResponse(userMessage),
                sessionId != null ? sessionId : UUID.randomUUID(),
                List.of(),
                List.of(),
                "DIRECT",
                List.of()
            );
            return new SubjectQaPreparation(userMessage, sessionId, session, direct, List.of(), Set.of(), List.of(), "DIRECT", null, null);
        }

        // First-year curriculum questions without a concrete course code
        if (detectedCodes.isEmpty() && asksForFirstYearCurriculum(userMessage)) {
            SubjectQaResponse direct = new SubjectQaResponse(
                buildFirstYearCurriculumResponse(),
                sessionId != null ? sessionId : UUID.randomUUID(),
                List.of(),
                List.of(),
                "DIRECT",
                List.of()
            );
            return new SubjectQaPreparation(userMessage, sessionId, session, direct, List.of(), Set.of(), List.of(), "DIRECT", null, null);
        }

        // Internal resources without course code
        if (detectedCodes.isEmpty() && asksForInternalResources(userMessage)) {
            SubjectQaResponse direct = new SubjectQaResponse(
                buildNeedsCourseCodeResponse(),
                sessionId != null ? sessionId : UUID.randomUUID(),
                List.of(),
                List.of(),
                "DIRECT",
                List.of()
            );
            return new SubjectQaPreparation(userMessage, sessionId, session, direct, List.of(), Set.of(), List.of(), "DIRECT", null, null);
        }

        // 3. Build DB Context
        List<Long> relevantNodeIds = new ArrayList<>();
        StringBuilder dbContext = new StringBuilder();

        sink.emit(SubjectQaStreamEvent.status("devorbit_context", "Đang kiểm tra dữ liệu DevOrbit"));

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

        // 4. Determine if web search is needed
        String normalizedIntent = normalizeForIntent(userMessage);
        boolean needsSearch = normalizedIntent.contains("lam sao") ||
                              normalizedIntent.contains("hoc tot") ||
                              normalizedIntent.contains("kinh nghiem") ||
                              normalizedIntent.contains("tai lieu") ||
                              normalizedIntent.contains("de cuong") ||
                              normalizedIntent.contains("de thi") ||
                              normalizedIntent.contains("giao trinh") ||
                              normalizedIntent.contains("tutorial") ||
                              normalizedIntent.contains("how to") ||
                              normalizedIntent.contains("project") ||
                              normalizedIntent.contains("repo") ||
                              normalizedIntent.contains("github") ||
                              normalizedIntent.contains("uit") ||
                              normalizedIntent.contains("nganh") ||
                              normalizedIntent.contains("chuyen nganh") ||
                              normalizedIntent.contains("dao tao") ||
                              normalizedIntent.contains("hoc phi") ||
                              normalizedIntent.contains("diem chuan") ||
                              normalizedIntent.contains("xet tuyen");

        // 5. Build semantic context (local RAG)
        sink.emit(SubjectQaStreamEvent.status("rag", "Đang tìm trong Knowledge RAG"));
        SemanticKnowledgeContext semanticContext = buildSemanticKnowledgeContext(userMessage, detectedCodes);

        // 6. Adaptive web
        boolean shouldUseWeb = needsSearch || (!semanticContext.hasChunks() && !detectedCodes.isEmpty());

        Set<String> sources = new LinkedHashSet<>();
        List<WebSearchResponse.WebSearchResult> searchResults = List.of();
        StringBuilder webContext = new StringBuilder();
        String queryType = "DIRECT";

        if (shouldUseWeb) {
            queryType = "SEARCH";
            sink.emit(SubjectQaStreamEvent.status("web_search", "Đang tìm nguồn web liên quan"));
            try {
                WebSearchResponse searchResponse = webSearchService.search(userMessage);
                if (searchResponse != null && searchResponse.web() != null) {
                    searchResults = searchResponse.web().stream()
                        .filter(java.util.Objects::nonNull)
                        .limit(5)
                        .collect(Collectors.toList());

                    if (semanticContext.hasChunks()) {
                        webContext.append("Nguồn web này được dùng để bổ sung, không thay thế Knowledge RAG nội bộ.\n");
                    }

                    boolean needsDetailedWebDocs = needsDetailedWebDocs(userMessage);
                    int scrapedCount = 0;
                    for (WebSearchResponse.WebSearchResult result : searchResults) {
                        if (result.url() == null || result.url().isBlank()) {
                            continue;
                        }

                        sources.add(result.url());
                        appendSearchResultContext(webContext, result);

                        // Emit search_result immediately after adding to context
                        sink.emit(SubjectQaStreamEvent.searchResult(result));

                        if (scrapedCount < 2 && shouldScrapeWebResult(result, needsDetailedWebDocs)) {
                            String host = extractHost(result.url());
                            sink.emit(SubjectQaStreamEvent.status("web_read", "Đang đọc nguồn web: " + host));

                            String scrapedText = scrapeWithFirecrawl(result.url());
                            if (scrapedText != null && !scrapedText.isBlank()) {
                                webContext.append(String.format("--- Firecrawl mở rộng nguồn: %s ---\n", result.url()));
                                webContext.append(scrapedText).append("\n\n");
                            }
                            scrapedCount++;
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("SubjectQaService: web search/crawl failed, continuing without web context: {}", e.getMessage());
            }
        }

        String ragContext = semanticContext.text();

        // 7. Build prompts
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

        String fallbackSystemPrompt = buildCompactSystemPrompt(dbContext, ragContext, webContext);

        return new SubjectQaPreparation(
            userMessage, sessionId, session, null,
            relevantNodeIds, sources, searchResults, queryType,
            systemPrompt, fallbackSystemPrompt
        );
    }

    // ─── Save AI response (best-effort) ───

    void saveAiResponseBestEffort(ChatSession session, String answer, Set<String> sources) {
        if (session == null) {
            return;
        }
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

    // ─── Helpers ───

    private String extractHost(String url) {
        try {
            return URI.create(url).getHost();
        } catch (Exception e) {
            return url;
        }
    }

    private boolean isGreeting(String message) {
        String normalized = normalizeForIntent(message);
        if (normalized.isEmpty()) {
            return true;
        }
        Set<String> tokens = Arrays.stream(normalized.split(" "))
            .filter(token -> !token.isBlank())
            .collect(Collectors.toSet());
        return normalized.equals("xin chao") ||
            normalized.equals("chao ban") ||
            tokens.contains("chao") ||
            tokens.contains("hello") ||
            tokens.contains("helo") ||
            tokens.contains("hi");
    }

    private SemanticKnowledgeContext buildSemanticKnowledgeContext(String userMessage, Set<String> detectedCodes) {
        StringBuilder context = new StringBuilder();
        double bestScore = 0.0;
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
                    if (chunkResult.score() > bestScore) {
                        bestScore = chunkResult.score();
                    }
                }
            } catch (Exception e) {
                log.warn("SubjectQaService: semantic retrieval failed for course {}: {}", courseCode, e.getMessage());
            }
        }

        boolean hasChunks = context.length() > 0;
        String text;
        if (!hasChunks) {
            text = "(Không tìm thấy chunk Knowledge RAG phù hợp hoặc embedding/search chưa sẵn sàng.)";
        } else {
            text = context.toString();
        }

        return new SemanticKnowledgeContext(text, hasChunks, bestScore);
    }

    private record SemanticKnowledgeContext(String text, boolean hasChunks, double bestScore) {}

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
            + "Web/Exa/Firecrawl context:\n" + trimForPrompt(webContext.toString(), 2500);
    }

    private boolean needsDetailedWebDocs(String message) {
        String normalized = normalizeForIntent(message);
        return normalized.contains("tai lieu") ||
            normalized.contains("de cuong") ||
            normalized.contains("de thi") ||
            normalized.contains("giao trinh") ||
            normalized.contains("slide") ||
            normalized.contains("slides") ||
            normalized.contains("pdf") ||
            normalized.contains("bai tap") ||
            normalized.contains("tutorial") ||
            normalized.contains("how to") ||
            normalized.contains("project") ||
            normalized.contains("repo") ||
            normalized.contains("github");
    }

    private boolean shouldScrapeWebResult(WebSearchResponse.WebSearchResult result, boolean needsDetailedWebDocs) {
        if (result == null || result.url() == null || result.url().isBlank()) {
            return false;
        }

        if (needsDetailedWebDocs) {
            return true;
        }

        if (!result.hasHighlights()) {
            return true;
        }

        int highlightChars = result.highlights().stream()
            .filter(java.util.Objects::nonNull)
            .mapToInt(String::length)
            .sum();
        return highlightChars < 160;
    }

    private void appendSearchResultContext(StringBuilder webContext, WebSearchResponse.WebSearchResult result) {
        webContext.append(String.format("--- Nguồn từ internet: %s", result.url()));
        if (result.title() != null && !result.title().isBlank()) {
            webContext.append(String.format(" (Tiêu đề: %s)", result.title()));
        }
        webContext.append(" ---\n");

        if (result.sourceProvider() != null && !result.sourceProvider().isBlank()) {
            webContext.append(String.format("- Provider: %s\n", result.sourceProvider()));
        }
        if (result.author() != null && !result.author().isBlank()) {
            webContext.append(String.format("- Tác giả: %s\n", result.author()));
        }
        if (result.publishedDate() != null && !result.publishedDate().isBlank()) {
            webContext.append(String.format("- Ngày xuất bản: %s\n", result.publishedDate()));
        }
        if (result.description() != null && !result.description().isBlank()) {
            webContext.append(String.format("- Mô tả: %s\n", trimForPrompt(result.description(), 700)));
        }
        if (result.highlights() != null && !result.highlights().isEmpty()) {
            webContext.append("- Highlights:\n");
            for (String highlight : result.highlights()) {
                if (highlight == null || highlight.isBlank()) {
                    continue;
                }
                webContext.append(String.format("  * %s\n", trimForPrompt(highlight, 700)));
            }
        }
        webContext.append('\n');
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

    private boolean asksForCareerCourseAdvice(String message) {
        String normalized = normalizeForIntent(message);
        boolean asksCourseChoice = normalized.contains("nen hoc mon gi") ||
            normalized.contains("hoc mon gi") ||
            normalized.contains("chon mon") ||
            normalized.contains("mon nao") ||
            normalized.contains("lo trinh") ||
            normalized.contains("dinh huong") ||
            normalized.contains("hoc nhu nao") ||
            normalized.contains("hoc the nao") ||
            normalized.contains("hoc ra sao") ||
            normalized.contains("hoc lam sao");
        boolean mentionsCareer = normalized.contains("backend") ||
            normalized.contains("back end") ||
            normalized.contains("java") ||
            normalized.contains("spring") ||
            normalized.contains("developer") ||
            normalized.contains("dev") ||
            normalized.contains("lap trinh vien");
        return asksCourseChoice && mentionsCareer;
    }

    private boolean asksForFirstYearCurriculum(String message) {
        String normalized = normalizeForIntent(message);
        return normalized.contains("nam 1") ||
            normalized.contains("nam nhat") ||
            normalized.contains("hoc ki 1") ||
            normalized.contains("hoc ky 1") ||
            normalized.contains("hk1") ||
            normalized.contains("hk 1") ||
            normalized.contains("sem 1") ||
            normalized.contains("semester 1") ||
            normalized.contains("dau nam") ||
            normalized.contains("nam dau") ||
            normalized.contains("nam thu nhat");
    }

    private String normalizeForIntent(String message) {
        if (message == null) {
            return "";
        }
        return java.text.Normalizer.normalize(message, java.text.Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .replace('\u0111', 'd')
            .replace('\u0110', 'D')
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

    private String buildCareerCourseAdviceResponse(String message) {
        String normalized = normalizeForIntent(message);
        if (normalized.contains("java") || normalized.contains("spring") || normalized.contains("backend") || normalized.contains("back end")) {
            return "Nếu mục tiêu của bạn là **Java backend dev**, nên ưu tiên các môn có thật trong dữ liệu DevOrbit theo thứ tự này:\n\n"
                + "1. **IT001** - Nhập môn Lập trình: nền tảng tư duy lập trình.\n"
                + "2. **IT002** - Lập trình hướng đối tượng: rất quan trọng trước khi học Java/Spring nghiêm túc.\n"
                + "3. **IT003** - Cấu trúc dữ liệu và giải thuật: cần cho code backend, phỏng vấn và tối ưu xử lý.\n"
                + "4. **IT004** - Cơ sở dữ liệu: backend luôn phải làm việc với SQL, schema, transaction và truy vấn.\n"
                + "5. **IT005** - Nhập môn mạng máy tính: giúp hiểu HTTP, client-server, API và triển khai service.\n"
                + "6. **SE104** - Nhập môn Công nghệ phần mềm: học cách làm phần mềm có quy trình, yêu cầu, thiết kế và kiểm thử.\n"
                + "7. **SE330** - Ngôn ngữ lập trình Java: môn sát nhất với mục tiêu Java.\n"
                + "8. **SE325** - Chuyên đề J2EE: phù hợp để đi sâu Java enterprise/backend.\n"
                + "9. **SE347** - Công nghệ Web và ứng dụng: giúp nối backend với web app thực tế.\n"
                + "10. **SE332** - Chuyên đề CSDL nâng cao: nên học nếu muốn backend mạnh về dữ liệu.\n"
                + "11. **SE356** - Kiến trúc phần mềm: hữu ích khi lên mức thiết kế hệ thống.\n\n"
                + "Bạn có thể hỏi tiếp từng mã môn, ví dụ **SE330 học gì?** hoặc **SE325 có repo nào không?**, mình sẽ tra dữ liệu chi tiết trong DevOrbit.";
        }
        return "Bạn đang hỏi theo định hướng nghề nghiệp, nhưng mình cần biết rõ hướng bạn muốn theo để gợi ý môn chính xác hơn. "
            + "Ví dụ: Java backend, frontend, mobile, game, AI/data, DevOps hoặc security.";
    }
    private String buildFirstYearCurriculumResponse() {
        StringBuilder response = new StringBuilder();
        response.append("Nếu bạn đang hỏi **KTPM UIT (khóa 20-2025)**, năm 1 gồm hai học kỳ nền tảng:\n\n");
        appendCurriculumSemester(response, 1, "HK1");
        response.append('\n');
        appendCurriculumSemester(response, 2, "HK2");
        response.append("\nNếu bạn chưa rõ ngành, mình có thể tra tiếp theo hướng bạn muốn theo như backend, frontend, mobile, AI/data, DevOps, security hoặc game.");
        return response.toString();
    }

    private void appendCurriculumSemester(StringBuilder response, int semester, String heading) {
        response.append("### ").append(heading).append('\n');
        List<String> courseCodes = CurriculumConstants.CURRICULUM_SEMESTERS.entrySet().stream()
            .filter(entry -> entry.getValue() != null && entry.getValue() == semester)
            .sorted(Map.Entry.comparingByKey())
            .map(Map.Entry::getKey)
            .toList();

        if (courseCodes.isEmpty()) {
            response.append("- Chưa có dữ liệu.\n");
            return;
        }

        for (String code : courseCodes) {
            response.append("- `").append(code).append("`");
            Optional<Course> courseOpt = courseRepository.findByMaMH(code);
            if (courseOpt.isPresent()) {
                String name = courseOpt.get().getTenMH();
                if (name != null && !name.isBlank()) {
                    response.append(" - ").append(name);
                }
            }
            response.append('\n');
        }
    }
}
