package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.uit.devorbit_api.dto.publicapi.RepoSummaryResponse;
import vn.edu.uit.devorbit_api.repository.GithubRepoRepository;
import vn.edu.uit.devorbit_api.repository.TechStackRepository;
import vn.edu.uit.devorbit_api.service.GithubRepoService;

import java.util.List;

/**
 * PUBLIC DISCOVERY CONTROLLER = khám phá repo mới và công nghệ phổ biến (public).
 *
 * Được trang "Khám phá" ở frontend sử dụng để hiển thị:
 * - Repo GitHub mới thêm gần đây
 * - Công nghệ phổ biến nhất
 *
 * Endpoints:
 *   GET /api/discovery/recent-repos — 10 repo mới nhất
 *   GET /api/discovery/repos        — tất cả repo đã duyệt
 *   GET /api/discovery/top-stacks   — 10 công nghệ được dùng nhiều nhất
 *
 * Authentication: không yêu cầu (public)
 */
@RestController
@RequestMapping("/api/discovery")
@RequiredArgsConstructor
public class PublicDiscoveryController {

    private final GithubRepoRepository repoRepository;
    private final TechStackRepository techStackRepository;
    private final GithubRepoService githubRepoService;

    /** GET 10 repo hoạt động gần đây nhất */
    @GetMapping("/recent-repos")
    public List<RepoSummaryResponse> getRecentRepos() {
        return repoRepository.findTop10ByActiveTrueOrderByIdDesc().stream()
                .map(githubRepoService::mapToRepoSummary)
                .toList();
    }

    /** GET tất cả repo đã duyệt (dùng cho tìm kiếm xuyên khoá học) */
    @GetMapping("/repos")
    public List<RepoSummaryResponse> getAllRepos() {
        return githubRepoService.getAllApprovedRepos();
    }

    /** GET 10 công nghệ được sử dụng nhiều nhất */
    @GetMapping("/top-stacks")
    public List<String> getTopStacks() {
        return techStackRepository.findTop10TechStacksByUsage();
    }
}
