package vn.edu.uit.devorbit.mobile.data.remote.dto

data class SemesterCourseResponse(
    val id: Long,
    val courseId: Long,
    val courseCode: String,
    val courseName: String,
    val createdAt: String?
)
