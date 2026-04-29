package vn.edu.uit.devorbit_api.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import vn.edu.uit.devorbit_api.config.GithubProperties;
import vn.edu.uit.devorbit_api.dto.admin.GithubScanRequest;
import vn.edu.uit.devorbit_api.dto.admin.RepoCandidateResponse;
import vn.edu.uit.devorbit_api.entity.Course;
import vn.edu.uit.devorbit_api.entity.RepoCandidate;
import vn.edu.uit.devorbit_api.entity.RepoCandidateStatus;
import vn.edu.uit.devorbit_api.exception.BadRequestException;
import vn.edu.uit.devorbit_api.repository.CourseRepository;
import vn.edu.uit.devorbit_api.repository.RepoCandidateRepository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GithubScanService {

    private final RepoCandidateRepository repoCandidateRepository;
    private final CourseRepository courseRepository;
    private final GithubProperties githubProperties;
    private final WebClient webClient;

    public GithubScanService(RepoCandidateRepository repoCandidateRepository,
                             CourseRepository courseRepository,
                             GithubProperties githubProperties) {
        this.repoCandidateRepository = repoCandidateRepository;
        this.courseRepository = courseRepository;
        this.githubProperties = githubProperties;
        this.webClient = WebClient.builder()
            .baseUrl("https://api.github.com")
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + githubProperties.token())
            .defaultHeader(HttpHeaders.USER_AGENT, "DevOrbit/1.0")
            .build();
    }

    @Transactional
    public List<RepoCandidateResponse> scan(GithubScanRequest request) {
        Course course = courseRepository.findById(request.courseId())
            .orElseThrow(() -> new BadRequestException("Course not found: " + request.courseId()));

        String json = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/search/repositories")
                .queryParam("q", request.query())
                .queryParam("per_page", 10)
                .build())
            .retrieve()
            .bodyToMono(String.class)
            .block(Duration.ofSeconds(30));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        JsonNode items = root.get("items");

        Set<String> existingUrls = new HashSet<>(repoCandidateRepository.findGithubUrlByCourseId(request.courseId()));

        List<RepoCandidateResponse> results = new ArrayList<>();
        for (JsonNode item : items) {
            String fullName = item.get("full_name").asText();
            String htmlUrl = item.get("html_url").asText();
            String[] parts = fullName.split("/");
            String owner = parts[0];
            String name = parts[1];

            if (existingUrls.contains(htmlUrl)) continue;

            RepoCandidate candidate = RepoCandidate.builder()
                .course(course)
                .scanQuery(request.query())
                .githubOwner(owner)
                .githubName(name)
                .githubUrl(htmlUrl)
                .status(RepoCandidateStatus.NEW)
                .build();

            RepoCandidate saved = repoCandidateRepository.save(candidate);
            results.add(new RepoCandidateResponse(saved.getId(), saved.getGithubOwner(), saved.getGithubName(), saved.getGithubUrl(), saved.getStatus().name()));
        }

        return results;
    }
}
