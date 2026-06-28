package vn.edu.uit.devorbit.mobile.data.repository

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import vn.edu.uit.devorbit.mobile.data.remote.dto.SubjectQaRequest
import vn.edu.uit.devorbit.mobile.data.remote.dto.SubjectQaResponse
import vn.edu.uit.devorbit.mobile.data.remote.dto.SubjectQaStreamEventResponse
import vn.edu.uit.devorbit.mobile.domain.repository.SubjectQaAnswer
import vn.edu.uit.devorbit.mobile.domain.repository.SubjectQaEvent
import vn.edu.uit.devorbit.mobile.domain.repository.SubjectQaRepository
import vn.edu.uit.devorbit.mobile.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectQaRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : SubjectQaRepository {

    private val gson = Gson()

    override suspend fun ask(message: String, sessionId: String?): SubjectQaAnswer {
        val response = apiService.querySubjectQa(SubjectQaRequest(message = message, sessionId = sessionId))
        return response.toAnswer()
    }

    override fun stream(message: String, sessionId: String?): Flow<SubjectQaEvent> = flow {
        val httpResponse = apiService.streamSubjectQa(
            SubjectQaRequest(message = message, sessionId = sessionId)
        )
        if (!httpResponse.isSuccessful) {
            val detail = httpResponse.errorBody()?.string().orEmpty()
            throw IllegalStateException(detail.ifBlank { "AI request failed (${httpResponse.code()})" })
        }

        val body = httpResponse.body() ?: throw IllegalStateException("AI stream has no response body")
        body.source().use { source ->
            val dataLines = mutableListOf<String>()

            suspend fun flushEvent() {
                if (dataLines.isEmpty()) return
                val event = gson.fromJson(dataLines.joinToString("\n"), SubjectQaStreamEventResponse::class.java)
                when (event.type) {
                    "status" -> emit(
                        SubjectQaEvent.Status(
                            stage = event.stage.orEmpty(),
                            message = event.message.orEmpty()
                        )
                    )
                    "delta" -> event.content?.takeIf { it.isNotEmpty() }?.let {
                        emit(SubjectQaEvent.Delta(it))
                    }
                    "complete" -> event.response?.let {
                        emit(SubjectQaEvent.Complete(it.toAnswer()))
                    }
                    "error" -> throw IllegalStateException(event.message ?: "AI stream failed")
                }
                dataLines.clear()
            }

            while (!source.exhausted()) {
                val line = source.readUtf8Line() ?: break
                when {
                    line.isBlank() -> flushEvent()
                    line.startsWith("data:") -> dataLines += line.removePrefix("data:").trimStart()
                }
            }
            flushEvent()
        }
    }.flowOn(Dispatchers.IO)

    private fun SubjectQaResponse.toAnswer() = SubjectQaAnswer(
        answer = answer,
        sessionId = sessionId,
        sources = sources,
        relevantNodeIds = relevantNodeIds,
        type = type
    )
}
