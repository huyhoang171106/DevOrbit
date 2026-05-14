package vn.edu.uit.devorbit.mobile.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthResponseDto(
    @SerializedName("token") val token: String,
    @SerializedName("studentCode") val studentCode: String? = null,
    @SerializedName("fullName") val fullName: String? = null,
    @SerializedName("email") val email: String? = null
)

data class StudentProfileDto(
    val id: Long,
    val studentCode: String,
    val fullName: String,
    val email: String,
    val active: Boolean
)

data class AiQueryRequest(val query: String)
data class AiQueryResponse(val answer: String, val relatedCourses: List<Long>? = null)

data class DiscoveryRepoDto(
    val id: Long,
    val displayName: String,
    val description: String,
    val githubUrl: String,
    val primaryLanguage: String,
    val stars: Int? = null,
    val courseName: String? = null
)

data class TopStackDto(
    @SerializedName("name") val name: String,
    @SerializedName("count") val count: Int
)

data class AiSummaryDto(val summary: String, val keyTopics: List<String>? = null)
data class AiAdviceDto(val advice: String, val difficulty: String? = null, val prerequisites: List<String>? = null)
