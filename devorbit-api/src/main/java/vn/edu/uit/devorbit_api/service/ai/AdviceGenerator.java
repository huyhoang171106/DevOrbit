package vn.edu.uit.devorbit_api.service.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.edu.uit.devorbit_api.dto.publicapi.AiResponse;
import vn.edu.uit.devorbit_api.entity.Course;
import vn.edu.uit.devorbit_api.entity.GithubRepo;

import java.util.stream.Collectors;

/**
 * Generates tutor advice using impact scores, language specifics, and course context.
 * Uses LLM when available, falls back to rule-based generation.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdviceGenerator {

    private final OpenCodeAiService openCodeAiService;
    private final LlmContextBuilder contextBuilder;

    public AiResponse generateAdvice(GithubRepo repo, double impactScore, int downstreamCount) {
        Course course = repo.getCourse();
        String courseName = course != null ? course.getTenMH() : "môn học";
        String lang = repo.getPrimaryLanguage() != null ? repo.getPrimaryLanguage() : "ngôn ngữ lập trình";

        // Try LLM first if enabled
        if (openCodeAiService.isLlmEnabled()) {
            try {
                // Build rich context from DB
                String ragContext = contextBuilder.buildRepoContext(repo);
                String context = String.format(
                    "%s\n\nRepository: %s, Điểm ảnh hưởng: %.1f, Số môn downstream: %d, Stars: %d",
                    ragContext, repo.getDisplayName(), impactScore, downstreamCount,
                    repo.getStars() != null ? repo.getStars() : 0
                );
                
                log.debug("RAG context length: {} chars", ragContext.length());
                
                String llmResponse = openCodeAiService.generateCompletion(
                    PromptTemplates.TUTOR_ADVICE, context
                );
                
                if (llmResponse != null && !llmResponse.isBlank()) {
                    log.debug("LLM advice generated for repo: {}", repo.getDisplayName());
                    return new AiResponse(llmResponse, "LLM_TUTOR_ADVICE");
                }
            } catch (Exception e) {
                log.warn("LLM advice failed, falling back to rule-based: {}", e.getMessage());
            }
        }

        // Fallback to rule-based generation
        return generateRuleBasedAdvice(repo, courseName, lang, impactScore, downstreamCount);
    }

    private AiResponse generateRuleBasedAdvice(GithubRepo repo, String courseName, 
            String lang, double impactScore, int downstreamCount) {
        String langLower = lang.toLowerCase();
        int stars = repo.getStars() != null ? repo.getStars() : 0;

        StringBuilder sb = new StringBuilder();
        sb.append("🎯 **CHIẾN LƯỢC HỌC TẬP**\n\n");

        // ============ MỨC ĐỘ ƯU TIÊN ============
        sb.append("📊 **Mức độ ưu tiên**\n\n");

        if (impactScore > 7.0) {
            sb.append("🔴 **Mức ưu tiên: Rất cao**\n\n");
            sb.append(String.format(
                "Đây là môn **nền tảng cốt lõi**, ảnh hưởng đến **%d môn** phía sau. " +
                "Dành thời gian nghiên cứu kỹ repository này, vì kiến thức ở đây sẽ " +
                "theo bạn suốt chương trình học.\n\n", downstreamCount));
        } else if (impactScore > 4.0) {
            sb.append("🟡 **Mức ưu tiên: Trung bình**\n\n");
            sb.append("Môn này có liên quan đến một số môn học khác. " +
                     "Nắm vững kiến thức nền tảng trước khi đi sâu vào codebase.\n\n");
        } else {
            sb.append("🟢 **Mức ưu tiên: Cơ bản**\n\n");
            sb.append("Môn học độc lập, không ảnh hưởng nhiều đến các môn khác. " +
                     "Tập trung hiểu rõ nội dung hiện tại.\n\n");
        }

        // ============ LỘ TRÌNH HỌC ============
        sb.append("📋 **Lộ trình học với repository này**\n\n");

        // Bước 1
        sb.append("**Bước 1 — Tiếp cận:**\n");
        sb.append("- Clone hoặc fork repository về máy\n");
        sb.append("- Đọc file README.md (nếu có) để hiểu tổng quan project\n");
        sb.append("- Xem cấu trúc thư mục để nắm cách tổ chức mã nguồn\n");
        sb.append("- Chạy thử ứng dụng (nếu có hướng dẫn) để thấy kết quả trực quan\n\n");

        // Bước 2 — phân tích kỹ thuật theo ngôn ngữ
        sb.append("**Bước 2 — Phân tích kỹ thuật:**\n");

        if (langLower.contains("c") || langLower.contains("c++") || langLower.contains("rust")) {
            sb.append(String.format(
                "- Repository viết bằng **%s**. Chú ý cách quản lý bộ nhớ (malloc/free, ownership)\n", lang));
            sb.append("- Xem cấu trúc file header (.h) và file triển khai (.c/.cpp)\n");
            sb.append("- Tìm hiểu cách tổ chức module và hàm\n");
            sb.append("- Chú ý các cấu trúc dữ liệu được định nghĩa (struct, typedef)\n");
        } else if (langLower.contains("java")) {
            sb.append("- Repository viết bằng **Java**. Chú ý cách tổ chức package theo MVC hoặc layered architecture\n");
            sb.append("- Xem cách sử dụng OOP: kế thừa, interface, abstract class\n");
            sb.append("- Tìm hiểu dependency injection và design pattern được áp dụng\n");
            sb.append("- Nếu có Spring: chú ý annotation, bean configuration\n");
        } else if (langLower.contains("python")) {
            sb.append("- Repository viết bằng **Python**. Chú ý cấu trúc module và import\n");
            sb.append("- Xem cách tổ chức project: có dùng virtual environment không?\n");
            sb.append("- Tìm hiểu cách viết test (pytest/unittest) nếu có\n");
            sb.append("- Chú ý docstring và type hint để hiểu rõ API\n");
        } else if (langLower.contains("javascript") || langLower.contains("typescript")) {
            sb.append(String.format(
                "- Repository viết bằng **%s**. Chú ý cấu trúc thành phần (components)\n", lang));
            sb.append("- Xem cách quản lý state và luồng dữ liệu\n");
            sb.append("- Tìm hiểu các thư viện/framework được sử dụng (React, Vue, Express...)\n");
            sb.append("- Nếu có TypeScript: chú ý type definition và interface\n");
        } else if (langLower.contains("kotlin")) {
            sb.append("- Repository viết bằng **Kotlin**. Chú ý cú pháp hiện đại so với Java\n");
            sb.append("- Xem cách sử dụng coroutine cho bất đồng bộ\n");
            sb.append("- Tìm hiểu Android architecture components (ViewModel, LiveData/Flow)\n");
        } else if (langLower.contains("sql") || langLower.contains("r") || langLower.contains("data")) {
            sb.append(String.format(
                "- Repository tập trung vào **%s** và xử lý dữ liệu\n", lang));
            sb.append("- Xem cách truy vấn và biến đổi dữ liệu\n");
            sb.append("- Tìm hiểu pipeline xử lý và trực quan hóa\n");
        } else {
            sb.append(String.format(
                "- Repository viết bằng **%s**. Tìm hiểu cú pháp và cấu trúc project\n", lang));
            sb.append("- Xem cách tổ chức file và module\n");
            sb.append("- Chú ý các thư viện/phụ thuộc được sử dụng\n");
        }
        sb.append("\n");

        // Bước 3 — thực hành
        sb.append("**Bước 3 — Thực hành:**\n");
        if (stars >= 10) {
            sb.append("- Repository có cộng đồng active, hãy xem issues và discussions\n");
        }
        sb.append("- Thử chạy các test cases (nếu có)\n");
        sb.append("- Tự viết thêm chức năng mới để thực hành\n");
        sb.append("- Ghi chú lại các pattern hay để áp dụng cho dự án cá nhân\n\n");

        // ============ TIPS RIÊNG ============
        sb.append("💡 **Mẹo nhỏ**\n\n");
        if (repo.getTechStacks() != null && !repo.getTechStacks().isEmpty()) {
            String stacks = repo.getTechStacks().stream()
                .map(ts -> "`" + ts.getName() + "`")
                .collect(Collectors.joining(", "));
            sb.append(String.format(
                "Repository này sử dụng các công nghệ: %s. " +
                "Hãy tìm hiểu sơ qua từng công nghệ trước khi đọc sâu.\n", stacks));
        }
        sb.append("- Đừng ngại thử nghiệm: fork và sửa code để xem tác động\n");
        sb.append("- Nếu gặp khó khăn, hãy đọc phần test (nếu có) để hiểu cách hoạt động\n");

        if (stars >= 10) {
            sb.append("- Repository có cộng đồng active, hãy xem issues và discussions\n");
        }

        return new AiResponse(sb.toString(), "TUTOR_ADVICE");
    }
}
