package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.dto.publicapi.AiResponse;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.entity.GithubRepo;
import vn.edu.uit.devorbit_api.repository.GithubRepoRepository;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiService {
    private final GithubRepoRepository githubRepoRepository;

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
