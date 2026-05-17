package vn.edu.uit.devorbit_api.service.ai;

import org.springframework.stereotype.Component;
import vn.edu.uit.devorbit_api.constant.CurriculumConstants;
import vn.edu.uit.devorbit_api.dto.admin.CourseRelationshipResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.KnowledgeGraphResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.RoadmapRecommendationResponse;
import vn.edu.uit.devorbit_api.entity.Course;
import vn.edu.uit.devorbit_api.entity.CourseRelationType;
import vn.edu.uit.devorbit_api.repository.CourseRepository;
import vn.edu.uit.devorbit_api.service.CourseRelationshipService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates personalized learning roadmaps based on career goals.
 * Uses the knowledge graph structure (topological levels) and keyword matching.
 * No LLM required.
 */
@Component
public class RoadmapGenerator {

    private final CourseRepository courseRepository;
    private final CourseRelationshipService relationshipService;
    private final CurriculumMatcher curriculumMatcher;

    // In-memory cache: loaded lazily on first request
    private volatile Map<String, Course> courseCache;
    private volatile List<Course> allCachedCourses;
    private volatile List<CourseRelationshipResponse> relationshipCache;
    private volatile Map<String, Set<String>> prerequisiteCodes;

    public RoadmapGenerator(CourseRepository courseRepository,
                            CourseRelationshipService relationshipService,
                            CurriculumMatcher curriculumMatcher) {
        this.courseRepository = courseRepository;
        this.relationshipService = relationshipService;
        this.curriculumMatcher = curriculumMatcher;
    }

    /** Lazily loads reference data into memory on first use. */
    private void ensureCacheLoaded() {
        if (courseCache != null) return;
        synchronized (this) {
            if (courseCache != null) return;
            List<Course> all = courseRepository.findAll();
            this.allCachedCourses = all;
            this.courseCache = new HashMap<>();
            for (Course c : all) {
                if (c.getMaMH() != null) courseCache.put(c.getMaMH(), c);
            }
            this.relationshipCache = relationshipService.getAll();
            this.prerequisiteCodes = buildPrerequisiteMap();
        }
    }

