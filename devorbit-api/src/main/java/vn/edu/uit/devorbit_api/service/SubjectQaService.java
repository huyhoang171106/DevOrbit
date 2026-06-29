package vn.edu.uit.devorbit_api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.constant.CurriculumConstants;
import vn.edu.uit.devorbit_api.dto.publicapi.*;
import vn.edu.uit.devorbit_api.entity.*;
import vn.edu.uit.devorbit_api.event.NotificationEvent;
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
    private final AiService aiService;
    private final FirecrawlClient firecrawlClient;
    private final CourseKnowledgeBootstrapService courseKnowledgeBootstrapService;
    private final KnowledgeRetrievalService knowledgeRetrievalService;
    private final ObjectMapper objectMapper;
    private final Executor subjectQaStreamExecutor;
    private final ApplicationEventPublisher eventPublisher;
    private final CourseRelationshipRepository courseRelationshipRepository;

    private static final Logger log = LoggerFactory.getLogger(SubjectQaService.class);
    private static final Pattern COURSE_CODE_PATTERN = Pattern.compile("\\b([A-Z]{2,4}\\d{2,4})\\b");

    // In-memory session summaries for cross-turn context
    private final Map<UUID, String> sessionSummaries = Collections.synchronizedMap(new LinkedHashMap<>());

    // Per-session web search counter for rate limiting
    private final Map<UUID, Integer> sessionSearchCounts = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final int MAX_SEARCHES_PER_SESSION = 3;

    // Flag for Fireworks embedding degradation
    private volatile boolean embeddingDegraded = false;
    private long embeddingDegradedSince = 0;
    private static final long EMBEDDING_DEGRADED_COOLDOWN_MS = 60_000; // reset after 1 min

    // Response cache: normalizedQuery → cached answer (bounded, expires after 15 min)
    private final Map<String, CachedQaResponse> responseCache = Collections.synchronizedMap(
        new LinkedHashMap<>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, CachedQaResponse> eldest) {
                return size() > 200; // max 200 entries
            }
        });

    private record CachedQaResponse(String answer, long timestamp) {
        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > 900_000; // 15 min
        }
    }

    public SubjectQaService(
            CourseRepository courseRepository,
            GithubRepoRepository githubRepoRepository,
            ChatSessionRepository chatSessionRepository,
            ChatMessageRepository chatMessageRepository,
            WebSearchService webSearchService,
            CrawlerService crawlerService,
            OpenCodeAiService openCodeAiService,
            AiService aiService,
            FirecrawlClient firecrawlClient,
            CourseKnowledgeBootstrapService courseKnowledgeBootstrapService,
            KnowledgeRetrievalService knowledgeRetrievalService,
            ObjectMapper objectMapper,
            @Qualifier("subjectQaStreamExecutor") Executor subjectQaStreamExecutor,
            ApplicationEventPublisher eventPublisher,
            CourseRelationshipRepository courseRelationshipRepository) {
        this.courseRepository = courseRepository;
        this.githubRepoRepository = githubRepoRepository;
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.webSearchService = webSearchService;
        this.crawlerService = crawlerService;
        this.openCodeAiService = openCodeAiService;
        this.aiService = aiService;
        this.firecrawlClient = firecrawlClient;
        this.courseKnowledgeBootstrapService = courseKnowledgeBootstrapService;
        this.knowledgeRetrievalService = knowledgeRetrievalService;
        this.objectMapper = objectMapper;
        this.subjectQaStreamExecutor = subjectQaStreamExecutor;
        this.eventPublisher = eventPublisher;
        this.courseRelationshipRepository = courseRelationshipRepository;
    }

    // Defer warmup to AFTER server accepts requests (was blocking startup for 45s)
    @org.springframework.context.event.EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    public void warmUpCaches() {
        log.info("SubjectQaService: starting deferred warmup (server already accepting requests)...");
        try {
            List<CourseSummaryResponse> popular = courseRepository.findAllWithRepoCountSortedByRepoCount();
            int indexed = 0;
            int limit = Math.min(popular.size(), 10);
            for (int i = 0; i < limit; i++) {
                String code = popular.get(i).code();
                try {
                    Course course = courseRepository.findByMaMH(code).orElse(null);
                    if (course != null) {
                        List<GithubRepo> repos = githubRepoRepository.findByCourseIdAndActiveTrue(course.getId());
                        courseKnowledgeBootstrapService.ensureCourseIndexed(course, repos);
                        indexed++;
                        // Delay 2s between requests to avoid Fireworks 429
                        if (i < limit - 1) {
                            Thread.sleep(2000);
                        }
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.info("SubjectQaService: warmup interrupted");
                    break;
                } catch (Exception e) {
                    log.warn("SubjectQaService: pre-index failed for {}, skipping: {}", code, e.getMessage());
                }
            }
            log.info("SubjectQaService: warmup complete ({} popular courses pre-indexed)", indexed);
        } catch (Exception e) {
            log.warn("SubjectQaService: warmup failed, caches will build on first request: {}", e.getMessage());
        }
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
            ChatSession saved = chatSessionRepository.save(session);
            eventPublisher.publishEvent(new NotificationEvent(
                "AI_CHAT",
                "Phiên chat AI mới: " + saved.getTitle(),
                "/admin/chat?sessionId=" + saved.getId()
            ));
            return saved;
        }
        return chatSessionRepository.findById(sessionId)
            .orElseGet(() -> {
                ChatSession newS = ChatSession.builder()
                    .title(userMessage.length() > 30 ? userMessage.substring(0, 27) + "..." : userMessage)
                    .build();
                ChatSession saved = chatSessionRepository.save(newS);
                eventPublisher.publishEvent(new NotificationEvent(
                    "AI_CHAT",
                    "Phiên chat AI mới: " + saved.getTitle(),
                "/admin/chat?sessionId=" + saved.getId()
                ));
                return saved;
            });
    }

    // ─── One-shot query (existing contract) ───

    public SubjectQaResponse processQuery(SubjectQaRequest request) {
        SubjectQaPreparation preparation = prepareQuery(request, SubjectQaProgressSink.NOOP);

        if (preparation.directResponse() != null) {
            SubjectQaResponse dr = preparation.directResponse();
            return new SubjectQaResponse(
                removeEmojis(dr.answer()),
                dr.sessionId(),
                dr.relevantNodeIds(),
                dr.sources(),
                dr.type(),
                dr.searchResults(),
                dr.roadmap(),
                dr.suggestedFollowUps(),
                dr.confidenceScore()
            );
        }

        // Check response cache before LLM call
        String cacheKey = buildCacheKey(request.message(), preparation.relevantNodeIds());
        CachedQaResponse cached = responseCache.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            log.info("SubjectQaService: cache HIT for '{}'", request.message());
            String answer = cached.answer();
            saveAiResponseBestEffort(preparation.session(), answer, preparation.sources());
            // Store session summary for cross-turn context
            Set<String> summaryCodes = new LinkedHashSet<>();
            if (preparation.relevantNodeIds() != null) {
                for (Long id : preparation.relevantNodeIds()) {
                    courseRepository.findById(id).ifPresent(c -> {
                        if (c.getMaMH() != null) summaryCodes.add(c.getMaMH());
                    });
                }
            }
            updateSessionSummary(preparation.sessionId(), preparation.userMessage(), answer, summaryCodes);
            List<String> followUps = buildSuggestedFollowUps(null, preparation.relevantNodeIds());
            double confidence = computeConfidenceScore(
                preparation.queryType(), preparation.relevantNodeIds(), preparation.sources(),
                null, null, null
            );
            return new SubjectQaResponse(
                answer,
                preparation.sessionId() != null ? preparation.sessionId() : UUID.randomUUID(),
                preparation.relevantNodeIds(),
                new ArrayList<>(preparation.sources()),
                preparation.queryType(),
                List.copyOf(preparation.searchResults()),
                null,
                followUps,
                confidence
            );
        }
        log.info("SubjectQaService: cache MISS for '{}'", request.message());

        // Quick fact mode: skip LLM for simple factual queries (credits, type, semester)
        String quickAnswer = tryQuickFact(request.message(), preparation.systemPrompt());
        if (quickAnswer != null) {
            log.info("SubjectQaService: quick fact HIT for '{}'", request.message());
            String answer = removeEmojis(quickAnswer);
            responseCache.put(cacheKey, new CachedQaResponse(answer, System.currentTimeMillis()));
            saveAiResponseBestEffort(preparation.session(), answer, preparation.sources());
            Set<String> summaryCodes = new LinkedHashSet<>();
            if (preparation.relevantNodeIds() != null) {
                for (Long id : preparation.relevantNodeIds()) {
                    courseRepository.findById(id).ifPresent(c -> {
                        if (c.getMaMH() != null) summaryCodes.add(c.getMaMH());
                    });
                }
            }
            updateSessionSummary(preparation.sessionId(), preparation.userMessage(), answer, summaryCodes);
            List<String> followUps = buildSuggestedFollowUps(null, preparation.relevantNodeIds());
            double confidence = computeConfidenceScore(
                preparation.queryType(), preparation.relevantNodeIds(), preparation.sources(),
                null, null, null
            );
            return new SubjectQaResponse(
                answer,
                preparation.sessionId() != null ? preparation.sessionId() : UUID.randomUUID(),
                preparation.relevantNodeIds(),
                new ArrayList<>(preparation.sources()),
                preparation.queryType(),
                List.copyOf(preparation.searchResults()),
                null,
                followUps,
                confidence
            );
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

        // Run self-critique on SEARCH/DIRECT responses to catch hallucination/issues
        // Only for queries with reasonable length (not greeting) and when answer is substantial
        if (!openCodeAiService.isOfflineFallbackResponse(answer)
                && ("SEARCH".equals(preparation.queryType()) || "DIRECT".equals(preparation.queryType()))) {
            try {
                String critiqueResult = runResponseCritique(request.message(), preparation.systemPrompt(), answer);
                if (critiqueResult != null) {
                    answer += "\n\n---\n" + critiqueResult;
                }
            } catch (Exception e) {
                log.warn("SubjectQaService: critique failed, using original response: {}", e.getMessage());
            }
        }

        // Clean up emojis from answer
        answer = removeEmojis(answer);

        // Store in response cache
        responseCache.put(cacheKey, new CachedQaResponse(answer, System.currentTimeMillis()));

        saveAiResponseBestEffort(preparation.session(), answer, preparation.sources());

        // Store session summary for cross-turn context
        Set<String> summaryCodes = new LinkedHashSet<>();
        if (preparation.relevantNodeIds() != null) {
            for (Long id : preparation.relevantNodeIds()) {
                courseRepository.findById(id).ifPresent(c -> {
                    if (c.getMaMH() != null) summaryCodes.add(c.getMaMH());
                });
            }
        }
        updateSessionSummary(preparation.sessionId(), preparation.userMessage(), answer, summaryCodes);

        List<String> followUps = buildSuggestedFollowUps(
            null, preparation.relevantNodeIds());
        double confidence = computeConfidenceScore(
            preparation.queryType(), preparation.relevantNodeIds(), preparation.sources(),
            preparation.systemPrompt(), null, null
        );
        return new SubjectQaResponse(
            answer,
            preparation.sessionId() != null ? preparation.sessionId() : UUID.randomUUID(),
            preparation.relevantNodeIds(),
            new ArrayList<>(preparation.sources()),
            preparation.queryType(),
            List.copyOf(preparation.searchResults()),
            null,
            followUps,
            confidence
        );
    }

    // ─── Streaming query ───

    public void streamQuery(SubjectQaRequest request, SubjectQaProgressSink sink) {
        try {
            SubjectQaPreparation preparation = prepareQuery(request, sink);

            if (preparation.directResponse() != null) {
                sink.emit(SubjectQaStreamEvent.status("answer", "Đang soạn câu trả lời"));
                sink.emit(SubjectQaStreamEvent.delta(removeEmojis(preparation.directResponse().answer())));
                sink.emit(SubjectQaStreamEvent.complete(preparation.directResponse()));
                return;
            }

            sink.emit(SubjectQaStreamEvent.status("answer", "Đang soạn câu trả lời"));

            // Real streaming: collect deltas from Flux and emit each one to client
            StringBuilder accumulated = new StringBuilder();
            try {
                openCodeAiService.streamCompletion(preparation.systemPrompt(), preparation.userMessage())
                    .doOnNext(delta -> {
                        accumulated.append(delta);
                        sink.emit(SubjectQaStreamEvent.delta(delta));
                    })
                    .doOnError(e -> {
                        log.error("SubjectQaService: stream completion error: {}", e.getMessage());
                    })
                    .blockLast();
            } catch (Exception e) {
                log.error("SubjectQaService: streamCompletion failed: {}", e.getMessage());
            }

            String finalAnswer = accumulated.toString();
            if (finalAnswer.isBlank()) {
                finalAnswer = "Mình đã lấy được ngữ cảnh thật từ DevOrbit nhưng LLM không trả về nội dung trả lời. "
                    + "Bạn hãy thử hỏi lại với mã môn cụ thể hoặc câu hỏi hẹp hơn.";
                sink.emit(SubjectQaStreamEvent.delta(finalAnswer));
            }

            finalAnswer = removeEmojis(finalAnswer);
            sink.emit(SubjectQaStreamEvent.complete(buildCompletedStreamResponse(preparation, finalAnswer)));
        } catch (Exception e) {
            log.error("SubjectQaService: stream query failed: {}", e.getMessage());
            sink.emit(SubjectQaStreamEvent.error("Không thể stream câu trả lời. Vui lòng thử lại."));
        }
    }

    private SubjectQaResponse buildCompletedStreamResponse(SubjectQaPreparation preparation, String finalAnswer) {
        saveAiResponseBestEffort(preparation.session(), finalAnswer, preparation.sources());

        // Store session summary for cross-turn context
        Set<String> summaryCodes = new LinkedHashSet<>();
        if (preparation.relevantNodeIds() != null) {
            for (Long id : preparation.relevantNodeIds()) {
                courseRepository.findById(id).ifPresent(c -> {
                    if (c.getMaMH() != null) summaryCodes.add(c.getMaMH());
                });
            }
        }
        updateSessionSummary(preparation.sessionId(), preparation.userMessage(), finalAnswer, summaryCodes);

        List<String> followUps = buildSuggestedFollowUps(null, preparation.relevantNodeIds());
        double confidence = computeConfidenceScore(
            preparation.queryType(), preparation.relevantNodeIds(), preparation.sources(),
            null, null, null
        );
        SubjectQaResponse response = new SubjectQaResponse(
            finalAnswer,
            preparation.sessionId() != null ? preparation.sessionId() : UUID.randomUUID(),
            preparation.relevantNodeIds(),
            new ArrayList<>(preparation.sources()),
            preparation.queryType(),
            List.copyOf(preparation.searchResults()),
            null,
            followUps,
            confidence
        );
        return response;
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
        // If no course code found via regex, try fuzzy matching against course names
        if (detectedCodes.isEmpty()) {
            String nameBasedCode = resolveCourseNameFromMessage(userMessage);
            if (nameBasedCode != null) {
                detectedCodes.add(nameBasedCode);
            }
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
                List.of(),
                null,
                null,
                null
            );
            return new SubjectQaPreparation(userMessage, sessionId, session, direct, List.of(), Set.of(), List.of(), "DIRECT", null, null);
        }

        // Roadmap / career goals without a concrete course code
        if (detectedCodes.isEmpty() && asksForRoadmap(userMessage)) {
            SubjectQaResponse direct = buildRoadmapResponse(userMessage, sessionId, List.of(), List.of());
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
                List.of(),
                null,
                null,
                null
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
                List.of(),
                null,
                null,
                null
            );
            return new SubjectQaPreparation(userMessage, sessionId, session, direct, List.of(), Set.of(), List.of(), "DIRECT", null, null);
        }

        // 3. Build DB Context
        List<Long> relevantNodeIds = new ArrayList<>();
        StringBuilder dbContext = new StringBuilder();
        sink.emit(SubjectQaStreamEvent.status("devorbit_context", "Đang kiểm tra dữ liệu DevOrbit"));

        // Batch fetch courses + repos (replaces N+1 per-code loop)
        if (!detectedCodes.isEmpty()) {
            try {
                long dbStart = System.currentTimeMillis();
                List<Course> courses = courseRepository.findByMaMHIn(new ArrayList<>(detectedCodes));
                Map<Long, List<GithubRepo>> reposByCourseId = githubRepoRepository
                    .findByCourseIdInAndActiveTrue(courses.stream().map(Course::getId).toList())
                    .stream()
                    .filter(r -> r.getCourse() != null)
                    .collect(java.util.stream.Collectors.groupingBy(r -> r.getCourse().getId()));
                log.info("SubjectQaService: batch DB fetch {} courses + {} repos in {}ms",
                    courses.size(), reposByCourseId.values().stream().mapToInt(List::size).sum(),
                    System.currentTimeMillis() - dbStart);

                for (Course course : courses) {
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

                    List<GithubRepo> repos = reposByCourseId.getOrDefault(course.getId(), List.of());
                    courseKnowledgeBootstrapService.ensureCourseIndexed(course, repos);
                    if (!repos.isEmpty()) {
                        dbContext.append("- Repository GitHub đã liên kết với môn học trên DevOrbit:\n");
                        for (GithubRepo repo : repos) {
                            dbContext.append(String.format("  * [%s](%s) - %s (Stars: %d)\n",
                                repo.getRepoName(), repo.getGithubUrl(), repo.getDescription(), repo.getStars()));
                        }
                    }
                    dbContext.append("\n");
                }
            } catch (Exception e) {
                log.warn("SubjectQaService: batch DB fetch failed, falling back: {}", e.getMessage());
            }
        }

        // 4. Determine if web search is needed
        String normalizedIntent = normalizeForIntent(userMessage);
        String questionType = classifyQuestionType(userMessage, normalizedIntent);
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

        // 5+6. Parallel RAG + Web search when intent requires web
        SemanticKnowledgeContext semanticContext;
        boolean shouldUseWeb = needsSearch;
        WebSearchResponse searchResponse = null;
        boolean parallelAttemptedWeb = false;

        if (needsSearch) {
            parallelAttemptedWeb = true;
            // PARALLEL: RAG + Web search concurrently via CompletableFuture
            long parallelStart = System.currentTimeMillis();
            log.info("SubjectQaService: parallel RAG + Web search for '{}'",
                userMessage.substring(0, Math.min(40, userMessage.length())));

            boolean skipRagInParallel = detectedCodes.isEmpty();
            java.util.concurrent.CompletableFuture<SemanticKnowledgeContext> ragFuture =
                skipRagInParallel
                    ? java.util.concurrent.CompletableFuture.completedFuture(
                        new SemanticKnowledgeContext("(skipped — no course codes)", false, 0.0))
                    : java.util.concurrent.CompletableFuture.supplyAsync(() -> {
                        sink.emit(SubjectQaStreamEvent.status("rag", "Đang tìm trong Knowledge RAG"));
                        return buildSemanticKnowledgeContext(userMessage, detectedCodes);
                    }, subjectQaStreamExecutor);

            java.util.concurrent.CompletableFuture<WebSearchResponse> webFuture =
                java.util.concurrent.CompletableFuture.supplyAsync(() -> {
                    sink.emit(SubjectQaStreamEvent.status("web_search", "Đang tìm nguồn web liên quan"));
                    try {
                        return webSearchService.search(userMessage);
                    } catch (Exception e) {
                        log.warn("SubjectQaService: parallel web search failed: {}", e.getMessage());
                        return null;
                    }
                }, subjectQaStreamExecutor);

            try {
                java.util.concurrent.CompletableFuture.allOf(ragFuture, webFuture).join();
                semanticContext = ragFuture.join();
                searchResponse = webFuture.join();
                long parallelMs = System.currentTimeMillis() - parallelStart;
                log.info("SubjectQaService: parallel RAG + Web completed in {}ms (RAG chunks={}, Web results={})",
                    parallelMs,
                    semanticContext.hasChunks(),
                    searchResponse != null && searchResponse.web() != null ? searchResponse.web().size() : 0);
            } catch (Exception e) {
                log.warn("SubjectQaService: parallel execution failed, falling back: {}", e.getMessage());
                semanticContext = new SemanticKnowledgeContext("(parallel execution failed)", false, 0.0);
            }
        } else {
            // SEQUENTIAL: RAG only, unless no codes and no search intent (lazy RAG)
            boolean shouldSkipRag = detectedCodes.isEmpty() && !needsSearch;
            if (shouldSkipRag) {
                log.info("SubjectQaService: lazy RAG — skipping (no codes, no search intent)");
                semanticContext = new SemanticKnowledgeContext("(skipped — no relevant course context for RAG)", false, 0.0);
            } else {
                sink.emit(SubjectQaStreamEvent.status("rag", "Đang tìm trong Knowledge RAG"));
                semanticContext = buildSemanticKnowledgeContext(userMessage, detectedCodes);
            }
        }

        // 6. Adaptive web (sequential path)
        if (!needsSearch) {
            shouldUseWeb = !semanticContext.hasChunks() && !detectedCodes.isEmpty();
        }

        // Rate limit: max 3 web searches per session
        if (shouldUseWeb && sessionId != null) {
            int current = sessionSearchCounts.getOrDefault(sessionId, 0);
            if (current >= MAX_SEARCHES_PER_SESSION) {
                log.info("SubjectQaService: session {} reached search limit ({}), skipping web search", sessionId, MAX_SEARCHES_PER_SESSION);
                shouldUseWeb = false;
            }
        }

        Set<String> sources = new LinkedHashSet<>();
        List<WebSearchResponse.WebSearchResult> searchResults = List.of();
        StringBuilder webContext = new StringBuilder();
        String queryType = "DIRECT";

        // Web search: parallel path already attempted, sequential path only if not tried yet
        if (shouldUseWeb && !parallelAttemptedWeb) {
            // Sequential path: fetch web search now
            sink.emit(SubjectQaStreamEvent.status("web_search", "Đang tìm nguồn web liên quan"));
            try {
                searchResponse = webSearchService.search(userMessage);
            } catch (Exception e) {
                log.warn("SubjectQaService: web search failed, continuing without web context: {}", e.getMessage());
            }
        }
        // Process web search results (both parallel and sequential paths)
        if (shouldUseWeb && searchResponse != null) {
            queryType = "SEARCH";
            if (searchResponse.web() != null) {
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
            // Increment session search counter
            if (sessionId != null) {
                sessionSearchCounts.merge(sessionId, 1, Integer::sum);
            }
        }

        // Set query type if web was attempted (even if results are empty)
        if (shouldUseWeb) {
            queryType = "SEARCH";
        }

        String ragContext = semanticContext.text();

        // 7a. Build data availability summary for gap detection
        StringBuilder availability = new StringBuilder();
        availability.append("=== TÌNH TRẠNG DỮ LIỆU (Data Availability) ===\n");
        boolean hasDb = dbContext.length() > 0;
        boolean hasRag = ragContext != null && !ragContext.isBlank()
            && !ragContext.contains("Không tìm thấy chunk");
        boolean hasWeb = webContext.length() > 0;
        // Check repos for detected courses
        boolean hasRepos = false;
        if (relevantNodeIds != null) {
            for (Long id : relevantNodeIds) {
                if (!githubRepoRepository.findByCourseIdAndActiveTrue(id).isEmpty()) {
                    hasRepos = true;
                    break;
                }
            }
        }
        // Check if embedding service is degraded
        boolean isEmbeddingDown = embeddingDegraded &&
            (System.currentTimeMillis() - embeddingDegradedSince) < EMBEDDING_DEGRADED_COOLDOWN_MS;
        if (!embeddingDegraded && hasRag) {
            // Check cooldown: reset flag after the cooldown period
            // (flag stays set until first request finds it working again)
        }
        if (!isEmbeddingDown && !hasRag && ragContext == null) {
            // RAG was never queried — reset degraded flag to try again
            embeddingDegraded = false;
        }

        availability.append("  + Thông tin môn học từ DB: ").append(hasDb ? "CÓ" : "KHÔNG CÓ").append("\n");
        availability.append("  + Repository GitHub: ").append(hasRepos ? "CÓ" : "KHÔNG CÓ").append("\n");
        availability.append("  + Knowledge RAG (tri thức nội bộ): ").append(hasRag ? "CÓ" : "KHÔNG CÓ");
        if (isEmbeddingDown && !hasRag) {
            availability.append(" (API embedding tạm thời gián đoạn, thử lại sau)");
        }
        availability.append("\n");
        availability.append("  + Kết quả tìm kiếm web: ").append(hasWeb ? "CÓ" : "KHÔNG CÓ").append("\n");
        availability.append("HƯỚNG DẪN: Nếu một loại dữ liệu KHÔNG CÓ, tuyệt đối không bịa đặt thông tin thuộc loại đó. ");
        availability.append("Ví dụ: nếu Repository GitHub KHÔNG CÓ, đừng nói 'có repo XYZ'. ");
        availability.append("Nếu không có dữ liệu nào (tất cả KHÔNG CÓ), hãy nói rõ DevOrbit không có dữ liệu và hỏi user mã môn cụ thể.\n");

        // Detect user year from message for adaptive depth
        String userYearInstruction = detectUserYear(userMessage, normalizedIntent);

        // Detect multi-part question structure for structured responses
        String multiPartInstruction = detectMultiPartQuery(userMessage);

        // 7. Build prompts
        String systemPrompt = "Bạn là Trợ lý Cố vấn Học tập thông minh tại hệ thống DevOrbit dành cho sinh viên trường Đại học Công nghệ Thông tin (UIT).\n" +
                "Nhiệm vụ của bạn là tư vấn môn học, giải đáp đề cương chi tiết khi DevOrbit có dữ liệu, và giới thiệu repository GitHub đã liên kết với môn học trên DevOrbit.\n\n" +
                "Dưới đây là thông tin chính xác từ hệ thống DevOrbit để làm ngữ cảnh trả lời (ƯU TIÊN TUYỆT ĐỐI):\n" +
                dbContext.toString() + "\n" +
                "Tri thức truy xuất bằng embedding từ kho Knowledge RAG của DevOrbit:\n" +
                ragContext + "\n" +
                "Thông tin bổ trợ thu thập từ các bài viết/diễn đàn (sử dụng để tư vấn kinh nghiệm học tập):\n" +
                webContext.toString() + "\n" +
                availability.toString() + "\n" +
                (userYearInstruction != null ? userYearInstruction + "\n" : "") +
                (multiPartInstruction != null ? multiPartInstruction + "\n" : "") +
                (questionType != null ? questionType + "\n" : "") +
                "Quy tắc khi viết câu trả lời:\n" +
                "1. Bắt đầu bằng 1-2 dòng tóm tắt ngắn (Tóm tắt: ...) để người đọc nắm ý chính ngay.\n" +
                "2. Trả lời bằng tiếng Việt chi tiết, cấu trúc rõ ràng, sử dụng định dạng Markdown.\n" +
                "3. Khi nhắc đến bất kỳ mã môn học nào (ví dụ: SE104, MA006), hãy viết HOA ĐÚNG mã môn để giao diện người dùng tự động render thành thẻ liên kết.\n" +
                "4. Chỉ dẫn link repository GitHub hoặc tài liệu thật sự xuất hiện trong ngữ cảnh để sinh viên truy cập.\n" +
                "5. Không bịa đặt mã môn học hoặc thông tin điểm số nằm ngoài ngữ cảnh.\n" +
                "6. Không tự nhận DevOrbit có ngân hàng đề thi, bài giảng, đồ án tiêu biểu, nhãn điểm A/A+, bộ lọc điểm số, hay mục tài nguyên học tập nếu các thứ đó không xuất hiện trong ngữ cảnh.\n" +
                "7. Nếu ngữ cảnh không có dữ liệu thật, hãy nói rõ DevOrbit hiện chỉ có thể tra cứu môn học, đề cương/tiêu chí đánh giá khi có trong DB, và repository GitHub đã liên kết theo môn; sau đó hỏi lại mã môn học hoặc repo cụ thể.\n";


        // Inject session memory for cross-turn continuity
        if (sessionId != null) {
            String prevSummary = sessionSummaries.get(sessionId);
            if (prevSummary != null) {
                systemPrompt += "\nGhi nhớ phiên trước: " + prevSummary + "\n";
            }
        }
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
                    context.append(String.format(Locale.ROOT,
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
                String msg = e.getMessage();
                if (msg != null && (msg.contains("429") || msg.contains("RATE_LIMIT") || msg.contains("rate limit"))) {
                    if (!embeddingDegraded) {
                        embeddingDegraded = true;
                        embeddingDegradedSince = System.currentTimeMillis();
                        log.warn("SubjectQaService: Fireworks embedding RATE LIMITED detected, degrading RAG");
                    }
                }
                log.warn("SubjectQaService: semantic retrieval failed for course {}: {}", courseCode, msg);
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


    /**
     * Try to infer a course code from the user message by matching against
     * course Vietnamese/English names (substring match on the normalized message).
     * Returns null when no course name could be matched.
     */
    private String resolveCourseNameFromMessage(String userMessage) {
        if (userMessage == null || userMessage.isBlank()) return null;
        String normalized = normalizeForIntent(userMessage);
        if (normalized.isEmpty()) return null;

        try {
            List<Course> allCourses = courseRepository.findAll();
            // Sort longest name first so "Hệ điều hành nâng cao" matches before "Hệ điều hành"
            allCourses.sort((a, b) -> {
                int la = a.getTenMH() != null ? a.getTenMH().length() : 0;
                int lb = b.getTenMH() != null ? b.getTenMH().length() : 0;
                return Integer.compare(lb, la);
            });
            for (Course course : allCourses) {
                if (course.getTenMH() != null && !course.getTenMH().isBlank()) {
                    String name = normalizeForIntent(course.getTenMH());
                    if (normalized.contains(name)) {
                        return course.getMaMH();
                    }
                }
                if (course.getTenMH_EN() != null && !course.getTenMH_EN().isBlank()) {
                    String nameEn = normalizeForIntent(course.getTenMH_EN());
                    if (normalized.contains(nameEn)) {
                        return course.getMaMH();
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error matching course name to code: {}", e.getMessage());
        }
        return null;
    }
    private boolean asksForInternalResources(String message) {
        String normalized = normalizeForIntent(message);
        return normalized.contains("do an") ||
            normalized.contains("project") ||
            normalized.contains("tai lieu") ||
            normalized.contains("on thi") ||
            normalized.contains("de thi");
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

    private boolean asksForRoadmap(String message) {
        String normalized = normalizeForIntent(message);
        if (asksForCareerCourseAdvice(message)) {
            return true;
        }

        boolean expressesGoal = normalized.contains("muon lam") ||
            normalized.contains("tro thanh") ||
            normalized.contains("muon theo") ||
            normalized.contains("muon co them ky nang") ||
            normalized.contains("them ky nang") ||
            normalized.contains("hoc them") ||
            normalized.contains("bo sung ky nang") ||
            normalized.contains("nang cap ky nang") ||
            normalized.contains("upskill") ||
            normalized.contains("reskill") ||
            normalized.contains("roadmap") ||
            normalized.contains("ke hoach hoc") ||
            normalized.contains("ke hoach phat trien");

        boolean mentionsTarget = normalized.contains("backend") ||
            normalized.contains("frontend") ||
            normalized.contains("mobile") ||
            normalized.contains("fullstack") ||
            normalized.equals("ai") ||
            normalized.startsWith("ai ") ||
            normalized.contains(" ai ") ||
            normalized.endsWith(" ai") ||
            normalized.contains("ai engineer") ||
            normalized.contains("artificial intelligence") ||
            normalized.contains("machine learning") ||
            normalized.contains("data engineer") ||
            normalized.contains("data science") ||
            normalized.contains("devops") ||
            normalized.contains("security") ||
            normalized.contains("game") ||
            normalized.contains("software engineer") ||
            normalized.contains("lap trinh vien");

        return expressesGoal && mentionsTarget;
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

    /**
     * Run a lightweight self-critique on the response to catch hallucinations,
     * contradictions, or missing context. Returns a correction note or null.
     */
    /**
     * Try to answer simple factual queries without calling the LLM.
     * Detects keywords like "tin chi", "loai", "may tin chi" and extracts
     * the answer directly from the system prompt (which contains DB context).
     * Returns null if the query is not a quick fact (falls through to LLM).
     */
    private String tryQuickFact(String userMessage, String systemPrompt) {
        if (userMessage == null || systemPrompt == null) return null;
        String normalized = normalizeForIntent(userMessage);
        if (normalized.isEmpty()) return null;

        // Must contain exactly one course code (detected via regex in the normalized msg)
        java.util.regex.Matcher codeMatcher = COURSE_CODE_PATTERN.matcher(userMessage.toUpperCase());
        if (!codeMatcher.find()) return null;
        String courseCode = codeMatcher.group(1);
        // If there are more codes, this is not simple
        if (codeMatcher.find()) return null;

        // Detect factual keywords
        boolean askCredits = normalized.contains("tin chi") || normalized.contains("may tc")
            || normalized.contains("so tc") || normalized.contains("may tin");
        boolean askType = normalized.contains("loai") || normalized.contains("loai mon");
        boolean askSemester = normalized.contains("hoc ky") || normalized.contains("semester");
        boolean askManagement = normalized.contains("quan ly") || normalized.contains("don vi");
        boolean askDescription = normalized.contains("tom tat") || normalized.contains("mieu ta")
            || normalized.contains("noi dung") || normalized.contains("description");
        boolean askObjectives = normalized.contains("muc tieu") || normalized.contains("muc dich");
        boolean askGrading = normalized.contains("danh gia") || normalized.contains("tinh diem")
            || normalized.contains("diem so") || normalized.contains("diem chu");
        boolean askTopics = normalized.contains("chu de") || normalized.contains("topic")
            || normalized.contains("noi dung chinh");

        if (!askCredits && !askType && !askSemester && !askManagement
            && !askDescription && !askObjectives && !askGrading && !askTopics) return null;

        // Try to extract from systemPrompt (which contains the DB context)
        // Format: "=== MÔN HỌC: mã (tên) ===" followed by "- Số tín chỉ: ..." etc.
        String[] lines = systemPrompt.split("\n");
        String credits = null, type = null, semester = null, mgmtUnit = null;
        String courseDesc = null, objectives = null, grading = null, topics = null;
        boolean inCourseBlock = false;
        for (String line : lines) {
            if (line.contains("=== MÔN HỌC:") && line.contains(courseCode)) {
                inCourseBlock = true;
                continue;
            }
            if (inCourseBlock && line.startsWith("===") && !line.contains(courseCode)) {
                break;
            }
            if (inCourseBlock) {
                if (line.contains("Số tín chỉ")) credits = line.trim();
                if (line.contains("Loại môn")) type = line.trim();
                if (line.contains("Semester")) semester = line.trim();
                if (line.contains("Đơn vị quản lý")) mgmtUnit = line.trim();
                if (line.contains("Tóm tắt:") || line.contains("Description:")) courseDesc = line.trim();
                if (line.contains("Mục tiêu")) objectives = line.trim();
                if (line.contains("đánh giá") || line.contains("tính điểm")) grading = line.trim();
                if (line.contains("chủ đề") || line.contains("Topics")) topics = line.trim();
            }
        }

        if (credits == null && type == null && semester == null
            && mgmtUnit == null && courseDesc == null && objectives == null
            && grading == null && topics == null) return null;

        // Ensure at least one ASKED field has data
        boolean hasAnyRequestedData =
            (askCredits && credits != null)
            || (askType && type != null)
            || (askSemester && semester != null)
            || (askManagement && mgmtUnit != null)
            || (askDescription && courseDesc != null)
            || (askObjectives && objectives != null)
            || (askGrading && grading != null)
            || (askTopics && topics != null);
        if (!hasAnyRequestedData) return null;

        StringBuilder sb = new StringBuilder();
        sb.append("**").append(courseCode).append("** — Thông tin nhanh:\n\n");
        if (askCredits && credits != null) sb.append(credits).append("\n");
        if (askType && type != null) sb.append(type).append("\n");
        if (askSemester && semester != null) sb.append(semester).append("\n");
        if (askManagement && mgmtUnit != null) sb.append(mgmtUnit).append("\n");
        if (askDescription && courseDesc != null) sb.append(courseDesc).append("\n");
        if (askObjectives && objectives != null) sb.append(objectives).append("\n");
        if (askGrading && grading != null) sb.append(grading).append("\n");
        if (askTopics && topics != null) sb.append(topics).append("\n");
        sb.append("\n(Dữ liệu từ DevOrbit — trả lời nhanh, không qua AI)");
        return sb.toString();
    }

    private String runResponseCritique(String userMessage, String systemPrompt, String response) {
        if (response == null || response.length() < 100) return null;
        if (response.length() > 3000) response = response.substring(0, 2997) + "...";
        if (systemPrompt.length() > 1500) {
            systemPrompt = systemPrompt.substring(0, 1497) + "...";
        }

        String critiquePrompt = "Bạn là người kiểm tra chất lượng câu trả lời. "
            + "Kiểm tra câu trả lời AI dưới đây dựa trên ngữ cảnh hệ thống.\n\n"
            + "=== NGỮ CẢNH HỆ THỐNG (Trích) ===\n"
            + systemPrompt + "\n\n"
            + "=== CÂU TRẢ LỜI CẦN KIỂM TRA ===\n"
            + response + "\n\n"
            + "=== NHIỆM VỤ ===\n"
            + "1. Câu trả lời có khẳng định thông tin KHÔNG CÓ trong ngữ cảnh không? (Hallucination)\n"
            + "2. Câu trả lời có bỏ sót thông tin quan trọng từ ngữ cảnh không?\n"
            + "3. Câu trả lời có đúng trọng tâm câu hỏi không?\n"
            + "4. Nếu có repository GitHub, câu trả lời có trích dẫn link không?\n\n"
            + "Chỉ trả lời bằng MỘT DÒNG: 'OK' nếu không có vấn đề, hoặc '📝 Lưu ý: [vấn đề ngắn gọn]' nếu cần bổ sung.";

        String critiqueResult = openCodeAiService.generateCompletion(critiquePrompt, "");
        if (critiqueResult != null && !critiqueResult.isBlank()
            && !critiqueResult.contains("OK")
            && critiqueResult.contains("Lưu ý")) {
            log.info("SubjectQaService: critique suggests improvement: {}", critiqueResult);
            return critiqueResult;
        }
        return null;
    }

    /**
     * Build a normalized cache key from the user message and course node IDs.
     * Same message + same courses = cache hit.
     */
    private String buildCacheKey(String message, List<Long> nodeIds) {
        String normalized = normalizeForIntent(message);
        if (nodeIds != null && !nodeIds.isEmpty()) {
            List<Long> sorted = new ArrayList<>(nodeIds);
            Collections.sort(sorted);
            return normalized + "::" + sorted.toString();
        }
        return normalized;
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

    /**
     * Build proactive follow-up suggestions based on course graph relationships.
     * Queries prerequisites, downstream courses, complementary courses, and repos.
     */
    private List<String> buildSuggestedFollowUps(Set<String> courseCodes, List<Long> nodeIds) {
        if ((courseCodes == null || courseCodes.isEmpty()) && (nodeIds == null || nodeIds.isEmpty())) {
            return List.of();
        }

        List<String> suggestions = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        // Resolve all Course entities from codes + IDs
        Set<Course> courses = new LinkedHashSet<>();
        if (courseCodes != null) {
            for (String code : courseCodes) {
                courseRepository.findByMaMH(code).ifPresent(courses::add);
            }
        }
        if (nodeIds != null) {
            for (Long id : nodeIds) {
                courseRepository.findById(id).ifPresent(courses::add);
            }
        }

        // If multiple courses found, suggest comparison (highest priority)
        if (courses.size() >= 2 && suggestions.size() < 3) {
            List<String> codes = courses.stream()
                .map(Course::getMaMH)
                .filter(c -> c != null && !c.isBlank())
                .limit(3)
                .toList();
            if (codes.size() >= 2) {
                suggestions.add("🔄 So sánh " + String.join(" với ", codes) + "?");
            }
        }

        for (Course course : courses) {
            String code = course.getMaMH();
            String ten = course.getTenMH();
            if (code == null || code.isBlank()) continue;

            // 1. Repos — GitHub repo (highest data availability)
            if (seen.add("repo_" + code) && suggestions.size() < 3) {
                List<GithubRepo> repos = githubRepoRepository.findByCourseIdAndActiveTrue(course.getId());
                if (!repos.isEmpty()) {
                    suggestions.add("💻 " + code + " có repository GitHub nào hay không?");
                }
            }

            // 2. Prerequisites từ DB
            if (seen.add("preq_" + code) && suggestions.size() < 3) {
                List<CourseRelationship> rels = courseRelationshipRepository
                    .findByCourseIdOrRelatedCourseIdOrderByCreatedAtAsc(course.getId(), course.getId());
                List<String> prereqs = rels.stream()
                    .filter(r -> r.getRelationType() == CourseRelationType.PREREQUISITE
                        && r.getCourse().getId().equals(course.getId()))
                    .map(r -> r.getRelatedCourse().getMaMH())
                    .filter(c -> c != null && !c.isBlank())
                    .toList();
                if (!prereqs.isEmpty()) {
                    suggestions.add("📚 " + code + " cần học những môn tiên quyết nào?");
                }
            }

            // 3. Downstream — môn học cần môn này
            if (seen.add("down_" + code) && suggestions.size() < 3) {
                List<Course> downstream = courseRepository.findDownstreamCourses(course.getId());
                if (!downstream.isEmpty()) {
                    suggestions.add("🔗 " + code + " là tiên quyết cho môn nào?");
                }
            }

            // 4. Complementary — môn liên quan
            if (seen.add("comp_" + code) && suggestions.size() < 3) {
                List<CourseRelationship> rels = courseRelationshipRepository
                    .findByCourseIdOrRelatedCourseIdOrderByCreatedAtAsc(course.getId(), course.getId());
                List<String> comps = rels.stream()
                    .filter(r -> r.getRelationType() == CourseRelationType.COMPLEMENTARY
                        || r.getRelationType() == CourseRelationType.COREQUISITE)
                    .map(r -> {
                        if (r.getCourse().getId().equals(course.getId()))
                            return r.getRelatedCourse().getMaMH();
                        return r.getCourse().getMaMH();
                    })
                    .filter(c -> c != null && !c.isBlank())
                    .limit(2)
                    .toList();
                if (!comps.isEmpty()) {
                    suggestions.add("🔄 So sánh " + code + " với " + String.join(", ", comps) + "?");
                }
            }

            // 5. Fallback: more diverse suggestions when graph data is sparse
            // Generates topic-based follow-ups as 2nd+ suggestion
            if (suggestions.size() == 1 && suggestions.size() < 3) {
                if (ten != null && !ten.isBlank()) {
                    String lower = ten.toLowerCase();
                    if (lower.contains("cấu trúc") || lower.contains("giải thuật") || lower.contains("data structure")) {
                        suggestions.add("📝 " + code + " học bằng ngôn ngữ nào tốt nhất?");
                    } else if (lower.contains("cơ sở") || lower.contains("database")) {
                        suggestions.add("🗄️ Học " + code + " cần cài công cụ gì?");
                    } else {
                        suggestions.add("📖 " + code + " có đề cương chi tiết không?");
                    }
                }
            }

            // 6. Generic fallback if still no follow-ups
            if (suggestions.isEmpty()) {
                suggestions.add("📖 " + code + " có đề cương chi tiết không?");
            }
        }

        // Cap at 3 and ensure diversity
        if (suggestions.size() > 3) {
            suggestions = suggestions.subList(0, 3);
        }
        return suggestions;
    }

    /**
     * Store a brief summary of what was discussed in this session
     * so the chatbot can reference it on the next user turn.
     */
    void updateSessionSummary(UUID sessionId, String userMessage, String answer, Set<String> detectedCodes) {
        if (sessionId == null) return;
        try {
            String codes = (detectedCodes != null && !detectedCodes.isEmpty())
                ? String.join(", ", detectedCodes)
                : "";
            String brief;
            if (!codes.isEmpty()) {
                // Truncate user message to first 80 chars for topic context
                String topic = userMessage.length() > 80
                    ? userMessage.substring(0, 77) + "..."
                    : userMessage;
                brief = "User hỏi về mã " + codes + " - " + topic.trim();
            } else {
                brief = "User hỏi: " + (userMessage.length() > 100
                    ? userMessage.substring(0, 97) + "..."
                    : userMessage);
            }
            if (brief.length() > 200) {
                brief = brief.substring(0, 197) + "...";
            }
            sessionSummaries.put(sessionId, brief);
            log.debug("Session summary updated for {}: {}", sessionId, brief);
        } catch (Exception e) {
            log.warn("Failed to update session summary: {}", e.getMessage());
        }
    }

    /**
     * Compute confidence score (0.0–1.0) based on data coverage.
     * Higher score = more data sources confirmed the answer.
     */
    private double computeConfidenceScore(
        String queryType, List<Long> nodeIds, Set<String> sources,
        String dbContext, String ragContext, StringBuilder webContext) {
        double score = 0.0;
        int factors = 0;

        // 1. Course data from DB (strongest signal)
        if (nodeIds != null && !nodeIds.isEmpty()) {
            score += 0.4;
        }
        factors++;

        // 2. Direct response (no web search needed) = confident in internal data
        if ("DIRECT".equals(queryType)) {
            score += 0.2;
        }
        factors++;

        // 3. Repos exist for found courses (looked up via relevantNodeIds)
        if (nodeIds != null && !nodeIds.isEmpty()) {
            boolean hasRepos = false;
            for (Long id : nodeIds) {
                if (!githubRepoRepository.findByCourseIdAndActiveTrue(id).isEmpty()) {
                    hasRepos = true;
                    break;
                }
            }
            if (hasRepos) score += 0.15;
        }
        factors++;

        // 4. RAG content available (embedding search returned real chunks)
        if (ragContext != null && !ragContext.isBlank()
            && !ragContext.contains("Không tìm thấy chunk")) {
            score += 0.15;
        }
        factors++;

        // 5. Web search found real results with URLs
        if (sources != null && !sources.isEmpty()) {
            score += 0.1;
        }
        factors++;

        return factors > 0 ? Math.min(1.0, score) : 0.5;
    }

    /**
     * Classify the user's question into a type for response structuring.
     * Injects a hint into the system prompt so LLM tailors its format.
     */
    private String classifyQuestionType(String rawMessage, String normalizedMsg) {
        if (normalizedMsg == null || normalizedMsg.isBlank()) return null;
        if (isGreeting(rawMessage)) return "📌 LOẠI CÂU HỎI: LỜI CHÀO. Hãy trả lời thân thiện, giới thiệu ngắn gọn khả năng của DevOrbit.";
        if (asksForRoadmap(rawMessage)) return "📌 LOẠI CÂU HỎI: ĐỊNH HƯỚNG / LỘ TRÌNH. Hãy đưa ra lộ trình học tập theo thứ tự ưu tiên, kèm mã môn cụ thể.";
        if (asksForFirstYearCurriculum(rawMessage)) return "📌 LOẠI CÂU HỎI: CHƯƠNG TRÌNH ĐÀO TẠO. Liệt kê các môn học theo học kỳ, kèm mã môn và số tín chỉ.";
        // COMPARISON: contains "so sanh", "vs", compare keywords
        String norm = normalizedMsg.toLowerCase();
        if (norm.contains("so sanh") || norm.contains(" vs ") || norm.contains("khac nhau")) {
            return "📌 LOẠI CÂU HỎI: SO SÁNH. Trả lời dạng bảng so sánh (| Môn | Số TC | Loại | ... |). Kết luận môn nào phù hợp hơn tùy mục tiêu.";
        }
        // MULTI-CODE detected: likely factual or comparison
        long codeCount = COURSE_CODE_PATTERN.matcher(rawMessage.toUpperCase()).results().count();
        if (codeCount >= 2) {
            return "📌 LOẠI CÂU HỎI: TRA CỨU NHIỀU MÔN. Trả lời từng môn riêng biệt, có thể kèm so sánh nếu phù hợp.";
        }
        if (codeCount == 1) {
            return "📌 LOẠI CÂU HỎI: TRA CỨU MÔN HỌC. Trả lời chi tiết thông tin môn học, repository, và hướng dẫn học tập.";
        }
        return null;
    }

    /**
     * Detect user year from message and return adaptive depth instruction.
     * Năm 1-2: focus on fundamentals. Năm 3-4: advanced topics, career.
     */
    private String detectUserYear(String rawMessage, String normalizedMessage) {
        if (normalizedMessage == null) return null;
        String msg = normalizedMessage.toLowerCase();
        int year = 0;

        // Match patterns: "nam 2", "nam 3", "nam 4", "năm hai", etc.
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("n[aă]m\\s*(\\d+|[h2b3b4b])")
            .matcher(msg);
        if (m.find()) {
            String val = m.group(1);
            switch (val) {
                case "2": year = 2; break;
                case "3": year = 3; break;
                case "4": year = 4; break;
                case "5": year = 5; break;
            }
        }

        // Also check raw message (user might write in English: "year 2")
        if (year == 0) {
            java.util.regex.Matcher m2 = java.util.regex.Pattern.compile("year\\s*(\\d+)")
                .matcher(rawMessage.toLowerCase());
            if (m2.find()) {
                try { year = Integer.parseInt(m2.group(1)); } catch (Exception e) {}
            }
        }

        if (year == 0) return null;

        if (year <= 2) {
            return "📌 SINH VIÊN NĂM " + year + " (mới): Hãy giải thích khái niệm cơ bản, tập trung vào kiến thức nền tảng và môn tiên quyết. "
                + "Đưa ra lời khuyên học tập thực tế. Tránh đi sâu vào chủ đề nâng cao.";
        } else {
            return "📌 SINH VIÊN NĂM " + year + " (cuối): Có thể đi sâu vào chủ đề nâng cao, đồ án, và định hướng nghề nghiệp. "
                + "Nhấn mạnh môn học nào quan trọng cho lập trình thực tế và cơ hội việc làm. "
                + "Có thể gợi ý repository GitHub làm portfolio.";
        }
    }

    /**
     * Detect if the user message contains multiple distinct sub-questions
     * (numbered, bulleted, or separated by question marks) and return
     * a system instruction to structure the response per-part.
     */
    private String detectMultiPartQuery(String rawMessage) {
        if (rawMessage == null || rawMessage.isBlank()) return null;
        String msg = rawMessage.trim();

        // Pattern 1: Numbered list like (1), (2), (3) or 1., 2., 3.
        java.util.regex.Matcher numMatcher = java.util.regex.Pattern.compile(
            "(?:\\(\\d+\\)|\\d+\\.)\\s*").matcher(msg);
        int count = 0;
        while (numMatcher.find()) count++;

        // Pattern 2: Question marks (each ? is a separate question)
        int qCount = msg.length() - msg.replace("?", "").length();
        if (msg.endsWith("?")) qCount = Math.max(qCount - 1, 1); // trailing ? often just punctuation
        qCount = Math.min(qCount, 5); // cap at 5

        int total = Math.max(count, qCount);

        if (total >= 2) {
            return "📌 CÂU HỎI GỒM " + total + " PHẦN. Hãy trả lời TỪNG PHẦN riêng biệt "
                + "với heading ### (1), ### (2) v.v. Rõ ràng, dễ đọc.";
        }
        if (total == 1 && msg.contains(",")) {
            // Single question with multiple subjects — could be comparison
            return null; // let LLM handle naturally
        }
        return null;
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

    private SubjectQaResponse buildRoadmapResponse(
            String message,
            UUID sessionId,
            List<Long> relevantNodeIds,
            List<String> sources) {
        UUID effectiveSessionId = sessionId != null ? sessionId : UUID.randomUUID();
        String roadmapAnswer = "Mình đã dựng lộ trình học tập theo mục tiêu bạn nhập. "
            + "Xem phần roadmap bên dưới để thấy môn học, tín chỉ, học kỳ và hướng tốt nghiệp.";

        try {
            RoadmapGenerationRequest request = new RoadmapGenerationRequest(
                trimForPrompt(message, 2000),
                trimForPrompt(message, 200)
            );
            RoadmapRecommendationResponse roadmap = aiService.generateRoadmap(request);
            if (roadmap != null) {
                return new SubjectQaResponse(
                    roadmapAnswer,
                    effectiveSessionId,
                    relevantNodeIds,
                    sources,
                    "ROADMAP",
                    List.of(),
                    roadmap,
                    null,
                    null
                );
            }
        } catch (Exception e) {
            log.warn("SubjectQaService: roadmap generation failed, falling back to text advice: {}", e.getMessage());
        }

        return new SubjectQaResponse(
            buildCareerCourseAdviceResponse(message),
            effectiveSessionId,
            relevantNodeIds,
            sources,
            "DIRECT",
            List.of(),
            null,
            null,
            null
        );
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

    private String removeEmojis(String text) {
        if (text == null) {
            return null;
        }
        return text.replaceAll("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]|[\\u2600-\\u27BF]|[\\u2B00-\\u2BFF]|[\\p{So}]|[\\uFE00-\\uFE0F]", "");
    }
}
