package vn.edu.uit.devorbit.mobile.domain.repository

data class SubjectQaAnswer(
    val answer: String,
    val sessionId: String?,
    val sources: List<String>,
    val relevantNodeIds: List<Long>,
    val type: String?
)

interface SubjectQaRepository {
    suspend fun ask(message: String, sessionId: String?): SubjectQaAnswer
}
