# TECH.md — Hoàn thiện tích hợp AI trong Knowledge Graph (Scenario Engine)

## 1. Current System Context

### Files Affected

| File | Vai trò | Hành động |
|---|---|---|
| `service/AiService.java` | Backend AI logic | **REWRITE** — ScenarioEngine |
| `service/ScenarioEngine.java` | **NEW** — Core engine | **CREATE** |
| `service/RoadmapGenerator.java` | **NEW** — Gen roadmap | **CREATE** |
| `service/SummaryGenerator.java` | **NEW** — Gen summary | **CREATE** |
| `service/AdviceGenerator.java` | **NEW** — Gen advice | **CREATE** |
| `service/GraphQueryEngine.java` | **NEW** — Query graph | **CREATE** |
| `service/CurriculumMatcher.java` | **NEW** — Match courses | **CREATE** |
| `controller/PublicAiController.java` | AI endpoints | **EDIT** — thêm graph query |
| `dto/AiQueryRequest.java` | **NEW** | **CREATE** |
| `dto/AiQueryResponse.java` | **NEW** | **CREATE** |
| `web/hooks/useAiRoadmap.ts` | Frontend | **EDIT** — gọi backend thay vì LLM |
| `web/hooks/useAiGraphQuery.ts` | **NEW** | **CREATE** |
| `web/lib/ai.ts` | LLM config cũ | **DELETE** |

## 2. Architecture

```
Browser (React)
  └── useAiRoadmap.ts → POST /api/ai/generate-roadmap (backend)
  └── useAiGraphQuery.ts → POST /api/ai/knowledge-graph/query

Spring Boot Backend
  AiService.java (Facade)
    ├── ScenarioEngine.java (Core)
    │     ├── RoadmapGenerator.java    — gen từ graph + career keywords
    │     ├── SummaryGenerator.java    — gen từ repo metadata + course
    │     ├── AdviceGenerator.java     — gen từ impact scores + risks
    │     └── GraphQueryEngine.java    — keyword matching trên graph
    │
    └── KnowledgeGraphService (existing) — graph data provider

Mobile (Android) — không đổi, gọi REST API từ backend
```

## 3. Implementation Plan

### Phase 1: Core Engine

#### Step 1: CurriculumMatcher.java

```java
@Component
public class CurriculumMatcher {
    private static final Map<String, Set<String>> CAREER_KEYWORDS = Map.of(
        "web developer", Set.of("frontend", "backend", "web", "react", "spring", "javascript", "html", "css", "rest"),
        "mobile developer", Set.of("android", "ios", "mobile", "kotlin", "swift", "react native"),
        "ai engineer", Set.of("machine learning", "deep learning", "ai", "neural", "data science", "python", "nlp"),
        "devops", Set.of("docker", "kubernetes", "ci/cd", "cloud", "aws", "linux", "network"),
        "game developer", Set.of("game", "unity", "unreal", "graphics", "3d", "opengl"),
        "data scientist", Set.of("data", "statistics", "analytics", "big data", "python", "ml"),
        "security engineer", Set.of("security", "cryptography", "network security", "penetration")
    );
    
    public CareerMatchResult matchCoursesToCareer(String careerPath, List<Course> allCourses) {
        // Lowercase + fuzzy match careerPath to CAREER_KEYWORDS
        // Find courses whose name/description contains matched keywords
        // Return scored results
    }
}
```

#### Step 2: SummaryGenerator.java

```java
@Component
public class SummaryGenerator {
    
    public RepoSummary generateSummary(GithubRepo repo, Course course, List<Course> downstreamCourses) {
        String summary = String.format(
            "Repository **%s** thuộc môn **%s** (%s, HK%d).\n\n" +
            "Sử dụng **%s** với **%d sao** trên GitHub.\n\n" +
            "**Giá trị học thuật:** %s\n\n" +
            "Môn này là nền tảng cho: **%s**.",
            repo.getDisplayName(),
            course.getName(), course.getCode(), course.getSemester(),
            repo.getPrimaryLanguage() != null ? repo.getPrimaryLanguage() : "ngôn ngữ khác",
            repo.getStars() != null ? repo.getStars() : 0,
            repo.getDescription() != null ? repo.getDescription() : "Repository chứa mã nguồn và tài liệu tham khảo cho môn học này.",
            downstreamCourses.isEmpty() 
                ? "các môn chuyên sâu hơn" 
                : downstreamCourses.stream().map(Course::getName).collect(Collectors.joining(", "))
        );
        
        return new RepoSummary(summary);
    }
}
```

