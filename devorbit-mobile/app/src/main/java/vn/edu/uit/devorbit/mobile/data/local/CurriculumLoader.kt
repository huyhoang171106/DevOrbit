package vn.edu.uit.devorbit.mobile.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class SemesterData(
    val semester: Int,
    val courses: List<String>
)

data class CourseData(
    val code: String,
    val name: String,
    val credits: Int,
    val prerequisites: List<String> = emptyList()
)

data class ElectiveGroupData(
    val id: String,
    val name: String,
    val minCredits: Int,
    @SerializedName("courseCodes") val courseCodes: List<String> = emptyList()
)

data class RulesData(
    val maxCreditsPerSemester: Int,
    val minCreditsPerSemester: Int,
    val totalSemesters: Int
)

data class Curriculum(
    val major: String,
    val version: String,
    val semesters: List<SemesterData>,
    val courseCatalog: List<CourseData>,
    val electiveGroups: List<ElectiveGroupData> = emptyList(),
    val rules: RulesData
)

@Singleton
class CurriculumLoader @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()
    private val cached = mutableMapOf<String, Curriculum>()

    fun load(major: String): Curriculum? {
        val key = major.uppercase()
        if (key in cached) return cached[key]
        val filename = "${key.lowercase()}_2024.json"
        return try {
            val json = context.assets.open("curriculum/$filename").bufferedReader().use { it.readText() }
            val curriculum = gson.fromJson(json, Curriculum::class.java)
            cached[key] = curriculum
            curriculum
        } catch (e: Exception) {
            null
        }
    }

    fun getAvailableMajors(): List<String> {
        return try {
            context.assets.list("curriculum")?.toList().orEmpty()
                .filter { it.endsWith("_2024.json") }
                .map { it.substringBefore("_").uppercase() }
                .sorted()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
