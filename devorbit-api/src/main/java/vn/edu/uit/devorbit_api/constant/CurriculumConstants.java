package vn.edu.uit.devorbit_api.constant;

import java.util.*;

/**
 * KTPM curriculum constants (Khoá 20-2025).
 * Source: daa.uit.edu.vn — Chương trình đào tạo Kỹ thuật Phần mềm.
 */
public final class CurriculumConstants {

    private CurriculumConstants() {}

    /**
     * Mandatory course codes from the KTPM curriculum (section 3.3 + 3.4.1 + 3.5).
     * Excludes ME001, PE231, PE232 (non-academic).
     */
    public static final Set<String> MANDATORY_CODES = Set.of(
        // HK1 — Giáo dục đại cương
        "IT001", "IT007", "MA003", "MA006", "ENG01", "SE005",
        // HK2
        "IT002", "IT003", "MA004", "MA005", "ENG02",
        // HK3
        "IT008", "IT004", "IT005", "IT012", "ENG03",
        // HK4
        "SE104", "SS004", "SS007", "SS008",
        // HK5
        "SE100", "SS009", "SS010",
        // HK6
        "SE503", "SS003", "SS006",
        // HK7
        "SE502",
        // HK8 — Tốt nghiệp
        "SE505", "SE506", "SE507"
    );

    /**
     * Semester assignment from the curriculum teaching plan (section 4.2).
     */
    public static final Map<String, Integer> CURRICULUM_SEMESTERS = Map.ofEntries(
        // HK1
        Map.entry("IT001", 1), Map.entry("IT007", 1), Map.entry("MA003", 1),
        Map.entry("MA006", 1), Map.entry("ENG01", 1), Map.entry("SE005", 1),
        // HK2
        Map.entry("IT002", 2), Map.entry("IT003", 2), Map.entry("MA004", 2),
        Map.entry("MA005", 2), Map.entry("ENG02", 2),
        // HK3
        Map.entry("IT008", 3), Map.entry("IT004", 3), Map.entry("IT005", 3),
        Map.entry("IT012", 3), Map.entry("ENG03", 3),
        // HK4
        Map.entry("SE104", 4), Map.entry("SS004", 4), Map.entry("SS007", 4),
        Map.entry("SS008", 4),
        // HK5
        Map.entry("SE100", 5), Map.entry("SS009", 5), Map.entry("SS010", 5),
        // HK6
        Map.entry("SE503", 6), Map.entry("SS003", 6), Map.entry("SS006", 6),
        // HK7
        Map.entry("SE502", 7),
        // HK8
        Map.entry("SE505", 8), Map.entry("SE506", 8), Map.entry("SE507", 8)
    );

    /**
     * Tự chọn cơ sở ngành (section 3.4.1) — tích lũy tối thiểu 12TC.
     */
    public static final Set<String> CO_SO_NGANH_ELECTIVE = Set.of(
        "SE359", "SE102", "SE115", "SE116", "SE114",
        "SE360", "SE361", "SE301", "SE215", "SE113",
        "SE358", "SE117"
    );

    /**
     * Tự chọn chuyên ngành (section 3.4.2) — tích lũy tối thiểu 16TC.
     * Bao gồm danh sách chính, định hướng PT PM, định hướng Game, và chuyên đề tốt nghiệp.
     */
    public static final Set<String> CHUYEN_NGANH_ELECTIVE = Set.of(
        "SE330", "SE332", "SE334", "SE347", "SE350",
        "SE343", "SE355", "SE310", "SE346", "SE404",
        "SE331", "SE313", "SE352",
        "SE109", "SE356", "SE357", "SE325", "SE101",
        "SE106", "SE214", "SE362", "SE363", "SE364", "SE365",
        "SE221", "SE220", "SE320", "SE327", "SE328",
        "SE344", "SE314", "SE315", "SE316", "SE317",
        "SE400", "SE406", "SE403", "SE407", "SE408", "SE409"
    );

    /**
     * Union of all KTPM elective codes (for backward compatibility).
     */
    public static final Set<String> ELECTIVE_CODES = Collections.unmodifiableSet(
        new HashSet<>() {{
            addAll(CO_SO_NGANH_ELECTIVE);
            addAll(CHUYEN_NGANH_ELECTIVE);
        }}
    );

    // Credit targets per pool
    public static final int CO_SO_NGANH_TARGET_TC = 12;
    public static final int CHUYEN_NGANH_TARGET_TC = 16;
    public static final int TU_DO_TARGET_TC = 6;
}