#### Step 3: AdviceGenerator.java

```java
@Component
public class AdviceGenerator {
    
    public TutorAdvice generateAdvice(GithubRepo repo, Course course, double impactScore) {
        StringBuilder sb = new StringBuilder();
        
        // Risk-based advice
        if (impactScore > 0.7) {
            sb.append("⚠️ **Môn có ảnh hưởng lớn** — đây là môn nền tảng.");
            sb.append("Nếu học không tốt sẽ ảnh hưởng đến nhiều môn sau.\n\n");
        }
        
        // Usage advice
        sb.append("📚 **Cách sử dụng repository này:**\n");
        sb.append("- Fork repository về để thực hành code\n");
        sb.append("- Đọc source code để hiểu cấu trúc project thực tế\n");
        sb.append("- Xem commit history để học quy trình phát triển\n\n");
        
        // Tech-specific advice
        if (repo.getPrimaryLanguage() != null) {
            sb.append(String.format(
                "💡 **Gợi ý:** Dự án sử dụng **%s**. Hãy tập trung nắm vững " +
                "các concept cốt lõi trước khi đọc sâu vào codebase.\n",
                repo.getPrimaryLanguage()
            ));
        }
        
        return new TutorAdvice(sb.toString());
    }
}
```

#### Step 4: RoadmapGenerator.java

```java
@Component
public class RoadmapGenerator {
    private final CourseRepository courseRepository;
    private final KnowledgeGraphService kgService;
    private final CurriculumMatcher matcher;
    
    public RoadmapRecommendationResponse generate(
        String learningGoals, 
        String careerPath,
        KnowledgeGraphResponse graph
    ) {
        // 1. Lấy tất cả courses
        List<Course> allCourses = courseRepository.findAll();
        
        // 2. Match mandatory courses (phải học)
        List<Course> mandatory = allCourses.stream()
            .filter(c -> CurriculumConstants.MANDATORY_CODES.contains(c.getCode()))
            .collect(toList());
            
        // 3. Match elective courses theo careerPath
        CareerMatchResult matched = matcher.matchCoursesToCareer(careerPath, allCourses);
        List<Course> elective = matched.getTopMatches(5); // top 5
        
        // 4. Sắp xếp theo topological level
        Map<Long, Integer> levels = kgService.getTopologicalLevels();
        List<Course> ordered = Stream.concat(mandatory.stream(), elective.stream())
            .distinct()
            .sorted(Comparator.comparingInt(c -> levels.getOrDefault(c.getId(), 999)))
            .collect(toList());
        
        // 5. Gen summary + reasoning cho từng môn
        String summary = String.format(
            "Với định hướng **%s** và mục tiêu **%s**, " +
            "DevOrbit đề xuất lộ trình gồm **%d môn bắt buộc** " +
            "và **%d môn tự chọn phù hợp** với định hướng của bạn.\n\n" +
            "Các môn được sắp xếp theo trình tự học tối ưu.",
            careerPath, learningGoals,
            mandatory.size(), elective.size()
        );
        
        List<CourseRecommendation> recommendations = ordered.stream()
            .map(c -> buildRecommendation(c, careerPath, levels))
            .collect(toList());
        
        return new RoadmapRecommendationResponse(summary, recommendations);
    }
    
    private CourseRecommendation buildRecommendation(Course course, String careerPath, Map<Long, Integer> levels) {
        boolean isMandatory = CurriculumConstants.MANDATORY_CODES.contains(course.getCode());
        String reasoning;
        
        if (isMandatory) {
            reasoning = String.format("Môn bắt buộc trong chương trình KTPM.");
        } else {
            reasoning = String.format(
                "Môn tự chọn phù hợp với định hướng **%s** " +
                "vì nội dung liên quan đến %s.",
                careerPath, course.getDescription()
            );
        }
        
        return new CourseRecommendation(
            course.getId(), course.getCode(), course.getName(),
            reasoning, course.getDescription()
        );
    }
}
```

#### Step 5: GraphQueryEngine.java

