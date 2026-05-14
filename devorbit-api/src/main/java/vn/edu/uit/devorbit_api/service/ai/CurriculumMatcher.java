package vn.edu.uit.devorbit_api.service.ai;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Matches career paths to course keywords.
 * Uses predefined keyword mappings — no LLM required.
 */
@Component
public class CurriculumMatcher {

    private static final Map<String, Set<String>> CAREER_KEYWORDS = new LinkedHashMap<>();

    static {
        CAREER_KEYWORDS.put("web", Set.of("web", "frontend", "backend", "react", "spring",
            "javascript", "html", "css", "rest", "api", "ui/ux", "giao diện"));
        CAREER_KEYWORDS.put("frontend", Set.of("frontend", "react", "javascript", "ui",
            "giao diện", "web", "html", "css", "typescript"));
        CAREER_KEYWORDS.put("backend", Set.of("backend", "spring", "java", "server",
            "sql", "cơ sở dữ liệu", "database", "api", "rest"));
        CAREER_KEYWORDS.put("mobile", Set.of("mobile", "android", "ios", "kotlin",
            "swift", "di động", "react native"));
        CAREER_KEYWORDS.put("android", Set.of("android", "kotlin", "mobile", "di động"));
        CAREER_KEYWORDS.put("ios", Set.of("ios", "swift", "mobile", "di động"));
        CAREER_KEYWORDS.put("ai", Set.of("machine learning", "deep learning", "ai",
            "neural", "data science", "python", "nlp", "trí tuệ nhân tạo"));
        CAREER_KEYWORDS.put("data", Set.of("data", "dữ liệu", "statistics", "thống kê",
            "analytics", "big data", "python", "ml"));
        CAREER_KEYWORDS.put("devops", Set.of("docker", "kubernetes", "ci/cd", "cloud",
            "aws", "linux", "network", "mạng", "bảo mật"));
        CAREER_KEYWORDS.put("security", Set.of("security", "bảo mật", "cryptography",
            "mã hóa", "network security", "an toàn thông tin"));
        CAREER_KEYWORDS.put("game", Set.of("game", "unity", "unreal", "graphics",
            "3d", "opengl", "đồ họa"));
        CAREER_KEYWORDS.put("fullstack", Set.of("fullstack", "full stack", "web",
            "frontend", "backend", "react", "spring", "javascript", "java"));
    }

    /**
     * Score courses based on how well they match a career path.
     */
    public List<ScoredCourse> matchCoursesToCareer(String careerPath, List<CourseInfo> allCourses) {
        String lowerPath = careerPath.toLowerCase();
        
        // Find which keyword sets match this career path
        Set<String> matchedKeywords = new HashSet<>();
        for (Map.Entry<String, Set<String>> entry : CAREER_KEYWORDS.entrySet()) {
            if (lowerPath.contains(entry.getKey()) || entry.getValue().stream().anyMatch(lowerPath::contains)) {
                matchedKeywords.addAll(entry.getValue());
            }
        }

        // Score each course
        List<ScoredCourse> scored = new ArrayList<>();
        for (CourseInfo course : allCourses) {
            String content = (course.name() + " " + course.nameEn() + " " + course.description()).toLowerCase();
            
            int score = 0;
            for (String keyword : matchedKeywords) {
                if (content.contains(keyword)) {
                    score += 10;
                }
            }
            
            if (score > 0) {
                scored.add(new ScoredCourse(course.id(), course.code(), course.name(), course.description(), score));
            }
        }

        // Sort by score descending
        scored.sort((a, b) -> Integer.compare(b.score(), a.score()));
        return scored;
    }

    public record ScoredCourse(Long id, String code, String name, String description, int score) {}
    public record CourseInfo(Long id, String code, String name, String nameEn, String description) {}
}
