package vn.edu.uit.devorbit_api.service;


import lombok.RequiredArgsConstructor;
import vn.edu.uit.devorbit_api.entity.GithubRepo;
import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.repository.GithubRepoRepository;

import java.util.List;
@Service
@RequiredArgsConstructor
public class GithubRepoService {
    private final GithubRepoRepository githubRepoRepository;
    public List<GithubRepo> getAllGithubRepo() {
        return githubRepoRepository.findAll();
    }

    public List<GithubRepo> getReposBySubject(String subjectId) {
        return githubRepoRepository.findBySubjectId(subjectId);
    }
}
