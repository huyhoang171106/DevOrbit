package vn.edu.uit.devorbit_api.service.ai;

import org.springframework.stereotype.Component;
import vn.edu.uit.devorbit_api.dto.publicapi.AiResponse;
import vn.edu.uit.devorbit_api.entity.Course;
import vn.edu.uit.devorbit_api.entity.GithubRepo;

/**
 * Generates tutor advice using impact scores and course context.
 * No LLM required — leverages precomputed knowledge graph analytics.
 */
@Component
public class AdviceGenerator {

    public AiResponse generateAdvice(GithubRepo repo, double impactScore, int downstreamCount) {
        Course course = repo.getCourse();
        String courseName = course != null ? course.getTenMH() : "môn học";
        String lang = repo.getPrimaryLanguage() != null ? repo.getPrimaryLanguage() : "ngôn ngữ lập trình";

        StringBuilder sb = new StringBuilder();
        sb.append("🎯 **LỜI KHUYÊN HỌC TẬP**\n\n");

        // Risk-based warning
        if (impactScore > 7.0) {
            sb.append("⚠️ **Môn có ảnh hưởng rất lớn** — đây là môn nền tảng cốt lõi.\n");
            sb.append("Nếu học không tốt sẽ ảnh hưởng đến **").append(downstreamCount).append(" môn** phía sau.\n\n");
        } else if (impactScore > 4.0) {
            sb.append("📌 **Môn có ảnh hưởng trung bình** — ");
            sb.append("cần nắm vững kiến thức để học các môn sau.\n\n");
        }

        // Repository usage advice
        sb.append("📚 **Cách sử dụng repository này:**\n");
        sb.append("- Fork repository về để thực hành code trực tiếp\n");
        sb.append("- Đọc source code để hiểu cấu trúc project thực tế\n");
        sb.append("- Xem commit history để học quy trình phát triển\n");
        sb.append("- Thử thực hiện một pull request nhỏ\n\n");

        // Tech-specific advice
        sb.append("💡 **Gợi ý kỹ thuật:**\n");
        sb.append(String.format(
            "Repository này sử dụng **%s**. Hãy tập trung nắm vững " +
            "các khái niệm cốt lõi trước khi đọc sâu vào codebase.\n\n", lang));

        // Course-specific advice
        if (course != null) {
            sb.append(String.format(
                "📖 **Về môn %s:**\n", courseName));
            sb.append("- Hoàn thành bài tập đầy đủ trước khi đọc repository\n");
            sb.append("- So sánh code trong repository với kiến thức đã học\n");
            sb.append("- Ghi chú lại những pattern hay để áp dụng cho dự án cá nhân\n");
        }

        return new AiResponse(sb.toString(), "TUTOR_ADVICE");
    }
}
