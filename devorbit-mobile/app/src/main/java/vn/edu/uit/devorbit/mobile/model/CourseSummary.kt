package vn.edu.uit.devorbit.mobile.model

data class CourseSummary(
    val id: Long,
    val code: String,
    val name: String,
    val semester: Int? = null,
    val loaiMonHoc: String? = null
)
