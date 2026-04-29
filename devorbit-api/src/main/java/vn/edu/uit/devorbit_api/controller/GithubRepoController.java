package vn.edu.uit.devorbit_api.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.uit.devorbit_api.entity.GithubRepo;
import vn.edu.uit.devorbit_api.service.GithubRepoService;
import vn.edu.uit.devorbit_api.repository.CourseRepository;

import java.util.List;
@RestController
@RequestMapping("/repos")
@RequiredArgsConstructor
public class GithubRepoController {

    private final GithubRepoService githubRepoService;
    @GetMapping
    public List<GithubRepo> getAllGithubRepo() {
        return githubRepoService.getAllGithubRepo();
    }

    @GetMapping("/subject/{subjectId}")
    public List<GithubRepo> getAllGithubRepoBySubjectId(@PathVariable String subjectId) {
        return githubRepoService.getReposBySubject(subjectId);
    }
}