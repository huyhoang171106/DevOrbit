package vn.edu.uit.devorbit.mobile.ui.screen.courses

data class CourseHubNavigationState(
    val selectedCourseId: Long? = null,
    val selectedRepoId: Long? = null
) {
    fun openCourse(courseId: Long): CourseHubNavigationState {
        return copy(selectedCourseId = courseId, selectedRepoId = null)
    }

    fun openRepo(repoId: Long): CourseHubNavigationState {
        return copy(selectedRepoId = repoId)
    }

    fun back(): CourseHubNavigationState {
        return if (selectedRepoId != null) {
            copy(selectedRepoId = null)
        } else {
            copy(selectedCourseId = null, selectedRepoId = null)
        }
    }
}
