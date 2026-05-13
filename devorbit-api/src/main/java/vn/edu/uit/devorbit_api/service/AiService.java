package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.constant.CurriculumConstants;
import vn.edu.uit.devorbit_api.dto.publicapi.AiResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.RoadmapGenerationRequest;
import vn.edu.uit.devorbit_api.dto.publicapi.RoadmapRecommendationResponse;
import vn.edu.uit.devorbit_api.entity.Course;
import vn.edu.uit.devorbit_api.entity.CourseRelationType;
import vn.edu.uit.devorbit_api.entity.CourseRelationship;
import vn.edu.uit.devorbit_api.entity.GithubRepo;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.CourseRelationshipRepository;
import vn.edu.uit.devorbit_api.repository.CourseRepository;
import vn.edu.uit.devorbit_api.repository.GithubRepoRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiService {
    private final GithubRepoRepository githubRepoRepository;
    private final CourseRepository courseRepository;
    private final CourseRelationshipRepository courseRelationshipRepository;

    public AiResponse getRepoAiSummary(Long repoId) {
        GithubRepo repo = githubRepoRepository.findById(repoId)
            .orElseThrow(() -> new NotFoundException("Repo not found: " + repoId));

        String techStr = repo.getTechStacks().stream()
            .map(ts -> ts.getName())
            .collect(Collectors.joining(", "));

        String category = determineCategory(repo);
        String summary = generateSummary(repo, category, techStr);

        return new AiResponse(summary, "SUMMARY");
    }

    public AiResponse getTutorAdvice(Long repoId) {
        GithubRepo repo = githubRepoRepository.findById(repoId)
            .orElseThrow(() -> new NotFoundException("Repo not found: " + repoId));

        String courseName = repo.getCourse() != null ? repo.getCourse().getTenMH() : "môn học";
        String category = determineCategory(repo);
        
        String advice = String.format(
            "LỜI KHUYÊN TỪ AI TUTOR: Trong khuôn khổ %s, repository này là một tài liệu tham khảo tuyệt vời về %s. " +
            "Bạn nên tập trung vào cách dự án sử dụng %s để giải quyết các bài toán thực tế. " +
            "Hãy thử fork repo này và thực hiện một thay đổi nhỏ để hiểu rõ hơn về cấu trúc luồng dữ liệu.",
            courseName, category, repo.getPrimaryLanguage()
        );

        return new AiResponse(advice, "TUTOR_ADVICE");
    }

    @Transactional(readOnly = true)
    public RoadmapRecommendationResponse generateRoadmap(RoadmapGenerationRequest request) {
        String goals = request.learningGoals().toLowerCase();
        String path = request.careerPath().toLowerCase();

        List<Course> allCourses = courseRepository.findAll();

        // Index courses by code for fast lookup
        Map<String, Course> courseByCode = new HashMap<>();
        for (Course c : allCourses) {
            courseByCode.put(c.getMaMH(), c);
        }

        // Build prerequisite map: for each course (by code), list of prerequisite course codes
        // A CR relationship with type PREREQUISITE means (courseId = course, relatedCourseId = prerequisite)
        Map<String, Set<String>> prerequisiteCodes = new HashMap<>();
        List<CourseRelationship> prereqRelations = courseRelationshipRepository.findByRelationType(CourseRelationType.PREREQUISITE);
        for (CourseRelationship rel : prereqRelations) {
            String courseCode = rel.getCourse().getMaMH();
            String prereqCode = rel.getRelatedCourse().getMaMH();
            prerequisiteCodes.computeIfAbsent(courseCode, k -> new HashSet<>()).add(prereqCode);
        }

        // Step 1: Always include mandatory courses (bắt buộc cơ sở ngành + chuyên ngành)
        List<Course> mandatoryCourses = new ArrayList<>();
        List<Course> electiveCourses = new ArrayList<>();
        for (Course c : allCourses) {
            if (CurriculumConstants.MANDATORY_CODES.contains(c.getMaMH())) {
                mandatoryCourses.add(c);
            } else {
                electiveCourses.add(c);
            }
        }

        // Step 2: Find elective courses matching the user's goals/path
        // Only include electives whose ALL prerequisites are satisfied
        // (i.e., all prerequisite codes are in the mandatory set)
        Set<String> availableCodes = new HashSet<>(CurriculumConstants.MANDATORY_CODES);

        List<Course> matchedElectives = new ArrayList<>();
        for (Course elective : electiveCourses) {
            if (!isRelevant(elective, goals, path)) continue;

            Set<String> prereqs = prerequisiteCodes.getOrDefault(elective.getMaMH(), Collections.emptySet());
            // Check if ALL prerequisites are satisfied (in the available set)
            boolean allPrereqsMet = true;
            for (String prereq : prereqs) {
                if (!availableCodes.contains(prereq)) {
                    allPrereqsMet = false;
                    break;
                }
            }
            if (allPrereqsMet) {
                matchedElectives.add(elective);
            }
        }

        // Step 3: Build combined recommendation list
        List<RoadmapRecommendationResponse.CourseRecommendation> recommendations = new ArrayList<>();

        // Start with all mandatory courses
        for (Course c : mandatoryCourses) {
            recommendations.add(new RoadmapRecommendationResponse.CourseRecommendation(
                c.getId(), c.getMaMH(), c.getTenMH(),
                generateMandatoryReasoning(c),
                c.getDescription()
            ));
        }

        // Then add matched electives (up to 5)
        for (Course c : matchedElectives.stream().limit(5).toList()) {
            recommendations.add(new RoadmapRecommendationResponse.CourseRecommendation(
                c.getId(), c.getMaMH(), c.getTenMH(),
                generateElectiveReasoning(c, goals, path),
                c.getDescription()
            ));
        }

        String summary = String.format(
            "📚 Lộ trình học tập đề xuất cho định hướng '%s': Bao gồm %d môn bắt buộc (cơ sở ngành + chuyên ngành) " +
            "và %d môn tự chọn phù hợp với mục tiêu '%s'. " +
            "Các môn tự chọn được chọn dựa trên điều kiện tiên quyết đã được thỏa mãn bởi chương trình bắt buộc. " +
            "Sinh viên nên hoàn thành các môn bắt buộc theo đúng học kỳ quy định trước khi chọn môn tự chọn.",
            request.careerPath(), mandatoryCourses.size(), matchedElectives.size(),
            request.learningGoals()
        );

        return new RoadmapRecommendationResponse(summary, recommendations);
    }

    private boolean isRelevant(Course c, String goals, String path) {
        String content = (c.getTenMH() + " " + c.getTenMH_EN() + " " + 
                          Objects.toString(c.getDescription(), "")).toLowerCase();
        // Simple keyword matching for simulation
        return content.contains(goals) || content.contains(path) || 
               (path.contains("frontend") && (content.contains("web") || content.contains("javascript"))) ||
               (path.contains("backend") && (content.contains("java") || content.contains("server") || content.contains("sql"))) ||
               (path.contains("data") && (content.contains("dữ liệu") || content.contains("thống kê"))) ||
               (path.contains("mobile") && (content.contains("di động") || content.contains("android") || content.contains("ios")));
    }

    private String generateMandatoryReasoning(Course c) {
        return String.format(
            "[BẮT BUỘC] Môn '%s' là môn bắt buộc trong chương trình Kỹ thuật Phần mềm. " +
            "Sinh viên bắt buộc phải hoàn thành môn này để đáp ứng yêu cầu tốt nghiệp.",
            c.getTenMH()
        );
    }

    private String generateElectiveReasoning(Course c, String goals, String path) {
        String name = c.getTenMH();
        if (name.contains("Cấu trúc dữ liệu")) return "Môn học này cung cấp nền tảng tư duy thuật toán cốt yếu cho bất kỳ lập trình viên nào, giúp bạn tối ưu hóa hiệu suất ứng dụng.";
        if (name.contains("Mạng máy tính")) return "Hiểu rõ giao thức mạng là bắt buộc để bạn có thể xây dựng các hệ thống phân tán và ứng dụng kết nối Internet ổn định.";
        if (name.contains("Cơ sở dữ liệu")) return "Quản lý dữ liệu là trái tim của mọi ứng dụng. Môn học này giúp bạn thiết kế và truy vấn dữ liệu hiệu quả.";
        return String.format("Môn học '%s' phù hợp với định hướng '%s' và các môn tiên quyết đều nằm trong chương trình bắt buộc, đảm bảo bạn đã có nền tảng kiến thức cần thiết để theo học.", name, path);
    }

    private String determineCategory(GithubRepo repo) {
        String lang = repo.getPrimaryLanguage() != null ? repo.getPrimaryLanguage().toLowerCase() : "";
        if (lang.contains("java") || lang.contains("python") || lang.contains("go")) return "phát triển Backend";
        if (lang.contains("typescript") || lang.contains("javascript") || lang.contains("html")) return "phát triển Web Frontend";
        if (lang.contains("kotlin") || lang.contains("swift") || lang.contains("dart")) return "phát triển ứng dụng Mobile";
        return "phát triển phần mềm";
    }

    private String generateSummary(GithubRepo repo, String category, String techStr) {
        return String.format(
            "Dựa trên phân tích mã nguồn, repository '%s' là một dự án tiêu biểu cho %s. " +
            "Dự án sử dụng %s làm ngôn ngữ chính, kết hợp với các công nghệ: %s. " +
            "Đây là một ví dụ thực tiễn giúp sinh viên UIT nắm vững cách tổ chức code và áp dụng kiến thức lý thuyết vào dự án thực tế.",
            repo.getDisplayName(), category, repo.getPrimaryLanguage(), techStr
        );
    }
}
