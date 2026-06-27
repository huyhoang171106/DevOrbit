package vn.edu.uit.devorbit.admin.data.remote.dto

/** Backend only returns id and name. No channelActive or channelId. */
data class AdminTechStackResponse(
    val id: Long,
    val name: String
)