    public RoadmapRecommendationResponse generate(
            String learningGoals,
            String careerPath,
            KnowledgeGraphResponse graph) {

        // Ensure cached data is loaded
        ensureCacheLoaded();

        List<Course> allCourses = allCachedCourses;
        Map<String, Course> courseMap = courseCache;

        // Build topological levels from graph
        Map<Long, Integer> levels = new HashMap<>();
        for (KnowledgeGraphResponse.GraphNode node : graph.nodes()) {
            levels.put(node.id(), node.level());
        }

        // Separate mandatory courses
        List<Course> mandatory = allCourses.stream()
            .filter(c -> CurriculumConstants.MANDATORY_CODES.contains(c.getMaMH()))
            .collect(Collectors.toCollection(ArrayList::new));

        // Build prerequisite map
        Map<String, Set<String>> prereqMap = this.prerequisiteCodes;

        // Match ALL courses against career path — KTPM electives get +30 priority boost
        List<CurriculumMatcher.ScoredCourse> allMatched = curriculumMatcher.matchCoursesToCareer(
            careerPath,
            allCourses.stream()
                .map(c -> new CurriculumMatcher.CourseInfo(c.getId(), c.getMaMH(), c.getTenMH(), c.getTenMH_EN(), c.getDescription()))
                .toList()
        );

        // Apply priority boost for KTPM curriculum electives, then sort by score descending
        List<CurriculumMatcher.ScoredCourse> prioritized = allMatched.stream()
            .map(sc -> {
                int boost = CurriculumConstants.ELECTIVE_CODES.contains(sc.code()) ? 30 : 0;
                return new CurriculumMatcher.ScoredCourse(sc.id(), sc.code(), sc.name(), sc.description(), sc.score() + boost);
            })
            .sorted((a, b) -> Integer.compare(b.score(), a.score()))
            .toList();

        // === Pool-based elective selection (Khung chương trình KTPM) ===
        Set<String> availableCodes = new HashSet<>(CurriculumConstants.MANDATORY_CODES);
        // 3 elective pools: cơ sở ngành (12TC), chuyên ngành (16TC), tự do (6TC)
        List<Course> coSoNganh = pickToCreditTarget(
            prioritized, CurriculumConstants.CO_SO_NGANH_ELECTIVE,
            CurriculumConstants.CO_SO_NGANH_TARGET_TC, prereqMap, availableCodes, courseMap
        );

        List<Course> chuyenNganh = pickToCreditTarget(
            prioritized, CurriculumConstants.CHUYEN_NGANH_ELECTIVE,
            CurriculumConstants.CHUYEN_NGANH_TARGET_TC, prereqMap, availableCodes, courseMap
        );

        // Tự do: any remaining non-mandatory course not already in elective pools
        Set<String> allKTPMElective = new HashSet<>(CurriculumConstants.CO_SO_NGANH_ELECTIVE);
        allKTPMElective.addAll(CurriculumConstants.CHUYEN_NGANH_ELECTIVE);
        List<Course> tuDo = pickFreeToCreditTarget(
            prioritized, allKTPMElective,
            CurriculumConstants.TU_DO_TARGET_TC, prereqMap, availableCodes, courseMap
        );

        // === Cross-training pool: sau khi đủ các pool KTPM, thêm môn đa dạng từ các ngành khác ===
        // Developer cần kỹ năng mềm, debug, quản lý dự án, kiến thức kinh tế, ngôn ngữ, v.v.
        // Gỡ bỏ giới hạn 130TC, cho phép gen thêm môn để lấp đầy học kỳ trống
        int electiveCreditsSoFar = coSoNganh.stream().mapToInt(Course::getSoTC).sum()
            + chuyenNganh.stream().mapToInt(Course::getSoTC).sum()
            + tuDo.stream().mapToInt(Course::getSoTC).sum();
        int mandatoryTC = mandatory.stream().mapToInt(Course::getSoTC).sum();
        int gradCoursesCredits = mandatory.stream()
            .filter(c -> "SE505".equals(c.getMaMH()) || "SE506".equals(c.getMaMH()) || "SE507".equals(c.getMaMH()))
            .mapToInt(Course::getSoTC).sum();
        int effectiveMandatoryTC = mandatoryTC - gradCoursesCredits + 10;
        int remainingCredits = Math.max(12, 130 - effectiveMandatoryTC - electiveCreditsSoFar + 6);

        List<Course> crossTraining = new ArrayList<>();
        if (remainingCredits > 0) {
            crossTraining = pickCrossTraining(
                prioritized, prereqMap, availableCodes, remainingCredits, courseMap
            );
        }

        // Merge all electives
        List<Course> allSelectedElectives = new ArrayList<>();
        allSelectedElectives.addAll(coSoNganh);
        allSelectedElectives.addAll(chuyenNganh);
        allSelectedElectives.addAll(tuDo);
        allSelectedElectives.addAll(crossTraining);

        // Calculate mandatory credits per semester for capacity planning
        Map<Integer, Integer> mandatoryBySem = new HashMap<>();
        for (Course c : mandatory) {
            Integer sem = CurriculumConstants.CURRICULUM_SEMESTERS.get(c.getMaMH());
            int s = (sem != null) ? sem : 4;
            if (!"SE505".equals(c.getMaMH()) && !"SE506".equals(c.getMaMH()) && !"SE507".equals(c.getMaMH())) {
                mandatoryBySem.merge(s, c.getSoTC(), Integer::sum);
            }
        }

        // Sort electives by earliest computed semester, then distribute evenly
        // across HK4-HK7: fill earlier semesters first (forward), so HK4/HK5 get courses
        allSelectedElectives.sort(Comparator.comparingInt(c ->
            computeSemesterFromPrereqs(c.getMaMH(), prereqMap)
        ));

        Map<Integer, List<CourseElectiveSem>> electivesBySemester = new LinkedHashMap<>();
        Map<Integer, Integer> semesterElectiveCredits = new HashMap<>();

        // Distribute electives: place each course in the semester with the MOST
        // remaining capacity (starting from HK4), so load spreads evenly.
        // No strict TC cap — room up to 26TC/semester.
        int SEM_MAX = 26;
        for (Course c : allSelectedElectives) {
            int placedSem = 4;
            int maxRoom = -1;
            for (int s = 4; s <= 7; s++) {
                int current = mandatoryBySem.getOrDefault(s, 0) + semesterElectiveCredits.getOrDefault(s, 0);
                int room = SEM_MAX - current;
                if (room > maxRoom) {
                    maxRoom = room;
                    placedSem = s;
                }
            }
            electivesBySemester.computeIfAbsent(placedSem, k -> new ArrayList<>())
                .add(new CourseElectiveSem(c, c.getSoTC(), placedSem));
            semesterElectiveCredits.merge(placedSem, c.getSoTC(), Integer::sum);
        }

        // 4. Build recommendations
        List<RoadmapRecommendationResponse.CourseRecommendation> recommendations = new ArrayList<>();

        mandatory.sort(Comparator.comparingInt(c ->
            CurriculumConstants.CURRICULUM_SEMESTERS.getOrDefault(c.getMaMH(), 4))
        );

        for (Course c : mandatory) {
            Integer sem = CurriculumConstants.CURRICULUM_SEMESTERS.get(c.getMaMH());
            int s = (sem != null) ? sem : 4;
            int credits = c.getSoTC();

            // Skip graduation courses — handled via graduation track
            if (c.getMaMH().startsWith("SE50") && credits >= 6) continue;

            String reasoning = String.format(
                "[BẮT BUỘC] Môn **%s** là môn bắt buộc trong chương trình Kỹ thuật Phần mềm. " +
                "Sinh viên phải hoàn thành môn này để đáp ứng yêu cầu tốt nghiệp.",
                c.getTenMH()
            );
            recommendations.add(new RoadmapRecommendationResponse.CourseRecommendation(
                c.getId(), c.getMaMH(), c.getTenMH(), reasoning, c.getDescription(), true, s, credits
            ));
        }

        // Add electives distributed by semester
        for (Map.Entry<Integer, List<CourseElectiveSem>> entry : electivesBySemester.entrySet()) {
            int sem = entry.getKey();
            List<CourseElectiveSem> courses = entry.getValue();
            courses.sort(Comparator.comparingInt(ce -> levels.getOrDefault(ce.course().getId(), 99)));

            for (CourseElectiveSem ce : courses) {
                Course c = ce.course();
                String reasoning = generateElectiveReasoning(c, careerPath);
                String poolLabel;
                if (crossTraining.contains(c)) {
                    poolLabel = "Đào tạo chéo";
                } else if (CurriculumConstants.CO_SO_NGANH_ELECTIVE.contains(c.getMaMH())) {
                    poolLabel = "Cơ sở ngành";
                } else if (CurriculumConstants.CHUYEN_NGANH_ELECTIVE.contains(c.getMaMH())) {
                    poolLabel = "Chuyên ngành";
                } else {
                    poolLabel = "Tự do";
                }
                recommendations.add(new RoadmapRecommendationResponse.CourseRecommendation(
                    c.getId(), c.getMaMH(), c.getTenMH(), reasoning, c.getDescription(), false, ce.semester(), ce.credits()
                ));
            }
        }

        // 5. Calculate semester totals
        Map<Integer, Integer> mandatoryCreditsBySemester = new HashMap<>();
        for (Course c : mandatory) {
            Integer sem = CurriculumConstants.CURRICULUM_SEMESTERS.get(c.getMaMH());
            int s = (sem != null) ? sem : 4;
            if (!"SE505".equals(c.getMaMH()) && !"SE506".equals(c.getMaMH()) && !"SE507".equals(c.getMaMH())) {
                mandatoryCreditsBySemester.merge(s, c.getSoTC(), Integer::sum);
            }
        }

        Map<Integer, Integer> semesterTotals = new TreeMap<>();
        for (int s = 1; s <= 8; s++) {
            int total = mandatoryCreditsBySemester.getOrDefault(s, 0)
                + semesterElectiveCredits.getOrDefault(s, 0);
            if (s == 8) total += 10; // graduation
            semesterTotals.put(s, total);
        }

        // Build summary matching the official curriculum structure
        int daicuongTC = 43; // fixed per curriculum
        int coSoNganhMandatoryTC = mandatoryCreditsBySemester.entrySet().stream()
            .filter(e -> e.getKey() <= 3 || e.getKey() == 4) // HK4 has SE104 part of cơ sở ngành
            .mapToInt(Map.Entry::getValue).sum();
        // Actually calculate properly
        int mandatoryTotalTC = mandatoryCreditsBySemester.values().stream().mapToInt(Integer::intValue).sum();
        int electiveTotalTC = allSelectedElectives.stream().mapToInt(Course::getSoTC).sum();
        int gradTC = 10;
        int grandTotalTC = mandatoryTotalTC + electiveTotalTC + gradTC;

        int coSoNganhElectiveTC = coSoNganh.stream().mapToInt(Course::getSoTC).sum();
        int chuyenNganhElectiveTC = chuyenNganh.stream().mapToInt(Course::getSoTC).sum();
        int tuDoElectiveTC = tuDo.stream().mapToInt(Course::getSoTC).sum();
        int crossTrainingTC = crossTraining.stream().mapToInt(Course::getSoTC).sum();

        String crossTrainingDetail = crossTraining.isEmpty() ? "" :
            String.format("  • Đào tạo chéo (kỹ năng mềm, mở rộng): %d TC\n", crossTrainingTC);

        // Build per-semester table
        StringBuilder semesterTable = new StringBuilder("\n📅 **Phân bổ tín chỉ theo học kỳ:**\n\n");
        String[] semNames = {"", "HK1", "HK2", "HK3", "HK4", "HK5", "HK6", "HK7", "HK8"};
        for (int s = 1; s <= 8; s++) {
            int total = semesterTotals.get(s);
            if (total == 0) continue;
            int mandatoryC = mandatoryCreditsBySemester.getOrDefault(s, 0);
            int electiveC = semesterElectiveCredits.getOrDefault(s, 0);
            String bar = "▓".repeat(Math.min(total / 2, 12));
            String status = (total >= curriculumExpected(s).min() && total <= curriculumExpected(s).max())
                ? "✅" : "📌";
            semesterTable.append(String.format(
                "  %s **%s**: %s **%d TC** (bắt buộc: %d, tự chọn: %d)\n",
                status, semNames[s], bar, total, mandatoryC, electiveC
            ));
        }

        String summary = String.format(
            "📚 **Lộ trình học tập đề xuất** cho định hướng **%s** với mục tiêu **%s**:\n\n" +
            "📊 **Tổng cộng: %d tín chỉ** (mục tiêu: 130)\n" +
            "  • Bắt buộc: %d TC (Giáo dục đại cương + Cơ sở ngành)\n" +
            "  • Tự chọn cơ sở ngành: %d TC\n" +
            "  • Tự chọn chuyên ngành: %d TC\n" +
            "  • Tự chọn tự do: %d TC\n" +
            "%s" +
            "  • Tốt nghiệp: %d TC\n" +
            "%s\n" +
            "💡 Lộ trình tuân thủ khung chương trình Kỹ thuật Phần mềm (130 TC) theo Quyết định áp dụng từ khóa 20.",
            careerPath, learningGoals,
            grandTotalTC,
            mandatoryTotalTC,
            coSoNganhElectiveTC,
            chuyenNganhElectiveTC,
            tuDoElectiveTC + crossTrainingTC,
            crossTrainingDetail,
            gradTC,
            semesterTable.toString()
        );

        // Generate graduation track recommendations
        List<RoadmapRecommendationResponse.GraduationTrack> graduationTracks =
            generateGraduationTracks(careerPath, learningGoals, levels);

        // Add recommended graduation course at HK8
        for (RoadmapRecommendationResponse.GraduationTrack track : graduationTracks) {
            if (track.recommended() && !track.courseCodes().isEmpty()) {
                String gradCode = track.courseCodes().get(0);
                // Find course info from allCourses
                Optional.ofNullable(courseMap.get(gradCode)).ifPresent(c -> {
                    String reasoning = String.format(
                        "[TỐT NGHIỆP] %s — %s",
                        track.name(), track.description().length() > 80
                            ? track.description().substring(0, 80) + "..."
                            : track.description()
                    );
                    recommendations.add(new RoadmapRecommendationResponse.CourseRecommendation(
                        c.getId(), c.getMaMH(), c.getTenMH(), reasoning, c.getDescription(),
                        true, 8, c.getSoTC()
                    ));
                });
                break;
            }
        }

        // Build elective pool candidates for user self-selection
        Set<String> selectedCodes = new HashSet<>();
        for (RoadmapRecommendationResponse.CourseRecommendation cr : recommendations) {
            if (!cr.isMandatory()) selectedCodes.add(cr.courseCode());
        }
        List<RoadmapRecommendationResponse.ElectivePoolCandidates> electivePools = buildElectivePools(
            prioritized, selectedCodes, crossTraining, courseMap
        );

        return new RoadmapRecommendationResponse(summary, recommendations, graduationTracks, electivePools);
    }

