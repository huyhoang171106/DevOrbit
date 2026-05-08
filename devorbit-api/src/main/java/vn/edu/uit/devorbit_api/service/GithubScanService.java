package vn.edu.uit.devorbit_api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import vn.edu.uit.devorbit_api.dto.admin.GithubScanRequest;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.StreamSupport;

@Service
public class GithubScanService {

    private static final int MAX_LOG_ENTRIES = 2000;

    private final RepoCandidateRepository repoCandidateRepository;
    private final GithubRepoRepository githubRepoRepository;
    private final CourseRepository courseRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<String> scanLogs = java.util.Collections.synchronizedList(new ArrayList<>());
    private final AtomicBoolean scanRunning = new AtomicBoolean(false);

    public GithubScanService(RepoCandidateRepository repoCandidateRepository,
                               GithubRepoRepository githubRepoRepository,
                               CourseRepository courseRepository,
                               WebClient githubWebClient) {
        this.repoCandidateRepository = repoCandidateRepository;
        this.githubRepoRepository = githubRepoRepository;
        this.courseRepository = courseRepository;
        this.webClient = githubWebClient;
    }

    public List<String> getScanLogs() {
        return new ArrayList<>(scanLogs);
    }

    public void clearLogs() {
        scanLogs.clear();
    }

    private void addLog(String message) {
        if (scanLogs.size() >= MAX_LOG_ENTRIES) return;
        String timestamp = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        scanLogs.add("[" + timestamp + "] " + message);
    }

    @Transactional
    public List<RepoCandidateResponse> scan(GithubScanRequest request) {
        return scanCourse(request.courseId(), request.query(), null);
    }

    @Transactional
    public List<RepoCandidateResponse> scanCourse(Long courseId, String query, Set<String> globalExistingUrls) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new BadRequestException("Course not found: " + courseId));

        String json = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/search/repositories")
                .queryParam("q", query)
                .queryParam("per_page", 10)
                .build())
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                resp -> resp.bodyToMono(String.class)
                    .map(body -> new BadRequestException("GitHub API error (" + resp.statusCode() + "): " + body)))
            .bodyToMono(String.class)
            .block(Duration.ofSeconds(30));

        JsonNode root;
        try {
            root = objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Invalid GitHub API response");
        }
        JsonNode items = root.path("items");
        if (!items.isArray()) {
            return List.of();
        }

        // Use provided set or fetch if not provided (for manual single scans)
        Set<String> existingUrls = globalExistingUrls;
        if (existingUrls == null) {
            existingUrls = new HashSet<>();
            existingUrls.addAll(repoCandidateRepository.findAllGithubUrls());
            existingUrls.addAll(githubRepoRepository.findAllGithubUrls());
        }

        List<RepoCandidateResponse> results = new ArrayList<>();
        for (JsonNode item : items) {
            String fullName = item.path("full_name").asText(null);
            String htmlUrl = item.path("html_url").asText(null);
            if (fullName == null || htmlUrl == null || !fullName.contains("/")) continue;
            
            // Skip if already exists in either table
            if (existingUrls.contains(htmlUrl)) continue;
            
            String[] parts = fullName.split("/");
            String owner = parts[0];
            String name = parts[1];

            // Add to set to avoid duplicates within the same scan result or subsequent variations
            existingUrls.add(htmlUrl);

            RepoCandidate candidate = RepoCandidate.builder()
                .course(course)
                .scanQuery(query)
                .githubOwner(owner)
                .githubName(name)
                .githubUrl(htmlUrl)
                .description(item.path("description").asText(null))
                .primaryLanguage(item.path("language").asText(null))
                .topics(readTopics(item.path("topics")))
                .stars(item.path("stargazers_count").isMissingNode() ? null : item.path("stargazers_count").asInt(0))
                .forks(item.path("forks_count").isMissingNode() ? null : item.path("forks_count").asInt(0))
                .lastPushedAt(item.path("pushed_at").asText(null))
                .readmeExcerpt(fetchReadmeExcerpt(owner, name))
                .status(RepoCandidateStatus.NEW)
                .build();

            RepoCandidate saved = repoCandidateRepository.save(candidate);
            results.add(RepoCandidateResponse.from(saved));
        }

        return results;
    }

    public boolean isScanRunning() {
        return scanRunning.get();
    }

    public void scanAll() {
        if (!scanRunning.compareAndSet(false, true)) {
            addLog("A scan is already in progress — ignoring duplicate request.");
            return;
        }
        try {
            clearLogs();
            addLog("Starting DEEP bulk scan for all courses...");
            
            // Fetch all existing URLs ONCE at the start for maximum efficiency
            Set<String> existingUrls = new HashSet<>();
            existingUrls.addAll(repoCandidateRepository.findAllGithubUrls());
            existingUrls.addAll(githubRepoRepository.findAllGithubUrls());
            
            List<Course> allCourses = courseRepository.findAll();
            addLog("Found " + allCourses.size() + " courses to process. Each will be scanned with 4 variations.");
            
            int totalFound = 0;
            for (int i = 0; i < allCourses.size(); i++) {
                Course course = allCourses.get(i);
                String code = course.getMaMH();
                String name = course.getTenMH();
                
                // Skip MA and SS courses
                if (code != null && (code.startsWith("MA") || code.startsWith("SS"))) {
                    addLog(String.format("[%d/%d] Skipping non-tech course %s: %s", i + 1, allCourses.size(), code, name));
                    continue;
                }
                
                // Define variations
                String[] variations = {
                    code,                             // Variation 1: Code only
                    code + " " + name,                // Variation 2: Code + Name
                    name + " repository",             // Variation 3: Name + "repository"
                    name + " project"                 // Variation 4: Name + "project"
                };

                addLog(String.format("[%d/%d] >>> Processing %s: %s", i + 1, allCourses.size(), code, name));
                
                for (int v = 0; v < variations.length; v++) {
                    String query = variations[v];
                    try {
                        addLog(String.format("  (%d/4) Searching: \"%s\"", v + 1, query));
                        List<RepoCandidateResponse> found = scanCourse(course.getId(), query, existingUrls);
                        if (!found.isEmpty()) {
                            addLog("    + Found " + found.size() + " new candidates");
                            totalFound += found.size();
                        }
                        
                        // Essential delay to respect GitHub Search API (authenticated limit: 30 requests/min)
                        Thread.sleep(2500);
                    } catch (Exception e) {
                        addLog("    !! Error with variation \"" + query + "\": " + e.getMessage());
                    }
                }
            }
            addLog("DEEP Bulk scan completed. Total new candidates found across all variations: " + totalFound);
        } finally {
            scanRunning.set(false);
        }
    }

    private String readTopics(JsonNode topics) {
        if (!topics.isArray()) return null;
        List<String> values = StreamSupport.stream(topics.spliterator(), false)
                .map(JsonNode::asText)
                .filter(value -> value != null && !value.isBlank())
                .toList();
        return values.isEmpty() ? null : String.join(",", values);
    }

    private String fetchReadmeExcerpt(String owner, String repo) {
        try {
            String readme = webClient.get()
                    .uri("/repos/{owner}/{repo}/readme", owner, repo)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .map(node -> node.path("content").asText(null))
                    .block(Duration.ofSeconds(5));
            if (readme == null || readme.isBlank()) return null;
            String decoded = new String(java.util.Base64.getMimeDecoder().decode(readme)).replaceAll("\\s+", " ").trim();
            return decoded.length() > 500 ? decoded.substring(0, 500) : decoded;
        } catch (Exception ignored) {
            return null;
        }
    }
}
