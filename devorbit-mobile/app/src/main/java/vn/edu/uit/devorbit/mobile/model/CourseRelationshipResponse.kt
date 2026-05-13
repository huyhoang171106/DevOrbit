package vn.edu.uit.devorbit.mobile.model

data class CourseRelationshipResponse(
    val id: Long,
    val courseId: Long,
    val relatedCourseId: Long,
    val relationType: String
)