    private record CourseElectiveSem(Course course, int credits, int semester) {}

    /**
     * Cross-training pool: picks diverse courses from ANY field (non-KTPM) when
     * the KTPM pool targets are met but total credits are below 130.
     * Prioritizes soft skills, debugging, project management, foreign languages,
     * and practical developer competencies.
     */
    private List<Course> pickCrossTraining(
            List<CurriculumMatcher.ScoredCourse> prioritized,
            Map<String, Set<String>> prereqMap,
            Set<String> availableCodes,
            int maxCredits,
            Map<String, Course> courseMap) {
        // Cross-training priority codes: soft skills, debugging, diverse knowledge
        Set<String> priorityCodes = Set.of(
            // Soft skills & career
            "IE230", "CS519",
            // Debugging, testing, verification
            "SE113", "SE358", "SE109",
            // Project management & architecture
            "IS208", "IE302", "IE103",
            // Architecture & design
            "IS215", "IS201", "CS111", "CS112",
            // UI/UX
            "IE106", "IE228",
            // Competitive programming / debugging skills
            "IT011", "CS117",
            // Economy & management
            "EC001", "EC002", "EC003", "EC336",
            // Advanced English
            "ENG04", "ENG05",
            // Data science
            "IE212", "IS252", "IE224"
        );

        // First: try to add scorable priority courses from scored list
        List<Course> result = new ArrayList<>();
        int total = 0;

        for (CurriculumMatcher.ScoredCourse sc : prioritized) {
            if (total >= maxCredits) break;
            if (!priorityCodes.contains(sc.code())) continue;
            if (availableCodes.contains(sc.code())) continue;

            Set<String> prereqs = prereqMap.getOrDefault(sc.code(), Collections.emptySet());
            long metCount = prereqs.stream().filter(availableCodes::contains).count();
            if (prereqs.isEmpty() || metCount >= 1) {
                Course c = courseMap.get(sc.code());
                if (c != null) {
                    result.add(c);
                    availableCodes.add(c.getMaMH());
                    total += c.getSoTC();
                }
            }
        }

        // Second: try direct DB query for priority codes that may have 0 score
        if (total < maxCredits) {
            for (String code : priorityCodes) {
                if (total >= maxCredits) break;
                if (availableCodes.contains(code)) continue;

                Set<String> prereqs = prereqMap.getOrDefault(code, Collections.emptySet());
                long metCount = prereqs.stream().filter(availableCodes::contains).count();
                if (prereqs.isEmpty() || metCount >= 1) {
                    Course c = courseMap.get(code);
                    if (c != null) {
                        result.add(c);
                        availableCodes.add(c.getMaMH());
                        total += c.getSoTC();
                    }
                }
            }
        }

        return result;
    }

