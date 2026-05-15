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

        String techStacks = repo.getTechStacks().stream()
            .map(ts -> ts.getName())
            .collect(Collectors.joining(", "));

        String lang = repo.getPrimaryLanguage() != null ? repo.getPrimaryLanguage() : "ngôn ngữ khác";
        int stars = repo.getStars() != null ? repo.getStars() : 0;

        // Find downstream courses that depend on this repo's course
        String downstreamInfo = "";
        if (course != null) {
            List<Course> downstream = courseRepository.findDownstreamCourses(course.getId());
            if (!downstream.isEmpty()) {
                downstreamInfo = downstream.stream()
                    .limit(3)
                    .map(Course::getTenMH)
                    .collect(Collectors.joining(", "));
                downstreamInfo = "Môn này là nền tảng cho: **" + downstreamInfo + "**.";
            }
        }

        String category = determineCategory(repo);

        String summary = String.format(
            "Repository **%s** thuộc môn **%s** (%s%s).\n\n" +
            "Sử dụng **%s** với **%d sao** trên GitHub.\n\n" +
            "**Công nghệ sử dụng:** %s\n\n" +
            "**Mô tả:** %s\n\n" +
            "**Giá trị học thuật:** Đây là một dự án %s, giúp sinh viên " +
            "thực hành kiến thức lý thuyết đã học và tiếp cận với mã nguồn thực tế.\n\n" +
            "%s",
            repo.getDisplayName(),
            courseName, courseCode,
            semester != null ? ", HK" + semester : "",
            lang, stars,
            techStacks.isEmpty() ? "Chưa xác định" : techStacks,
            repo.getDescription() != null ? repo.getDescription() : "Repository chứa mã nguồn tham khảo cho môn học này.",
            category,
            downstreamInfo
        );

        return new AiResponse(summary, "SUMMARY");
    }

    private String determineCategory(GithubRepo repo) {
        String lang = repo.getPrimaryLanguage() != null ? repo.getPrimaryLanguage().toLowerCase() : "";
        String desc = repo.getDescription() != null ? repo.getDescription().toLowerCase() : "";

        if (lang.contains("java") || lang.contains("python") || lang.contains("go") ||
            desc.contains("backend") || desc.contains("server") || desc.contains("api")) {
            return "phát triển Backend";
        }
        if (lang.contains("typescript") || lang.contains("javascript") || lang.contains("html") ||
            desc.contains("frontend") || desc.contains("ui") || desc.contains("giao diện")) {
            return "phát triển Web Frontend";
        }
        if (lang.contains("kotlin") || lang.contains("swift") || lang.contains("dart") ||
            desc.contains("mobile") || desc.contains("di động") || desc.contains("android")) {
            return "phát triển ứng dụng Mobile";
        }
        if (lang.contains("sql") || desc.contains("data") || desc.contains("dữ liệu") || desc.contains("database")) {
            return "khoa học dữ liệu";
        }
        return "phát triển phần mềm";
    }
}
