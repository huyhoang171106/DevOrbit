package vn.edu.uit.devorbit.mobile.data.repository

import vn.edu.uit.devorbit.mobile.data.remote.dto.SubjectQaRequest
import vn.edu.uit.devorbit.mobile.domain.repository.SubjectQaAnswer
import vn.edu.uit.devorbit.mobile.domain.repository.SubjectQaRepository
import vn.edu.uit.devorbit.mobile.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectQaRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : SubjectQaRepository {

    override suspend fun ask(message: String, sessionId: String?): SubjectQaAnswer {
        val response = apiService.querySubjectQa(SubjectQaRequest(message = message, sessionId = sessionId))
        return SubjectQaAnswer(
            answer = response.answer,
            sessionId = response.sessionId,
            sources = response.sources,
            relevantNodeIds = response.relevantNodeIds,
            type = response.type
        )
    }
}