    /**
     * Selects courses from a pool up to a credit target.
     * Uses relaxed prereq check for KTPM pool courses to handle interlocking prereqs.
     */
    private List<Course> pickToCreditTarget(
            List<CurriculumMatcher.ScoredCourse> prioritized,
            Set<String> poolCodes,
            int maxCredits,
            Map<String, Set<String>> prereqMap,
            Set<String> availableCodes,
            Map<String, Course> courseMap) {
        List<Course> result = new ArrayList<>();
        int total = 0;
        int maxOvershoot = Math.min(4, maxCredits);

        // Collect all pool-matching scored courses first
        List<CurriculumMatcher.ScoredCourse> poolScored = new ArrayList<>();
        for (CurriculumMatcher.ScoredCourse sc : prioritized) {
            if (poolCodes != null && !poolCodes.contains(sc.code())) continue;
            if (CurriculumConstants.MANDATORY_CODES.contains(sc.code())) continue;
            if (availableCodes.contains(sc.code())) continue;
            poolScored.add(sc);
        }

        // Multiple passes: each pass picks courses whose prereqs are now satisfied
        boolean changed = true;
        while (changed && total < maxCredits + maxOvershoot) {
            changed = false;
            for (CurriculumMatcher.ScoredCourse sc : poolScored) {
                if (total >= maxCredits + maxOvershoot) break;
                if (availableCodes.contains(sc.code())) continue;

                Set<String> prereqs = prereqMap.getOrDefault(sc.code(), Collections.emptySet());
                long metCount = prereqs.stream().filter(availableCodes::contains).count();

                // Include if: no prereqs, any prereq met, or score >= 20 (likely relevant)
                if (prereqs.isEmpty() || metCount >= 1 || sc.score() >= 20) {
                    Course c = courseMap.get(sc.code());
                    if (c != null) {
                        result.add(c);
                        availableCodes.add(c.getMaMH());
                        total += c.getSoTC();
                        changed = true;
                    }
                }
            }
        }

        return result;
    }

