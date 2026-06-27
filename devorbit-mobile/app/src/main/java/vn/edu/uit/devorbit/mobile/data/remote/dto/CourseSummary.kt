package vn.edu.uit.devorbit.mobile.data.remote.dto

data class CourseSummary(
    val id: Long,
    val code: String,
    val name: String,
    val description: String? = null,
    val credits: Int = 0,
    val semester: Int? = null,
    val loaiMonHoc: String? = null,
    val repoCount: Int = 0
)
