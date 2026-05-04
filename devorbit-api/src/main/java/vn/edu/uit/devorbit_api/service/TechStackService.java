package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.dto.publicapi.TechStackResponse;
import vn.edu.uit.devorbit_api.repository.TechStackRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TechStackService {
    private final TechStackRepository techStackRepository;

    public List<TechStackResponse> getTechStacksByRepo(Long repoId) {
        var stacks = techStackRepository.findByRepoIdFromJoinTable(repoId);
        if (stacks.isEmpty()) {
            stacks = techStackRepository.findByRepoId(repoId);
        }
        return stacks.stream()
                .map(techStack -> new TechStackResponse(techStack.getName()))
                .toList();
    }

    public List<TechStackResponse> getAllTechStacks() {
        return techStackRepository.findAllDistinctOrderByName().stream()
                .map(techStack -> new TechStackResponse(techStack.getName()))
                .toList();
    }
}