    /**
     * Selects electives from non-KTPM courses (tự do tự chọn).
     */
    private List<Course> pickFreeToCreditTarget(
            List<CurriculumMatcher.ScoredCourse> prioritized,
            Set<String> excludePool,
            int maxCredits,
            Map<String, Set<String>> prereqMap,
            Set<String> availableCodes,
            Map<String, Course> courseMap) {
        List<Course> result = new ArrayList<>();
        int total = 0;

        for (CurriculumMatcher.ScoredCourse sc : prioritized) {
            if (total >= maxCredits) break;
            if (CurriculumConstants.MANDATORY_CODES.contains(sc.code())) continue;
            if (availableCodes.contains(sc.code())) continue;
            // Skip courses already in KTPM elective pools (they were handled separately)
            if (CurriculumConstants.CO_SO_NGANH_ELECTIVE.contains(sc.code())
                || CurriculumConstants.CHUYEN_NGANH_ELECTIVE.contains(sc.code())) continue;

            Set<String> prereqs = prereqMap.getOrDefault(sc.code(), Collections.emptySet());
            long metCount = prereqs.stream().filter(availableCodes::contains).count();

            boolean include = prereqs.isEmpty()
                || metCount >= 1
                || sc.score() >= 25;

            if (include) {
                Course c = courseMap.get(sc.code());
                if (c != null) {
                    int credits = c.getSoTC();
                    result.add(c);
                    availableCodes.add(c.getMaMH());
                    total += credits;
                }
            }
        }
        return result;
    }

    /**
     * Expected TC range per semester per the official KTPM curriculum.
     */
    private record SemesterRange(int min, int max) {}
    private SemesterRange curriculumExpected(int semester) {
        return switch (semester) {
            case 1 -> new SemesterRange(19, 21);
            case 2 -> new SemesterRange(18, 20);
            case 3 -> new SemesterRange(19, 21);
            case 4 -> new SemesterRange(14, 16);
            case 5 -> new SemesterRange(15, 17);
            case 6 -> new SemesterRange(13, 15);
            case 7 -> new SemesterRange(15, 17);
            case 8 -> new SemesterRange(10, 10);
            default -> new SemesterRange(0, 99);
        };
    }

