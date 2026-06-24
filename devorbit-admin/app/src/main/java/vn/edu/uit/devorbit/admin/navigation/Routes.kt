package vn.edu.uit.devorbit.admin.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(val route: String) {

    object Today : Screen("today")

    object Subjects : Screen("subjects")

    object SubjectDetail : Screen("subject/{subjectId}") {
        const val ARG_SUBJECT_ID = "subjectId"

        /** Create a concrete route with the subject ID substituted in. */
        fun createRoute(subjectId: String): String = "subject/$subjectId"

        /** NavArgument definition for use with composable(route, arguments = ...). */
        val arguments = listOf(
            navArgument(ARG_SUBJECT_ID) { type = NavType.StringType },
        )

        /** Safely extract the subject ID from a NavBackStackEntry. */
        fun fromEntry(entry: NavBackStackEntry): String =
            entry.arguments?.getString(ARG_SUBJECT_ID).orEmpty()
    }

    object Tutor : Screen("tutor")

    object StudyPlan : Screen("studyplan")

    object Profile : Screen("profile")

    companion object {
        /** All top-level destinations shown in the CommandDock / bottom nav. */
        val topLevelDestinations: List<Screen> = listOf(
            Today,
            Subjects,
            Tutor,
            StudyPlan,
            Profile,
        )
    }
}
