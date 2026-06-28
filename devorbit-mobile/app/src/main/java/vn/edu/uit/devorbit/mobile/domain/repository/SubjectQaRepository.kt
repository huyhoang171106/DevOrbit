package vn.edu.uit.devorbit.mobile.domain.repository

import kotlinx.coroutines.flow.Flow

data class SubjectQaAnswer(
    val answer: String,
    val sessionId: String?,
    val sources: List<String>,
    val relevantNodeIds: List<Long>,
    val type: String?
)

sealed interface SubjectQaEvent {
    data class Status(val stage: String, val message: String) : SubjectQaEvent
    data class Delta(val content: String) : SubjectQaEvent
    data class Complete(val answer: SubjectQaAnswer) : SubjectQaEvent
}

interface SubjectQaRepository {
    suspend fun ask(message: String, sessionId: String?): SubjectQaAnswer
    fun stream(message: String, sessionId: String?): Flow<SubjectQaEvent>
}
