package vn.edu.uit.devorbit.mobile.data.remote.dto

data class CourseSummary(
    val id: Long,
    val code: String,
    val name: String,
    val semester: Int? = null,
    val loaiMonHoc: String? = null
)
