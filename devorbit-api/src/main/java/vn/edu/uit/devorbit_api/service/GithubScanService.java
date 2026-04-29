package vn.edu.uit.devorbit_api.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import vn.edu.uit.devorbit_api.config.GithubProperties;
import vn.edu.uit.devorbit_api.dto.admin.RepoCandidateResponse;
import vn.edu.uit.devorbit_api.entity.Course;
import vn.edu.uit.devorbit_api.entity.RepoCandidate;
import vn.edu.uit.devorbit_api.entity.RepoCandidateStatus;
import vn.edu.uit.devorbit_api.exception.BadRequestException;
import vn.edu.uit.devorbit_api.repository.CourseRepository;
import vn.edu.uit.devorbit_api.repository.GithubRepoRepository;
import vn.edu.uit.devorbit_api.repository.RepoCandidateRepository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GithubScanService {

    private final RepoCandidateRepository repoCandidateRepository;
    private final CourseRepository courseRepository;
    private final GithubRepoRepository githubRepoRepository;
    private final GithubProperties githubProperties;

    public List<RepoCandidateResponse> scan(vn.edu.uit.devorbit_api.dto.admin.GithubScanRequest request) {
        Course course = courseRepository.findById(request.courseId())
            .orElseThrow(() -> new BadRequestException("Course not found: " + request.courseId()));

        WebClient client = WebClient.builder()
            .baseUrl("https://api.github.com")
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + githubProperties.token())
            .defaultHeader(HttpHeaders.USER_AGENT, "DevOrbit/1.0")
            .build();

        try {
            String json = client.get()
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

            List<RepoCandidateResponse> results = new ArrayList<>();
            for (JsonNode item : items) {
                String fullName = item.get("full_name").asText();
                String htmlUrl = item.get("html_url").asText();
                String[] parts = fullName.split("/");
                String owner = parts[0];
                String name = parts[1];

                Optional<RepoCandidate> existing = repoCandidateRepository
                    .findByGithubUrlAndCourseId(htmlUrl, request.courseId());
                if (existing.isPresent()) continue;

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

        } catch (Exception e) {
            throw new RuntimeException("GitHub search failed", e);
        }
    }
}
