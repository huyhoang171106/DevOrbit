package vn.edu.uit.devorbit_api.constant;

import java.util.Map;
import java.util.Set;

/**
 * KTPM curriculum constants (Khoá 20-2025).
 * Source: daa.uit.edu.vn — Chương trình đào tạo Kỹ thuật Phần mềm.
 */
public final class CurriculumConstants {

    private CurriculumConstants() {}

    /**
     * Mandatory course codes from the KTPM curriculum.
     * Excludes TCN1-4, CDTN (elective placeholders — handled by LLM)
     * and ME001, PE0231, PE0232 (non-academic).
     */
    public static final Set<String> MANDATORY_CODES = Set.of(
        // HK1
        "IT001", "IT012", "MA003", "MA006", "ENG01", "SE005",
        // HK2
        "IT002", "IT003", "MA004", "MA005", "ENG02",
        // HK3
        "IT008", "IT004", "IT005", "IT007", "ENG03",
        // HK4
        "SE104", "SS004", "SS007", "SS008",
        // HK5
        "SE100", "SS009", "SS010",
        // HK6
        "SE503", "SS003", "SS006",
        // HK7
        "SE502",
        // HK8
        "SE505", "SE506", "SE507"
    );

    /**
     * Semester assignment from the curriculum teaching plan (section 4.2).
     */
    public static final Map<String, Integer> CURRICULUM_SEMESTERS = Map.ofEntries(
        Map.entry("IT001", 1), Map.entry("IT012", 1), Map.entry("MA003", 1),
        Map.entry("MA006", 1), Map.entry("ENG01", 1), Map.entry("SE005", 1),
        Map.entry("IT002", 2), Map.entry("IT003", 2), Map.entry("MA004", 2),
        Map.entry("MA005", 2), Map.entry("ENG02", 2),
        Map.entry("IT008", 3), Map.entry("IT004", 3), Map.entry("IT005", 3),
        Map.entry("IT007", 3), Map.entry("ENG03", 3),
        Map.entry("SE104", 4), Map.entry("SS004", 4), Map.entry("SS007", 4),
        Map.entry("SS008", 4),
        Map.entry("SE100", 5), Map.entry("SS009", 5), Map.entry("SS010", 5),
        Map.entry("SE503", 6), Map.entry("SS003", 6), Map.entry("SS006", 6),
        Map.entry("SE502", 7),
        Map.entry("SE505", 8), Map.entry("SE506", 8), Map.entry("SE507", 8)
    );
}
