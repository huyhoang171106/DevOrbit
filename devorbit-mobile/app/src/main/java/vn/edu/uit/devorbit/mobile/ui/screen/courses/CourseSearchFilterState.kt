package vn.edu.uit.devorbit.mobile.ui.screen.courses

data class CourseSubjectFilter(
    val label: String,
    val value: String?
)

data class CourseSemesterFilter(
    val label: String,
    val value: Int?
)

data class CourseSearchFilterState(
    val query: String = "",
    val subjectType: String? = null,
    val semester: Int? = null
) {
    val normalizedQuery: String?
        get() = query.trim().takeIf { it.isNotBlank() }

    fun updateQuery(value: String): CourseSearchFilterState = copy(query = value)

    fun selectSubjectType(value: String?): CourseSearchFilterState {
        return copy(subjectType = if (value == subjectType) null else value)
    }

    fun selectSemester(value: Int?): CourseSearchFilterState {
        return copy(semester = if (value == semester) null else value)
    }

    companion object {
        val subjectFilters = listOf(
            CourseSubjectFilter("Tất cả", null),
            CourseSubjectFilter("Đại cương", "DAI_CUONG"),
            CourseSubjectFilter("Cơ sở", "CO_SO"),
            CourseSubjectFilter("Chuyên ngành", "CHUYEN_NGANH"),
            CourseSubjectFilter("Tự chọn", "TU_CHON")
        )

        val semesterFilters = listOf(
            CourseSemesterFilter("Tất cả HK", null),
            CourseSemesterFilter("HK 1", 1),
            CourseSemesterFilter("HK 2", 2),
            CourseSemesterFilter("HK 3", 3),
            CourseSemesterFilter("HK 4", 4),
            CourseSemesterFilter("HK 5", 5),
            CourseSemesterFilter("HK 6", 6),
            CourseSemesterFilter("HK 7", 7),
            CourseSemesterFilter("HK 8", 8)
        )
    }
}
