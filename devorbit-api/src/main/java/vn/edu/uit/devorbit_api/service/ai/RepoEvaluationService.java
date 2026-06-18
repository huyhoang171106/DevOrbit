package vn.edu.uit.devorbit_api.service.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.entity.Course;
import vn.edu.uit.devorbit_api.entity.GithubRepo;
import vn.edu.uit.devorbit_api.entity.TechStack;
import vn.edu.uit.devorbit_api.repository.GithubRepoRepository;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepoEvaluationService {

    private final GithubRepoRepository githubRepoRepository;

    private static final Pattern PROJECT_CONFIG_PATTERN = Pattern.compile(
            "(^|/)(package(-lock)?\\.json|pnpm-lock\\.yaml|yarn\\.lock|bun\\.lock|requirements\\.txt|pyproject\\.toml|poetry\\.lock|pom\\.xml|build\\.gradle|settings\\.gradle|gradle\\.properties|pubspec\\.yaml|composer\\.json|go\\.mod|cargo\\.toml|[^/]+\\.(csproj|sln))$",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern BUILD_FILE_PATTERN = Pattern.compile(
            "(^|/)(makefile|cmakelists\\.txt|mvnw|gradlew|dockerfile)$",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern SOURCE_FOLDER_PATTERN = Pattern.compile(
            "(^|/)(src|source|app|lib|components|controllers|services|models)(/|$)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern SOURCE_FILE_PATTERN = Pattern.compile(
            ".*\\.(c|cc|cpp|cs|dart|go|h|hpp|ipynb|java|js|jsx|kt|m|php|py|rb|rs|sql|swift|ts|tsx)$",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern TEST_PATH_PATTERN = Pattern.compile(
            "(^|/)(__tests__|tests?|spec|input|output|sample)(/|$)|(\\.|-)(test|spec)\\.[a-z0-9]+$",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern FOUNDATION_ALGORITHM_PATTERN = Pattern.compile(
            "\\b(it003|dsa|ctdl|data structures?|cau truc du lieu|giai thuat|algorithms?|lap trinh|co so lap trinh|programming fundamentals|stack|queue|tree|graph|sorting)\\b",
            Pattern.CASE_INSENSITIVE
    );

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {
        log.info("RepoEvaluationService: Auto-running evaluations on startup...");
        evaluateAndSaveAll();
    }

    public void evaluateAndSaveAll() {
        try {
            List<GithubRepo> repos = githubRepoRepository.findAll();
            log.info("Starting evaluation for {} repositories...", repos.size());
            int count = 0;
            for (GithubRepo repo : repos) {
                evaluateRepo(repo);
                githubRepoRepository.save(repo);
                count++;
            }
            log.info("Successfully evaluated and updated {} repositories.", count);
        } catch (Exception e) {
            log.error("Failed to run batch evaluation: {}", e.getMessage(), e);
        }
    }

    public void evaluateRepo(GithubRepo repo) {
        // 1. Extract Signals
        RepoSignals signals = extractRepoSignals(repo);

        // 2. Classify Repo Type
        String repoType = classifyRepoType(signals);

        // 3. Detect Course Group
        String courseGroup = detectCourseGroup(signals);

        // 4. Calculate Ready to Use level & stars
        int readyStars = calculateReadyToUseStars(repoType, courseGroup, signals);
        String readyToUseLevel = buildReadyToUseLevel(readyStars);

        // 5. Calculate Usefulness Score
        int usefulnessScore = calculateUsefulnessScore(repoType, courseGroup, readyStars, signals);

        // 6. Map to Usefulness Rating
        String usefulnessRating = ratingFromScore(usefulnessScore, repoType);

        // 7. Save to Repo
        repo.setRepoType(repoType);
        repo.setUsefulnessRating(usefulnessRating);
        repo.setUsefulnessScore(usefulnessScore);
        repo.setReadyToUseLevel(readyToUseLevel);
    }

    private RepoSignals extractRepoSignals(GithubRepo repo) {
        String name = repo.getDisplayName() != null ? repo.getDisplayName() : "";
        String description = repo.getDescription() != null ? repo.getDescription() : "";
        String primaryLanguage = repo.getPrimaryLanguage() != null ? repo.getPrimaryLanguage() : "";
        
        List<String> techStacks = repo.getTechStacks() != null
                ? repo.getTechStacks().stream().map(TechStack::getName).toList()
                : Collections.emptyList();

        String readmeText = repo.getReadmeExcerpt() != null ? repo.getReadmeExcerpt() : "";
        Boolean hasReadme = repo.getHasReadme() != null ? repo.getHasReadme() : false;

        List<String> filePaths = new ArrayList<>();
        if (repo.getFileTree() != null && !repo.getFileTree().isBlank()) {
            filePaths = Arrays.stream(repo.getFileTree().split("\n"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }

        Course course = repo.getCourse();
        String courseCode = course != null ? course.getMaMH() : "";
        String courseName = course != null ? course.getTenMH() : "";

        // Combine text for searching keywords
        List<String> textParts = new ArrayList<>();
        textParts.add(courseCode);
        textParts.add(courseName);
        textParts.add(name);
        textParts.add(description);
        textParts.add(readmeText);
        textParts.add(primaryLanguage);
        textParts.addAll(techStacks);
        textParts.addAll(filePaths);
        String haystack = normalizeSearchText(textParts);

        boolean hasFileList = !filePaths.isEmpty();
        boolean hasPackageFile = hasPathMatch(filePaths, PROJECT_CONFIG_PATTERN);
        boolean hasBuildFile = hasPackageFile || hasPathMatch(filePaths, BUILD_FILE_PATTERN);
        boolean hasEnvExample = hasPathMatch(filePaths, Pattern.compile("(^|/)\\.env\\.(example|sample|template)$", Pattern.CASE_INSENSITIVE));
        boolean hasDockerConfig = hasPathMatch(filePaths, Pattern.compile("(^|/)(docker-compose\\.ya?ml|dockerfile)$", Pattern.CASE_INSENSITIVE));
        boolean hasSourceCode = !primaryLanguage.isEmpty() || !techStacks.isEmpty() ||
                hasPathMatch(filePaths, SOURCE_FOLDER_PATTERN) || hasPathMatch(filePaths, SOURCE_FILE_PATTERN);
        
        boolean hasTests = hasPathMatch(filePaths, TEST_PATH_PATTERN) || 
                containsKeyword(haystack, "\\b(test|tests|unit test|input|output|sample)\\b");
        
        boolean hasAssignments = containsKeyword(haystack, "\\b(lab|labs|assignment|assignments|exercise|exercises|homework|practice|practical|bai tap|bài tập|thuc hanh|thực hành|dsa|algorithm|oop)\\b");
        boolean hasSolutions = containsKeyword(haystack, "\\b(solution|solutions|answer|answers|loi giai|lời giải)\\b") ||
                hasPathMatch(filePaths, Pattern.compile("(^|/)(solution|solutions|answer|answers)(/|$)", Pattern.CASE_INSENSITIVE));
        
        boolean hasSlides = containsKeyword(haystack, "\\b(slide|slides|ppt|pptx|lecture|lectures)\\b") ||
                hasPathMatch(filePaths, Pattern.compile("(^|/)(slides?|lectures?)(/|$)")) ||
                filePaths.stream().anyMatch(p -> p.endsWith(".ppt") || p.endsWith(".pptx"));

        boolean hasNotes = containsKeyword(haystack, "\\b(note|notes|document|docs|theory|ly thuyet|lý thuyết|summary|cheatsheet|giao trinh|giáo trình)\\b");
        boolean hasDocs = hasNotes || hasPathMatch(filePaths, Pattern.compile("(^|/)(docs?|documents?|notes?)(/|$)", Pattern.CASE_INSENSITIVE));
        
        boolean hasExam = containsKeyword(haystack, "\\b(exam|exams|midterm|final|quiz|past exam|de thi|đề thi|on tap|ôn tập)\\b");
        boolean hasAnswerOrSolution = hasSolutions || containsKeyword(haystack, "\\b(answer key|dap an|đáp án)\\b");
        boolean hasLicense = hasPathMatch(filePaths, Pattern.compile("(^|/)licen[cs]e(\\.md|\\.txt)?$", Pattern.CASE_INSENSITIVE));

        // count top level folders
        long topLevelCount = filePaths.stream()
                .map(p -> p.replace('\\', '/'))
                .map(p -> p.split("/")[0])
                .distinct()
                .count();
        boolean organizedFolders = hasFileList && topLevelCount >= 3;

        return new RepoSignals(
                name, description, primaryLanguage, techStacks, readmeText, courseCode, courseName,
                hasReadme, hasFileList, filePaths, hasSourceCode, hasTests, hasDocs, hasAssignments,
                hasSolutions, hasSlides, hasNotes, hasExam, hasAnswerOrSolution, hasPackageFile,
                hasBuildFile, hasEnvExample, hasDockerConfig, hasLicense, organizedFolders, haystack
        );
    }

    private String classifyRepoType(RepoSignals signals) {
        String text = signals.haystack;
        boolean hasFoundationAlgorithmContext = FOUNDATION_ALGORITHM_PATTERN.matcher(text).find();

        int programmingExerciseScore = sumScores(
                signals.hasAssignments,
                signals.hasSourceCode && Pattern.compile("\\b(code|programming|algorithms?|dsa|data structures?|ctdl|giai thuat|oop|java|python|c\\+\\+|cpp|c#)\\b", Pattern.CASE_INSENSITIVE).matcher(text).find(),
                signals.hasSourceCode && hasFoundationAlgorithmContext,
                signals.hasTests,
                signals.hasSolutions
        );

        int projectPracticeScore = sumScores(
                Pattern.compile("\\b(project|web|frontend|backend|fullstack|api|database|mobile|app|demo|spring|react|android|server)\\b", Pattern.CASE_INSENSITIVE).matcher(text).find(),
                signals.hasPackageFile || signals.hasBuildFile,
                signals.hasDockerConfig || signals.hasEnvExample,
                signals.hasSourceCode && !signals.techStacks.isEmpty()
        );

        int studyMaterialScore = sumScores(
                signals.hasSlides,
                signals.hasNotes,
                signals.hasDocs,
                Pattern.compile("\\b(lecture|slides|note|document|theory|material|course|chuong|chapter)\\b", Pattern.CASE_INSENSITIVE).matcher(text).find()
        );

        int examReviewScore = sumScores(
                signals.hasExam,
                signals.hasAnswerOrSolution,
                Pattern.compile("\\b(midterm|final|quiz|exam|de thi|đề thi|answer|solution)\\b", Pattern.CASE_INSENSITIVE).matcher(text).find()
        );

        // Classify decision logic
        if (signals.hasAssignments && signals.hasSourceCode && 
                !Pattern.compile("\\b(do an|project|fullstack|backend|frontend|api|server)\\b", Pattern.CASE_INSENSITIVE).matcher(text).find()) {
            return "programming_exercise";
        }

        // Find max score
        Map<String, Integer> scoreMap = new HashMap<>();
        scoreMap.put("programming_exercise", programmingExerciseScore);
        scoreMap.put("project_practice", projectPracticeScore);
        scoreMap.put("study_material", studyMaterialScore);
        scoreMap.put("exam_review", examReviewScore);

        String maxType = "unknown";
        int maxVal = 0;
        for (var entry : scoreMap.entrySet()) {
            if (entry.getValue() > maxVal) {
                maxVal = entry.getValue();
                maxType = entry.getKey();
            }
        }

        // Check overlap for mixed_resource
        List<String> strongTypes = new ArrayList<>();
        for (var entry : scoreMap.entrySet()) {
            if (entry.getValue() >= 2) {
                strongTypes.add(entry.getKey());
            }
        }
        if (strongTypes.size() >= 2 && maxVal <= 3) {
            // Check if gap is small
            strongTypes.sort((a, b) -> scoreMap.get(b) - scoreMap.get(a));
            if (scoreMap.get(strongTypes.get(0)) - scoreMap.get(strongTypes.get(1)) <= 1) {
                return "mixed_resource";
            }
        }

        if (maxVal >= 2) {
            return maxType;
        }

        if (hasFoundationAlgorithmContext && signals.hasSourceCode) {
            return "programming_exercise";
        }

        if (signals.hasSourceCode && (!signals.description.isEmpty() || !signals.techStacks.isEmpty())) {
            return "project_practice";
        }

        return "unknown";
    }

    private String detectCourseGroup(RepoSignals signals) {
        String text = signals.haystack;
        if (Pattern.compile("\\b(ss004|ky nang|soft skill|english|anh van|xac suat|thong ke|phuong phap|seminar|chuyen de|cv|presentation)\\b", Pattern.CASE_INSENSITIVE).matcher(text).find()) {
            return "general_skills";
        }
        if (Pattern.compile("\\b(ooad|hci|srs|sad|uml|use case|erd|figma|prototype|software engineering|cong nghe phan mem|kien truc phan mem|thiet ke|dac ta)\\b", Pattern.CASE_INSENSITIVE).matcher(text).find()) {
            return "design_process";
        }
        if (Pattern.compile("\\b(web|mobile|android|flutter|react|spring|\\.net|dotnet|game|cloud|do an|khoa luan|fullstack|backend|frontend|deployment)\\b", Pattern.CASE_INSENSITIVE).matcher(text).find()) {
            return "software_project";
        }
        if (Pattern.compile("\\b(dsa|ctdl|giai thuat|algorithms?|data structures?|lap trinh|co so du lieu|database|csdl|he dieu hanh|operating system|cau truc roi rac|to chuc may tinh)\\b", Pattern.CASE_INSENSITIVE).matcher(text).find()) {
            return "foundation_algorithms";
        }
        return "unknown";
    }

    private int calculateReadyToUseStars(String repoType, String courseGroup, RepoSignals signals) {
        if ("unknown".equals(repoType) && signals.filePaths.size() <= 2) {
            return 1;
        }
        int score = 0;
        if ("programming_exercise".equals(repoType)) {
            score = sumScores(
                    signals.hasReadme,
                    signals.hasAssignments,
                    signals.hasSourceCode,
                    signals.hasTests,
                    signals.hasSolutions || signals.organizedFolders,
                    "foundation_algorithms".equals(courseGroup) && signals.hasFileList
            );
        } else if ("project_practice".equals(repoType)) {
            boolean hasRunKeyword = Pattern.compile("\\b(run|setup|install|database|migration|npm|mvn|gradle|docker)\\b", Pattern.CASE_INSENSITIVE).matcher(signals.readmeText).find();
            score = sumScores(
                    signals.hasReadme,
                    signals.hasPackageFile,
                    signals.hasEnvExample || signals.hasDockerConfig,
                    signals.hasBuildFile,
                    signals.hasDocs || hasRunKeyword,
                    signals.hasSourceCode
            );
        } else if ("study_material".equals(repoType)) {
            score = sumScores(
                    signals.hasReadme,
                    signals.hasSlides,
                    signals.hasNotes || signals.hasDocs,
                    signals.organizedFolders,
                    signals.hasFileList
            );
        } else if ("exam_review".equals(repoType)) {
            boolean hasYearKeyword = Pattern.compile("\\b(20\\d{2}|midterm|final|cuoi ky|giua ky)\\b", Pattern.CASE_INSENSITIVE).matcher(signals.haystack).find();
            score = sumScores(
                    signals.hasExam,
                    signals.hasAnswerOrSolution,
                    signals.hasFileList,
                    hasYearKeyword,
                    signals.hasReadme
            );
        } else if ("general_skills".equals(courseGroup) || "design_process".equals(courseGroup)) {
            boolean hasRubric = Pattern.compile("rubric|guideline|assignment", Pattern.CASE_INSENSITIVE).matcher(signals.haystack).find();
            boolean hasReport = Pattern.compile("report|bao cao|docx|pdf", Pattern.CASE_INSENSITIVE).matcher(signals.haystack).find();
            boolean hasSlide = Pattern.compile("slide|presentation|pptx", Pattern.CASE_INSENSITIVE).matcher(signals.haystack).find();
            score = sumScores(
                    signals.hasReadme,
                    hasRubric,
                    hasReport,
                    hasSlide,
                    signals.organizedFolders || signals.hasFileList
            );
        } else {
            score = sumScores(
                    signals.hasReadme,
                    signals.hasFileList,
                    !signals.description.isEmpty(),
                    !signals.techStacks.isEmpty(),
                    !signals.primaryLanguage.isEmpty()
            );
        }
        return Math.max(1, Math.min(5, score));
    }

    private String buildReadyToUseLevel(int stars) {
        if (stars >= 5) return "very_ready";
        if (stars >= 4) return "ready";
        if (stars >= 3) return "needs_check";
        if (stars >= 2) return "quick_reference";
        return "insufficient_data";
    }

    private int calculateUsefulnessScore(String repoType, String courseGroup, int readyStars, RepoSignals signals) {
        boolean hasCourseContext = !"unknown".equals(courseGroup);
        if ("unknown".equals(repoType) && signals.description.isEmpty() && !signals.hasReadme && !signals.hasFileList && !hasCourseContext) {
            return 10;
        }
        int value = readyStars * 13;
        value += sumScoresMultiplier(
                new Object[]{!signals.description.isEmpty(), 10},
                new Object[]{signals.hasFileList, 12},
                new Object[]{signals.hasReadme, 12},
                new Object[]{hasCourseContext, 10}
        );

        if ("programming_exercise".equals(repoType)) {
            value += sumScoresMultiplier(
                    new Object[]{signals.hasSourceCode, 8},
                    new Object[]{signals.hasTests, 10},
                    new Object[]{signals.hasSolutions, 8},
                    new Object[]{signals.hasAssignments, 10},
                    new Object[]{"foundation_algorithms".equals(courseGroup), 8}
            );
        }
        if ("project_practice".equals(repoType)) {
            value += sumScoresMultiplier(
                    new Object[]{signals.hasSourceCode, 8},
                    new Object[]{signals.hasPackageFile, 10},
                    new Object[]{signals.hasEnvExample || signals.hasDockerConfig, 10},
                    new Object[]{signals.techStacks.size() >= 2, 8}
            );
        }
        if ("study_material".equals(repoType)) {
            value += sumScoresMultiplier(
                    new Object[]{signals.hasSlides, 10},
                    new Object[]{signals.hasNotes || signals.hasDocs, 10},
                    new Object[]{signals.organizedFolders, 6}
            );
        }
        if ("exam_review".equals(repoType)) {
            value += sumScoresMultiplier(
                    new Object[]{signals.hasExam, 12},
                    new Object[]{signals.hasAnswerOrSolution, 12}
            );
            if (signals.hasExam && signals.hasAnswerOrSolution) {
                value += 8;
            }
        }
        if (("general_skills".equals(courseGroup) || "design_process".equals(courseGroup)) && !"project_practice".equals(repoType)) {
            value += sumScoresMultiplier(
                    new Object[]{signals.hasDocs, 8},
                    new Object[]{signals.hasSlides, 8}
            );
        }

        // Deductions
        if ("programming_exercise".equals(repoType)) {
            value -= sumScoresMultiplier(
                    new Object[]{!signals.hasReadme, 8},
                    new Object[]{!signals.hasTests, 10}
            );
        }
        if ("project_practice".equals(repoType)) {
            value -= sumScoresMultiplier(
                    new Object[]{!signals.hasReadme, 8},
                    new Object[]{!signals.hasPackageFile, 8},
                    new Object[]{!signals.hasEnvExample && !signals.hasDockerConfig, 6}
            );
        }

        return Math.max(0, Math.min(100, value));
    }

    private String ratingFromScore(int value, String repoType) {
        // Thresholds widened to match frontend (recommended window was 4pts at 64-67 → now 10pts at 58-67)
        if ("unknown".equals(repoType) && value < 30) return "insufficient_data";
        if ("exam_review".equals(repoType) && value >= 56) return "highly_recommended";
        if (value >= 68) return "highly_recommended";
        if (value >= 58) return "recommended";
        if (value >= 34) return "selective";
        if (value >= 20) return "quick_reference";
        return "low_priority";
    }

    // --- Helper methods ---

    private boolean hasPathMatch(List<String> filePaths, Pattern pattern) {
        return filePaths.stream().anyMatch(path -> pattern.matcher(path).find());
    }

    private boolean containsKeyword(String haystack, String regex) {
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(haystack).find();
    }

    private String normalizeSearchText(List<String> parts) {
        return parts.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "))
                .toLowerCase()
                .trim();
    }

    private int sumScores(boolean... conditions) {
        int sum = 0;
        for (boolean c : conditions) {
            if (c) sum += 1;
        }
        return sum;
    }

    private int sumScoresMultiplier(Object[]... pairs) {
        int sum = 0;
        for (Object[] pair : pairs) {
            boolean cond = (boolean) pair[0];
            int val = (int) pair[1];
            if (cond) sum += val;
        }
        return sum;
    }

    private record RepoSignals(
            String name, String description, String primaryLanguage, List<String> techStacks,
            String readmeText, String courseCode, String courseName, boolean hasReadme,
            boolean hasFileList, List<String> filePaths, boolean hasSourceCode, boolean hasTests,
            boolean hasDocs, boolean hasAssignments, boolean hasSolutions, boolean hasSlides,
            boolean hasNotes, boolean hasExam, boolean hasAnswerOrSolution, boolean hasPackageFile,
            boolean hasBuildFile, boolean hasEnvExample, boolean hasDockerConfig, boolean hasLicense,
            boolean organizedFolders, String haystack
    ) {}
}
