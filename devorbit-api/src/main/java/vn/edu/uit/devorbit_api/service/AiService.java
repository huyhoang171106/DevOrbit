package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.dto.publicapi.AiResponse;
import vn.edu.uit.devorbit_api.entity.GithubRepo;
import vn.edu.uit.devorbit_api.repository.GithubRepoRepository;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiService {
    private final GithubRepoRepository githubRepoRepository;

    public AiResponse getRepoAiSummary(Long repoId) {
        GithubRepo repo = githubRepoRepository.findById(repoId)
            .orElseThrow(() -> new RuntimeException("Repo not found"));

        String techStr = repo.getTechStacks().stream()
            .map(ts -> ts.getName())
            .collect(Collectors.joining(", "));

        String summary = String.format(
            "Dựa trên phân tích mã nguồn, repository '%s' là một dự án phát triển bằng %s. " +
            "Hệ sinh thái công nghệ bao gồm: %s. " +
            "Dự án này tập trung vào việc áp dụng các nguyên lý lập trình hiện đại và cấu trúc thư mục chuẩn cho sinh viên UIT.",
            repo.getDisplayName(), repo.getPrimaryLanguage(), techStr
        );

        return new AiResponse(summary, "SUMMARY");
    }

    public AiResponse getTutorAdvice(Long repoId) {
        GithubRepo repo = githubRepoRepository.findById(repoId)
            .orElseThrow(() -> new RuntimeException("Repo not found"));

        String advice = String.format(
            "LỜI KHUYÊN TỪ AI TUTOR: Để làm chủ dự án này, bạn nên tập trung học kỹ về %s. " +
            "Hãy thử tìm hiểu cách dự án tổ chức logic xử lý và cách kết nối các thành phần công nghệ với nhau. " +
            "Đây là một ví dụ tuyệt vời về cách vận dụng lý thuyết vào thực tế tại UIT.",
            repo.getPrimaryLanguage()
        );

        return new AiResponse(advice, "TUTOR_ADVICE");
    }
}
