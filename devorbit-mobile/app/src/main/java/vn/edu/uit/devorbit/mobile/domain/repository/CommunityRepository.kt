package vn.edu.uit.devorbit.mobile.domain.repository

import vn.edu.uit.devorbit.mobile.data.remote.dto.*
import vn.edu.uit.devorbit.mobile.network.stomp.StompEventListener

interface CommunityRepository {
    suspend fun getChannels(): List<ChatChannelResponse>
    suspend fun getChannelMessages(channelId: Long, page: Int = 0, size: Int = 50): PaginatedMessagesResponse
    fun connectWebSocket(token: String, listener: StompEventListener)
    fun subscribeToChannel(channelId: Long)
    fun unsubscribeFromChannel(channelId: Long)
    fun sendMessage(channelId: Long, content: String)
    fun disconnectWebSocket()
    fun isConnected(): Boolean
}
