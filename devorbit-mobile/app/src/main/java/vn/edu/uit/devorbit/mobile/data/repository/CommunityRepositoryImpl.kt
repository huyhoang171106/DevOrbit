package vn.edu.uit.devorbit.mobile.data.repository

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import vn.edu.uit.devorbit.mobile.BuildConfig
import vn.edu.uit.devorbit.mobile.data.remote.dto.*
import vn.edu.uit.devorbit.mobile.domain.repository.CommunityRepository
import vn.edu.uit.devorbit.mobile.network.ApiService
import vn.edu.uit.devorbit.mobile.network.stomp.StompClient
import vn.edu.uit.devorbit.mobile.network.stomp.StompEventListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
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

    override fun sendMessage(channelId: Long, content: String, imageUrl: String?) {
        val payload = mutableMapOf<String, String>()
        if (content.isNotBlank()) payload["content"] = content
        if (!imageUrl.isNullOrBlank()) payload["imageUrl"] = imageUrl
        stompClient.send("/app/chat.send/$channelId", Gson().toJson(payload))
    }

    override suspend fun uploadImage(channelId: Long, uri: Uri, context: Context): String? {
        return try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
            tempFile.outputStream().use { output -> inputStream.copyTo(output) }

            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
            val requestBody = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", tempFile.name, requestBody)

            val result = apiService.uploadCommunityImage(channelId, part)
            tempFile.delete()
            result["url"]
        } catch (e: Exception) {
            null
        }
    }

    override fun disconnectWebSocket() {
        stompClient.disconnect()
    }

    override fun isConnected(): Boolean = stompClient.isConnected()
}