    /**
     * Generates 3 graduation track options per the KTPM curriculum (sections 3.5.3-3.5.5).
     * One track is marked as recommended based on the user's career path.
     */
    private List<RoadmapRecommendationResponse.GraduationTrack> generateGraduationTracks(
            String careerPath, String learningGoals, Map<Long, Integer> levels) {

        String cp = careerPath.toLowerCase();
        String lg = learningGoals.toLowerCase();

        // Determine which track fits best
        String recommendedType;
        if (cp.contains("nghiên cứu") || cp.contains("research") || cp.contains("academic")
            || cp.contains("ai") || cp.contains("machine learning") || cp.contains("data scientist")
            || cp.contains("học thuật") || cp.contains("thuật toán") || cp.contains("algorithm")
            || lg.contains("nghiên cứu") || lg.contains("research") || lg.contains("master")
            || lg.contains("thạc sĩ") || lg.contains("tiến sĩ") || lg.contains("phd")) {
            recommendedType = "THESIS";
        } else if (cp.contains("game") || cp.contains("vr") || cp.contains("thực tế ảo")
            || cp.contains("sáng tạo") || cp.contains("creative") || cp.contains("indie")
            || cp.contains("chuyên đề") || cp.contains("elective")) {
            recommendedType = "PROJECT_PLUS";
        } else {
            // Default: most students benefit from company internship experience
            recommendedType = "COMPANY";
        }

        // Build 3 track options
        List<RoadmapRecommendationResponse.GraduationTrack> tracks = new ArrayList<>();

        // Track 1: Thesis
        tracks.add(new RoadmapRecommendationResponse.GraduationTrack(
            "THESIS",
            "Khóa luận tốt nghiệp",
            "Làm khóa luận tốt nghiệp với đề tài nghiên cứu chuyên sâu dưới sự hướng dẫn của giảng viên. "
                + "Phù hợp với sinh viên có định hướng nghiên cứu, học lên cao học (Thạc sĩ/Tiến sĩ). "
                + "Tối đa 02 sinh viên/đề tài.",
            10,
            "Đủ điều kiện theo quy chế của Trường",
            recommendedType.equals("THESIS")
                ? "🎯 **Đề xuất:** Định hướng **" + careerPath + "** rất phù hợp với hình thức làm khóa luận tốt nghiệp. "
                  + "Bạn có thể đi sâu nghiên cứu chuyên đề phù hợp, tạo tiền đề cho việc học lên cao."
                : "Phù hợp nếu bạn muốn nghiên cứu chuyên sâu và có dự định học lên cao học.",
            recommendedType.equals("THESIS"),
            List.of("SE505")
        ));

        // Track 2: Company project
        tracks.add(new RoadmapRecommendationResponse.GraduationTrack(
            "COMPANY",
            "Đồ án tốt nghiệp tại doanh nghiệp",
            "Thực hiện đồ án tốt nghiệp tại doanh nghiệp, giải quyết bài toán thực tế. "
                + "Có cơ hội làm việc trực tiếp với công nghệ doanh nghiệp sử dụng, "
                + "tích lũy kinh nghiệm thực tế và mở rộng mối quan hệ nghề nghiệp. "
                + "01 sinh viên/đề tài.",
            10,
            "Điểm trung bình tích lũy ≥ 70/100",
            recommendedType.equals("COMPANY")
                ? "🎯 **Đề xuất:** Với định hướng **" + careerPath + "**, hình thức đồ án tại doanh nghiệp là lựa chọn tối ưu. "
                  + "Bạn sẽ được trải nghiệm môi trường làm việc thực tế, "
                  + "áp dụng kiến thức đã học vào dự án cụ thể và xây dựng CV ấn tượng."
                : "Lựa chọn tốt để tích lũy kinh nghiệm thực tế và xây dựng mạng lưới quan hệ nghề nghiệp.",
            recommendedType.equals("COMPANY"),
            List.of("SE506")
        ));

        // Track 3: Project + Specialized topic
        tracks.add(new RoadmapRecommendationResponse.GraduationTrack(
            "PROJECT_PLUS",
            "Đồ án tốt nghiệp + Chuyên đề",
            "Kết hợp làm Đồ án tốt nghiệp (6TC) với tối thiểu 01 môn chuyên đề tốt nghiệp (4TC). "
                + "Các môn chuyên đề: Mẫu thiết kế HĐT (SE406), Nguyên lý thiết kế thế giới ảo (SE403), "
                + "Pervasive and Mobile Computing (SE407), Phát triển game với Blockchain (SE408), "
                + "Phát triển dự án Game (SE409), Seminar CNPM hiện đại (SE400). "
                + "Phù hợp nếu muốn kết hợp làm đồ án và học thêm kiến thức chuyên sâu.",
            10,
            "Điểm trung bình tích lũy ≥ 65/100",
            recommendedType.equals("PROJECT_PLUS")
                ? "🎯 **Đề xuất:** Với định hướng **" + careerPath + "** và mục tiêu **" + learningGoals + "**, "
                  + "hình thức kết hợp đồ án + chuyên đề giúp bạn vừa thực hành vừa mở rộng kiến thức chuyên ngành."
                : "Linh hoạt, cho phép kết hợp làm đồ án với học thêm môn chuyên đề yêu thích.",
            recommendedType.equals("PROJECT_PLUS"),
            List.of("SE507", "SE400", "SE406", "SE403", "SE407", "SE408", "SE409")
        ));

        return tracks;
    }

