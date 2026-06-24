package vn.edu.uit.devorbit.mobile.data.repository

import vn.edu.uit.devorbit.mobile.BuildConfig
import vn.edu.uit.devorbit.mobile.data.remote.dto.*
import vn.edu.uit.devorbit.mobile.domain.repository.CommunityRepository
import vn.edu.uit.devorbit.mobile.network.ApiService
import vn.edu.uit.devorbit.mobile.network.stomp.StompClient
import vn.edu.uit.devorbit.mobile.network.stomp.StompEventListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommunityRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val stompClient: StompClient
) : CommunityRepository {

    override suspend fun getChannels(): List<ChatChannelResponse> {
        return apiService.getCommunityChannels()
    }

    override suspend fun getChannelMessages(channelId: Long, page: Int, size: Int): PaginatedMessagesResponse {
        return apiService.getChannelMessages(channelId, page, size)
    }

    override fun connectWebSocket(token: String, listener: StompEventListener) {
        val wsBase = BuildConfig.API_BASE_URL.trimEnd('/')
        val wsUrl = "$wsBase/ws/community/websocket"
        stompClient.setListener(listener)
        stompClient.connect(wsUrl, token)
    }

    override fun subscribeToChannel(channelId: Long) {
        stompClient.subscribe("/topic/channel/$channelId", "msg-$channelId")
        stompClient.subscribe("/topic/channel/$channelId/presence", "pres-$channelId")
    }

    override fun unsubscribeFromChannel(channelId: Long) {
        stompClient.unsubscribe("msg-$channelId")
        stompClient.unsubscribe("pres-$channelId")
    }

    override fun sendMessage(channelId: Long, content: String) {
        stompClient.send("/app/chat.send/$channelId", "{\"content\":\"$content\"}")
    }

    override fun disconnectWebSocket() {
        stompClient.disconnect()
    }

    override fun isConnected(): Boolean = stompClient.isConnected()
}
