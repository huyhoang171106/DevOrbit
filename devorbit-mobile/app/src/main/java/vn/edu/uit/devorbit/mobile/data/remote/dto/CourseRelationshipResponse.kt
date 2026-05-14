package vn.edu.uit.devorbit.mobile.data.remote.dto

data class CourseRelationshipResponse(
    val id: Long,
    val courseId: Long,
    val relatedCourseId: Long,
    val relationType: String
)