    private Map<String, Set<String>> buildPrerequisiteMap() {
        Map<String, Set<String>> prereqMap = new HashMap<>();
        for (CourseRelationshipResponse rel : relationshipCache) {
            if (rel.relationType() == CourseRelationType.PREREQUISITE) {
                // relatedCourseCode is the prerequisite, courseCode is the dependent course
                prereqMap.computeIfAbsent(rel.courseCode(), k -> new HashSet<>())
                         .add(rel.relatedCourseCode());
            }
        }
        return prereqMap;
    }

    /**
     * Computes the optimal semester for a course by tracing its prerequisite chain
     * back to mandatory curriculum courses. The result is the deepest prerequisite
     * semester + 1, or a default value if no curriculum prerequisite is found.
     */
    private int computeSemesterFromPrereqs(String courseCode, Map<String, Set<String>> prereqMap) {
        return computeSemesterFromPrereqs(courseCode, prereqMap, new HashSet<>(), 0);
    }

    private int computeSemesterFromPrereqs(String courseCode, Map<String, Set<String>> prereqMap,
                                            Set<String> visited, int depth) {
        if (depth > 5 || visited.contains(courseCode)) return 4;
        visited.add(courseCode);

        Set<String> directPrereqs = prereqMap.getOrDefault(courseCode, Collections.emptySet());
        if (directPrereqs.isEmpty()) return 4;

        int deepest = 0;
        boolean found = false;

        for (String prereq : directPrereqs) {
            Integer sem = CurriculumConstants.CURRICULUM_SEMESTERS.get(prereq);
            if (sem != null) {
                deepest = Math.max(deepest, sem);
                found = true;
            } else {
                // Trace further back
                int indirect = computeSemesterFromPrereqs(prereq, prereqMap, visited, depth + 1);
                if (indirect > 0) {
                    deepest = Math.max(deepest, indirect);
                    found = true;
                }
            }
        }

        return found ? deepest + 1 : 4;
    }

    /**
     * Build elective pool data for frontend self-selection UI.
     * Groups all eligible elective candidates by pool with their scores
     * and selection status.
     */
    private List<RoadmapRecommendationResponse.ElectivePoolCandidates> buildElectivePools(
            List<CurriculumMatcher.ScoredCourse> prioritized,
            Set<String> selectedCodes,
            List<Course> crossTraining,
            Map<String, Course> courseMap) {

        Set<String> crossTrainingCodes = crossTraining.stream()
            .map(Course::getMaMH).collect(Collectors.toSet());

        List<RoadmapRecommendationResponse.ElectivePoolCandidates> pools = new ArrayList<>();

        // Pool 1: Cơ sở ngành
        pools.add(buildSinglePool("co-so-nganh", "Tự chọn Cơ sở ngành",
            CurriculumConstants.CO_SO_NGANH_ELECTIVE,
            CurriculumConstants.CO_SO_NGANH_TARGET_TC, prioritized, selectedCodes, courseMap));

        // Pool 2: Chuyên ngành
        pools.add(buildSinglePool("chuyen-nganh", "Tự chọn Chuyên ngành",
            CurriculumConstants.CHUYEN_NGANH_ELECTIVE,
            CurriculumConstants.CHUYEN_NGANH_TARGET_TC, prioritized, selectedCodes, courseMap));

        // Pool 3: Tự do + Đào tạo chéo
        Set<String> allKTPM = new HashSet<>(CurriculumConstants.CO_SO_NGANH_ELECTIVE);
        allKTPM.addAll(CurriculumConstants.CHUYEN_NGANH_ELECTIVE);
        List<RoadmapRecommendationResponse.ElectiveCandidate> freeCrossCandidates = new ArrayList<>();
        int freeCrossTC = 0;
        for (CurriculumMatcher.ScoredCourse sc : prioritized) {
            if (CurriculumConstants.MANDATORY_CODES.contains(sc.code())) continue;
            if (allKTPM.contains(sc.code())) continue;
            if (sc.score() < 5 && !crossTrainingCodes.contains(sc.code())) continue;
            boolean isSelected = selectedCodes.contains(sc.code());
            if (isSelected) freeCrossTC += Optional.ofNullable(courseMap.get(sc.code()))
                .map(Course::getSoTC).orElse(3);
            freeCrossCandidates.add(new RoadmapRecommendationResponse.ElectiveCandidate(
                sc.id(), sc.code(), sc.name(), 0, sc.score(), isSelected,
                "", "", null
            ));
        }
        // Fix credits for free candidates
        List<RoadmapRecommendationResponse.ElectiveCandidate> fixedFreeCross = fixCandidateCredits(freeCrossCandidates, courseMap);
        pools.add(new RoadmapRecommendationResponse.ElectivePoolCandidates(
            "tu-do", "Tự do + Đào tạo chéo",
            8, freeCrossTC, fixedFreeCross
        ));

        return pools;
    }

