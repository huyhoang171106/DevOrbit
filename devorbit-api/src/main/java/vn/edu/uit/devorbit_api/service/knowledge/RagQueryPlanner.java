package vn.edu.uit.devorbit_api.service.knowledge;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.entity.Course;
import vn.edu.uit.devorbit_api.repository.CourseRepository;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagQueryPlanner {

    private final CourseRepository courseRepository;

    private static final Pattern COURSE_CODE_PATTERN = Pattern.compile("\\b([A-Z]{2,4}\\d{3,4})\\b");

    // Intent expansion maps
    private static final Map<Pattern, String> INTENT_EXPANSIONS = new LinkedHashMap<>();
    static {
        INTENT_EXPANSIONS.put(
            Pattern.compile("hoc tot|lam sao|kinh nghiem", Pattern.CASE_INSENSITIVE),
            "phuong phap hoc tap cach hoc hieu qua kinh nghiem hoc mon");
        INTENT_EXPANSIONS.put(
            Pattern.compile("de cuong|giao trinh|tai lieu", Pattern.CASE_INSENSITIVE),
            "de cuong mon hoc giao trinh tai lieu tham khao syllabus");
        INTENT_EXPANSIONS.put(
            Pattern.compile("de thi|kiem tra|thi", Pattern.CASE_INSENSITIVE),
            "hinh thuc danh gia bai tap diem qua trinh thi cuoi ky");
        INTENT_EXPANSIONS.put(
            Pattern.compile("project|repo|github|do an", Pattern.CASE_INSENSITIVE),
            "do an thuc hanh repository github source code");
        INTENT_EXPANSIONS.put(
            Pattern.compile("lo trinh|nen hoc|truoc sau", Pattern.CASE_INSENSITIVE),
            "tien quyet lo trinh hoc phan lien quan");
    }

    public RagQueryPlan plan(String rawQuery, String scopedCourseCode) {
        if (rawQuery == null || rawQuery.isBlank()) {
            return new RagQueryPlan(rawQuery, "", "", List.of(), Set.of());
        }

        // Detect course codes
        Set<String> detectedCodes = new LinkedHashSet<>();
        Matcher matcher = COURSE_CODE_PATTERN.matcher(rawQuery.toUpperCase());
        while (matcher.find()) {
            detectedCodes.add(matcher.group(1));
        }
        if (scopedCourseCode != null && !scopedCourseCode.isBlank()) {
            detectedCodes.add(scopedCourseCode);
        }

        // Build course expansions from DB
        StringBuilder courseExpansion = new StringBuilder();
        for (String code : detectedCodes) {
            try {
                Optional<Course> courseOpt = courseRepository.findByMaMH(code);
                if (courseOpt.isPresent()) {
                    Course course = courseOpt.get();
                    courseExpansion.append(" ").append(course.getTenMH());
                    if (course.getLoaiMonHoc() != null) {
                        courseExpansion.append(" ").append(course.getLoaiMonHoc());
                    }
                    if (course.getDescription() != null && !course.getDescription().isBlank()) {
                        courseExpansion.append(" ").append(course.getDescription());
                    }
                }
            } catch (Exception e) {
                log.warn("RagQueryPlanner: course lookup failed for {}, continuing: {}", code, e.getMessage());
            }
        }

        // Detect intent
        String normalizedQuery = normalizeForIntent(rawQuery);
        StringBuilder intentExpansion = new StringBuilder();
        for (Map.Entry<Pattern, String> entry : INTENT_EXPANSIONS.entrySet()) {
            if (entry.getKey().matcher(normalizedQuery).find()) {
                intentExpansion.append(" ").append(entry.getValue());
            }
        }

        // Build primaryQuery: raw + course expansions + intent expansions
        String primaryQuery = rawQuery
            + courseExpansion.toString()
            + intentExpansion.toString();
        if (primaryQuery.length() > 1600) {
            primaryQuery = primaryQuery.substring(0, 1600);
        }

        // Build textQuery: normalized + detected codes + normalized course tokens + intent expansions
        String normalizedCourseExpansion = normalizeForIntent(courseExpansion.toString());
        String normalizedIntentExpansion = intentExpansion.toString().replaceAll("[^a-z0-9\\s]", " ");
        String textQuery = normalizedQuery
            + " " + String.join(" ", detectedCodes)
            + " " + normalizedCourseExpansion
            + " " + normalizedIntentExpansion;
        textQuery = textQuery.replaceAll("[^a-z0-9\\s]", " ").replaceAll("\\s+", " ").trim();
        if (textQuery.length() > 800) {
            textQuery = textQuery.substring(0, 800);
        }

        // Build expanded queries list: at most 3 variants, deduplicated
        List<String> expandedQueries = new ArrayList<>();
        if (!rawQuery.isBlank()) {
            expandedQueries.add(rawQuery);
        }
        if (!primaryQuery.isBlank() && !primaryQuery.equals(rawQuery)) {
            expandedQueries.add(primaryQuery);
        }
        // Add normalized text query as a third variant if distinct from already-added queries
        String normalizedText = normalizedQuery.replaceAll("[^a-z0-9\\s]", " ").replaceAll("\\s+", " ").trim();
        if (!normalizedText.isBlank()) {
            boolean alreadyExists = expandedQueries.stream()
                .anyMatch(q -> {
                    String qNormalized = normalizeForIntent(q).replaceAll("[^a-z0-9\\s]", " ").replaceAll("\\s+", " ").trim();
                    return qNormalized.equals(normalizedText);
                });
            if (!alreadyExists) {
                expandedQueries.add(normalizedText);
            }
        }

        return new RagQueryPlan(rawQuery, primaryQuery, textQuery, expandedQueries, detectedCodes);
    }

    public static String normalizeForIntent(String message) {
        if (message == null) return "";
        String nfd = Normalizer.normalize(message.toLowerCase(), Normalizer.Form.NFD);
        String noMarks = nfd.replaceAll("\\p{M}", "");
        String normalized = noMarks
                .replace("đ", "d")
                .replace("Đ", "d")
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
        return normalized;
    }
}
