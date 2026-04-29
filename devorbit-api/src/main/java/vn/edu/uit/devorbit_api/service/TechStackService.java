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
        return techStackRepository.findByRepoId(repoId).stream()
                .map(techStack -> new TechStackResponse(techStack.getName()))
                .toList();
    }
}
