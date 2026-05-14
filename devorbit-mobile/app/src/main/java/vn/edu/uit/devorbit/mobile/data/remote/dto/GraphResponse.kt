package vn.edu.uit.devorbit.mobile.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GraphResponse(
    val nodes: List<GraphNodeDto>,
    val links: List<GraphLinkDto>
)

data class GraphNodeDto(
    val id: Long,
    val name: String,
    val code: String,
    val description: String?,
    @SerializedName("val") val nodeVal: Double?,
    val level: Int?,
    val impactScore: Double?,
    val semester: Int?,
    val electiveGroup: String?
)

data class GraphLinkDto(
    val source: Long,
    val target: Long,
    val type: String
)
