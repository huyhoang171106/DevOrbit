package vn.edu.uit.devorbit_api.service.ai;

import org.springframework.stereotype.Component;
import vn.edu.uit.devorbit_api.dto.publicapi.AiResponse;
import vn.edu.uit.devorbit_api.entity.Course;
import vn.edu.uit.devorbit_api.entity.GithubRepo;
import vn.edu.uit.devorbit_api.repository.CourseRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates repo summaries using repo metadata + course context.
 * No LLM required — all data comes from the database and knowledge graph.
 */
@Component
public class SummaryGenerator {

    private final CourseRepository courseRepository;

    public SummaryGenerator(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public AiResponse generateSummary(GithubRepo repo) {
        Course course = repo.getCourse();

        String courseName = course != null ? course.getTenMH() : "môn học";
        String courseCode = course != null ? course.getMaMH() : "";
        Integer semester = course != null ? course.getSemester() : null;
        int credits = course != null ? course.getSoTC() : 0;

        StringBuilder sb = new StringBuilder();

        // ============ HEADER ============
        sb.append("📌 **Tổng quan**\n\n");
        sb.append(String.format(
            "Repository **%s** thuộc môn **%s** (%s)",
            repo.getDisplayName(), courseName, courseCode
        ));
        if (semester != null) {
            sb.append(String.format(" — học kỳ **HK%d**", semester));
        }
        if (credits > 0) {
            sb.append(String.format(" (%d tín chỉ)", credits));
        }
        sb.append(".\n\n");

        // ============ TECH STACK & STATS ============
        sb.append("⚙️ **Thông số kỹ thuật**\n\n");

        // Ngôn ngữ chính
        String lang = repo.getPrimaryLanguage() != null ? repo.getPrimaryLanguage() : "Chưa xác định";
        sb.append(String.format("- **Ngôn ngữ chính:** %s\n", lang));

        // Tech stacks
        String techStacks = repo.getTechStacks().stream()
            .map(ts -> ts.getName())
            .collect(Collectors.joining(", "));
        if (!techStacks.isEmpty()) {
            sb.append(String.format("- **Công nghệ sử dụng:** %s\n", techStacks));
        }

        // Stars
        int stars = repo.getStars() != null ? repo.getStars() : 0;
        if (stars > 0) {
            sb.append(String.format("- **Đánh giá:** ⭐ %d sao trên GitHub\n", stars));
        } else {
            sb.append("- **Đánh giá:** Repository mới, chưa có sao\n");
        }

        // Ngôn ngữ bổ sung từ tech stacks
        List<String> nonPrimaryStacks = repo.getTechStacks().stream()
            .map(ts -> ts.getName())
            .filter(name -> !name.equalsIgnoreCase(lang))
            .collect(Collectors.toList());
        if (!nonPrimaryStacks.isEmpty()) {
            sb.append(String.format(
                "- **Kỹ thuật liên quan:** %s\n",
                String.join(", ", nonPrimaryStacks)
            ));
        }

        sb.append("\n");

        // ============ MÔ TẢ CHI TIẾT ============
        sb.append("📝 **Mô tả**\n\n");
        String description = repo.getDescription();
        if (description != null && !description.isBlank()) {
            // Làm giàu mô tả
            sb.append(description.trim());
            sb.append("\n\n");
        } else {
            sb.append("Repository chứa mã nguồn tham khảo cho môn học này.\n\n");
        }

        // ============ PHÂN TÍCH GIÁ TRỊ HỌC THUẬT ============
        sb.append("🎓 **Phân tích giá trị học thuật**\n\n");

        String category = determineCategory(repo);
        String techLevel = determineTechLevel(stars);

        sb.append(String.format(
            "Đây là một dự án **%s** %s. ",
            category, techLevel
        ));

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

        // Kiểm tra từ tên + desc
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
        if (stars >= 1) return "có chất lượng tham khảo tốt";
        return "quy mô nhỏ, phù hợp để thực hành";
    }
}