    private RoadmapRecommendationResponse.ElectivePoolCandidates buildSinglePool(
            String poolId, String poolName, Set<String> poolCodes,
            int targetTC, List<CurriculumMatcher.ScoredCourse> prioritized,
            Set<String> selectedCodes,
            Map<String, Course> courseMap) {
        List<RoadmapRecommendationResponse.ElectiveCandidate> candidates = new ArrayList<>();
        int currentTC = 0;
        for (CurriculumMatcher.ScoredCourse sc : prioritized) {
            if (!poolCodes.contains(sc.code())) continue;
            if (CurriculumConstants.MANDATORY_CODES.contains(sc.code())) continue;
            boolean isSelected = selectedCodes.contains(sc.code());
            if (isSelected) {
                currentTC += Optional.ofNullable(courseMap.get(sc.code()))
                    .map(Course::getSoTC).orElse(3);
            }
            candidates.add(new RoadmapRecommendationResponse.ElectiveCandidate(
                sc.id(), sc.code(), sc.name(), 0, sc.score(), isSelected,
                "", "", null
            ));
        }
        List<RoadmapRecommendationResponse.ElectiveCandidate> fixed = fixCandidateCredits(candidates, courseMap);
        return new RoadmapRecommendationResponse.ElectivePoolCandidates(
            poolId, poolName, targetTC, currentTC, fixed
        );
    }

    /**
     * Fill in credits for elective candidates by querying the DB.
     * The ScoredCourse from CurriculumMatcher doesn't carry credit info.
     */
    private List<RoadmapRecommendationResponse.ElectiveCandidate> fixCandidateCredits(
            List<RoadmapRecommendationResponse.ElectiveCandidate> candidates,
            Map<String, Course> courseMap) {
        List<RoadmapRecommendationResponse.ElectiveCandidate> result = new ArrayList<>();
        for (RoadmapRecommendationResponse.ElectiveCandidate c : candidates) {
            int credits = Optional.ofNullable(courseMap.get(c.courseCode()))
                .map(Course::getSoTC).orElse(3);
            int semester = computeSemesterFromPrereqs(c.courseCode(), this.prerequisiteCodes);
            result.add(new RoadmapRecommendationResponse.ElectiveCandidate(
                c.courseId(), c.courseCode(), c.courseName(), credits,
                c.score(), c.isSelected(), c.description(), c.reasoning(), semester
            ));
        }
        return result;
    }

    private String generateElectiveReasoning(Course c, String careerPath) {
        String name = c.getTenMH().toLowerCase();

        if (name.contains("cấu trúc dữ liệu") || name.contains("giải thuật")) {
            return "🧠 **Nền tảng thuật toán cốt lõi** — kiến thức không thể thiếu cho mọi lập trình viên, " +
                   "đặc biệt khi phỏng vấn kỹ thuật và giải quyết bài toán hiệu quả.";
        }
        if (name.contains("mạng máy tính") || name.contains("network")) {
            return "🌐 **Kiến thức mạng nền tảng** — hiểu về giao thức và kiến trúc mạng " +
                   "giúp bạn xây dựng ứng dụng phân tán ổn định.";
        }
        if (name.contains("cơ sở dữ liệu") || name.contains("database")) {
            return "🗄️ **Quản lý dữ liệu** — kỹ năng thiết kế và truy vấn dữ liệu là " +
                   "trái tim của mọi ứng dụng hiện đại.";
        }
        if (name.contains("hệ điều hành")) {
            return "⚙️ **Hiểu sâu về hệ thống** — kiến thức về quản lý tiến trình, bộ nhớ " +
                   "giúp bạn viết code hiệu quả và tối ưu hơn.";
        }
        if (name.contains("công nghệ phần mềm") || name.contains("software")) {
            return "🏗️ **Quy trình phát triển phần mềm** — áp dụng các methodology " +
                   "và best practice vào dự án thực tế.";
        }
        if (name.contains("trí tuệ nhân tạo") || name.contains("machine learning")) {
            return "🤖 **Trí tuệ nhân tạo** — lĩnh vực hot nhất hiện nay, phù hợp với " +
                   "định hướng **" + careerPath + "**.";
        }
        if (name.contains("android") || name.contains("kotlin")) {
            return "📱 **Phát triển Android** — kỹ năng lập trình mobile với Kotlin, " +
                   "phù hợp với định hướng **" + careerPath + "**.";
        }
        if (name.contains("web") || name.contains("react") || name.contains("frontend")) {
            return "🕸️ **Phát triển Web** — xây dựng giao diện người dùng hiện đại, " +
                   "phù hợp với định hướng **" + careerPath + "**.";
        }

        return String.format(
            "📖 Môn học **%s** phù hợp với định hướng **%s** và các môn tiên quyết " +
            "đã được đáp ứng trong chương trình bắt buộc.",
            c.getTenMH(), careerPath
        );
    }
}
