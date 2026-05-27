package vn.edu.uit.devorbit_api.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.uit.devorbit_api.service.RepoCandidateService;

@Component
@RequiredArgsConstructor
public class RepoCandidateDataInitializer {
    private final RepoCandidateService repoCandidateService;

    @PostConstruct
    public void init() {
        // repoCandidateService.distributeCandidates();
    }
}
