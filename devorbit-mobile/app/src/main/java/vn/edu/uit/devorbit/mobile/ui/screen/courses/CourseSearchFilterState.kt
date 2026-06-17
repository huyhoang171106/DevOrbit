package vn.edu.uit.devorbit.mobile.ui.screen.courses

data class CourseSubjectFilter(
    val label: String,
    val value: String?
)

data class CourseSearchFilterState(
    val query: String = "",
    val subjectType: String? = null
) {
    val normalizedQuery: String?
        get() = query.trim().takeIf { it.isNotBlank() }

    fun updateQuery(value: String): CourseSearchFilterState = copy(query = value)

    fun selectSubjectType(value: String?): CourseSearchFilterState {
        return copy(subjectType = if (value == subjectType) null else value)
    }

    companion object {
        val subjectFilters = listOf(
            CourseSubjectFilter("Tất cả", null),
            CourseSubjectFilter("Đại cương", "DAI_CUONG"),
            CourseSubjectFilter("Cơ sở", "CO_SO"),
            CourseSubjectFilter("Chuyên ngành", "CHUYEN_NGANH")
        )
    }
}
