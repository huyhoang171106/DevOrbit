package vn.edu.uit.devorbit_api.service.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.edu.uit.devorbit_api.dto.publicapi.AiResponse;
import vn.edu.uit.devorbit_api.entity.Course;
import vn.edu.uit.devorbit_api.entity.GithubRepo;
import vn.edu.uit.devorbit_api.repository.CourseRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates repo summaries using repo metadata + course context.
 * Uses LLM when available, falls back to rule-based generation.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SummaryGenerator {

    private final CourseRepository courseRepository;
    private final OpenCodeAiService openCodeAiService;

    public AiResponse generateSummary(GithubRepo repo) {
        Course course = repo.getCourse();

        String courseName = course != null ? course.getTenMH() : "môn học";
        String courseCode = course != null ? course.getMaMH() : "";
        String language = repo.getPrimaryLanguage() != null ? repo.getPrimaryLanguage() : "Chưa xác định";
        String techStacks = repo.getTechStacks() != null 
            ? repo.getTechStacks().stream().map(ts -> ts.getName()).collect(Collectors.joining(", "))
            : "";

        // Try LLM first if enabled
        if (openCodeAiService.isLlmEnabled()) {
            try {
                String context = String.format(
                    "Repository: %s, Môn: %s (%s), Ngôn ngữ: %s, Tech stacks: %s, Stars: %d",
                    repo.getDisplayName(), courseName, courseCode, language, techStacks,
                    repo.getStars() != null ? repo.getStars() : 0
                );
                
                String llmResponse = openCodeAiService.generateCompletion(
                    PromptTemplates.REPO_SUMMARY, context
                );
                
                if (llmResponse != null && !llmResponse.isBlank()) {
                    log.debug("LLM summary generated for repo: {}", repo.getDisplayName());
                    return new AiResponse(llmResponse, "LLM_SUMMARY");
                }
            } catch (Exception e) {
                log.warn("LLM summary failed, falling back to rule-based: {}", e.getMessage());
            }
        }

        // Fallback to rule-based generation
        return generateRuleBasedSummary(repo, course, courseName, courseCode, language, techStacks);
    }

    private AiResponse generateRuleBasedSummary(GithubRepo repo, Course course, 
            String courseName, String courseCode, String language, String techStacks) {
        StringBuilder sb = new StringBuilder();

        // ============ HEADER ============
        sb.append("📌 **Tổng quan**\n\n");
        sb.append(String.format(
            "Repository **%s** thuộc môn **%s** (%s)",
            repo.getDisplayName(), courseName, courseCode
        ));
        if (course != null && course.getSemester() != null) {
            sb.append(String.format(" — học kỳ **HK%d**", course.getSemester()));
        }
        if (course != null && course.getSoTC() > 0) {
            sb.append(String.format(" (%d tín chỉ)", course.getSoTC()));
        }
        sb.append(".\n\n");

        // ============ TECH STACK & STATS ============
        sb.append("⚙️ **Thông số kỹ thuật**\n\n");
        sb.append(String.format("- **Ngôn ngữ chính:** %s\n", language));

        if (!techStacks.isEmpty()) {
            sb.append(String.format("- **Công nghệ sử dụng:** %s\n", techStacks));
        }

        int stars = repo.getStars() != null ? repo.getStars() : 0;
        if (stars > 0) {
            sb.append(String.format("- **Đánh giá:** ⭐ %d sao trên GitHub\n", stars));
        } else {
            sb.append("- **Đánh giá:** Repository mới, chưa có sao\n");
        }

        // ============ CATEGORY ============
        sb.append("\n📂 **Phân loại:** ");
        sb.append(determineCategory(repo));
        sb.append("\n\n");

        // ============ LEARNING VALUE ============
        sb.append("📚 **Giá trị học tập**\n\n");
        String category = determineCategory(repo);
        if (category.contains("Backend")) {
            sb.append("Repository này tập trung vào kiến thức **phát triển phía server**. ");
            sb.append("Sinh viên nên chú ý cách tổ chức API, xử lý business logic, và quản lý dữ liệu.\n\n");
        } else if (category.contains("Frontend")) {
            sb.append("Repository này tập trung vào **giao diện người dùng**. ");
            sb.append("Sinh viên nên chú ý cách tổ chức component, quản lý state, và UX patterns.\n\n");
        } else if (category.contains("Mobile")) {
            sb.append("Repository này liên quan đến **phát triển ứng dụng di động**. ");
            sb.append("Sinh viên nên chú ý kiến trúc MVVM/MVI, lifecycle management, và responsive design.\n\n");
        } else {
            sb.append("Repository này minh họa các khái niệm **phát triển phần mềm** tổng quát. ");
            sb.append("Sinh viên nên chú ý cách tổ chức code, design patterns, và best practices.\n\n");
        }

        // ============ TECH LEVEL ============
        sb.append("📊 **Đánh giá kỹ thuật:** ");
        String techLevel = determineTechLevel(stars);
        sb.append(techLevel);
        sb.append(". ");

        if (stars > 5) {
            sb.append("Số lượng sao cao cho thấy cộng đồng đánh giá cao chất lượng mã nguồn. ");
        } else if (stars == 0) {
            sb.append("Dù chưa có nhiều sao, repository vẫn chứa mã nguồn tham khảo hữu ích cho sinh viên. ");
        }

        sb.append("Sinh viên nên đọc hiểu cấu trúc project, cách tổ chức mã nguồn, ");
        sb.append("và đối chiếu với kiến thức lý thuyết đã học trên lớp.\n\n");

        // ============ DOWNSTREAM ============
        if (course != null) {
            List<Course> downstream = courseRepository.findDownstreamCourses(course.getId());
            if (!downstream.isEmpty()) {
                sb.append("🔗 **Môn học liên quan**\n\n");
                sb.append("Môn này là nền tảng cho các môn sau:\n");
                for (Course d : downstream) {
                    sb.append(String.format("- **%s** (%s)\n", d.getTenMH(), d.getMaMH()));
                }
                sb.append("\n");
            }
        }

        return new AiResponse(sb.toString(), "SUMMARY");
    }

    private String determineCategory(GithubRepo repo) {
        String lang = repo.getPrimaryLanguage() != null ? repo.getPrimaryLanguage().toLowerCase() : "";
        String desc = repo.getDescription() != null ? repo.getDescription().toLowerCase() : "";
        String name = repo.getDisplayName() != null ? repo.getDisplayName().toLowerCase() : "";

        boolean hasWeb = lang.contains("html") || lang.contains("css") || name.contains("web") || desc.contains("web");
        boolean hasBackend = lang.contains("java") || lang.contains("python") || lang.contains("go")
            || lang.contains("node") || lang.contains("c#") || desc.contains("api") || desc.contains("server");
        boolean hasFrontend = lang.contains("typescript") || lang.contains("javascript") || lang.contains("react")
            || lang.contains("vue") || desc.contains("frontend") || desc.contains("ui");
        boolean hasMobile = lang.contains("kotlin") || lang.contains("swift") || lang.contains("dart")
            || lang.contains("android") || desc.contains("mobile");
        boolean hasData = lang.contains("sql") || lang.contains("r") || desc.contains("data") || desc.contains("machine learning");
        boolean hasSystem = lang.contains("c") || lang.contains("c++") || lang.contains("rust")
            || desc.contains("hệ thống") || desc.contains("operating") || desc.contains("embedded");

        if (hasSystem) return "lập trình hệ thống";
        if (hasBackend && !hasFrontend) return "phát triển Backend";
        if (hasFrontend && !hasBackend) return "phát triển Frontend";
        if (hasWeb) return "phát triển Web";
        if (hasMobile) return "phát triển ứng dụng Mobile";
        if (hasData) return "khoa học dữ liệu";

        return "phát triển phần mềm";
    }

    private String determineTechLevel(int stars) {
        if (stars >= 50) return "có độ phổ biến cao";
        if (stars >= 10) return "được cộng đồng quan tâm";
        if (stars >= 1) return "mới nhưng có tiềm năng";
        return "mới, chưa có đánh giá";
    }
}