```java
@Component
public class GraphQueryEngine {
    private final KnowledgeGraphService kgService;
    
    private static final List<QueryPattern> PATTERNS = List.of(
        new QueryPattern("tiên quyết.*(\\w{4,6})", QueryType.PREREQUISITE_OF),
        new QueryPattern("(\\w{4,6}).*tiên quyết", QueryType.PREREQUISITES_FOR),
        new QueryPattern("học sau.*(\\w{4,6})", QueryType.DOWNSTREAM_OF),
        new QueryPattern("(\\w{4,6}).*học sau", QueryType.DOWNSTREAM_FOR),
        new QueryPattern("ảnh hưởng.*trượt.*(\\w{4,6})", QueryType.IMPACT_IF_FAIL),
        new QueryPattern("tác động.*(\\w{4,6})", QueryType.IMPACT_IF_FAIL),
        new QueryPattern("học kỳ.*(\\w{4,6})", QueryType.SEMESTER_OF),
        new QueryPattern("(\\w{4,6}).*học kỳ.*mấy", QueryType.SEMESTER_OF),
        new QueryPattern("tổng.*môn|tổng.*tín|ba.o nhiêu", QueryType.TOTAL_STATS),
        new QueryPattern("kho.*khó|khó nh?|nặng.*nh?", QueryType.DIFFICULTY)
    );
    
    public AiQueryResponse query(String question, KnowledgeGraphResponse graph) {
        String lower = question.toLowerCase();
        
        for (QueryPattern p : PATTERNS) {
            Matcher m = p.matcher(lower);
            if (m.find()) {
                return handleQuery(p.type, m, graph);
            }
        }
        
        // Fallback: search trong course names
        return fallbackSearch(question, graph);
    }
}
```

#### Step 6: AiService.java (Facade)

Simplify: `AiService` becomes a thin facade that delegates to the scenario generators.

```java
@Service
public class AiService {
    private final SummaryGenerator summaryGen;
    private final AdviceGenerator adviceGen;
    private final RoadmapGenerator roadmapGen;
    private final GraphQueryEngine queryEngine;
    
    // Delegate methods...
}
```

### Phase 2: New Endpoint — Graph Query

#### Step 7: DTOs + Controller

```java
// AiQueryRequest.java
public record AiQueryRequest(String query) {}

// AiQueryResponse.java
public record AiQueryResponse(
    String answer,
    List<Long> relevantNodeIds
) {}

// PublicAiController.java — thêm:
@PostMapping("/knowledge-graph/query")
public AiQueryResponse queryKnowledgeGraph(@RequestBody AiQueryRequest request) {
    return aiService.queryKnowledgeGraph(request);
}
```

### Phase 3: Frontend Refactor

#### Step 8: useAiRoadmap.ts → gọi backend

```typescript
export function useAiRoadmap() {
    return useMutation<RoadmapResponse, Error, RoadmapRequest>({
        mutationFn: async ({ learningGoals, careerPath }) => {
            const res = await fetch('/api/ai/generate-roadmap', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ learningGoals, careerPath })
            });
            if (!res.ok) throw new Error('Generation failed');
            return res.json();
        }
    });
}
```

#### Step 9: useAiGraphQuery.ts (NEW)

```typescript
import { useMutation } from '@tanstack/react-query';

interface AiQueryResponse {
    answer: string;
    relevantNodeIds: number[];
}

export function useAiGraphQuery() {
    return useMutation<AiQueryResponse, Error, string>({
        mutationFn: async (query: string) => {
            const res = await fetch('/api/ai/knowledge-graph/query', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ query })
            });
            if (!res.ok) throw new Error('Query failed');
            return res.json();
        }
    });
}
```

#### Step 10: Xoá lib/ai.ts + cập nhật web/.env

- Xoá file `devorbit-web/src/lib/ai.ts`
- Xoá `VITE_LLM_API_KEY` khỏi `devorbit-web/.env`
- (Giữ lại hoặc xoá `@ai-sdk/openai-compatible` và `ai` khỏi package.json)

## 4. Testing Strategy

### Unit Tests (core logic — dễ vì deterministic)

- `CurriculumMatcherTest` — verify keyword matching
- `RoadmapGeneratorTest` — verify mandatory always included, electives match career
- `GraphQueryEngineTest` — verify pattern matching
- `SummaryGeneratorTest` — verify correct formatting
- `AdviceGeneratorTest` — verify impact-based warnings

### Integration Tests

- `AiControllerTest` — full HTTP cycle
- Verify graph query returns relevant node IDs

### Manual Verification

- `curl POST /api/ai/generate-roadmap` với "Web Developer"
- `curl POST /api/ai/knowledge-graph/query` với "Môn nào tiên quyết cho SE104?"
- Network tab không còn request đến opencode.ai

## 5. Risks

| Risk | Mitigation |
|---|---|
| Keyword matching không chính xác | Fallback search trong course names |
| Career keyword set thiếu | Dễ mở rộng, chỉ cần thêm entry |
| Query pattern không match | Fallback "Xin lỗi, tôi chưa hiểu câu hỏi..." |
