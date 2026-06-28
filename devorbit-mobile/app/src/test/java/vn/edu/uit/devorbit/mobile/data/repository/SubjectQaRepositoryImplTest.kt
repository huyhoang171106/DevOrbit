package vn.edu.uit.devorbit.mobile.data.repository

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response
import vn.edu.uit.devorbit.mobile.domain.repository.SubjectQaEvent
import vn.edu.uit.devorbit.mobile.network.ApiService

class SubjectQaRepositoryImplTest {

    @Test
    fun stream_parsesStatusDeltasAndCompletion() = runTest {
        val apiService = mock<ApiService>()
        val sse = """
            event:status
            data:{"type":"status","stage":"analyze","message":"Đang phân tích câu hỏi"}

            event:delta
            data:{"type":"delta","content":"Xin "}

            event:delta
            data:{"type":"delta","content":"chào"}

            event:complete
            data:{"type":"complete","stage":"done","response":{"answer":"Xin chào","sessionId":"session-1","relevantNodeIds":[],"sources":["DevOrbit"],"type":"DIRECT"}}

        """.trimIndent()
        whenever(apiService.streamSubjectQa(any())).thenReturn(
            Response.success(sse.toResponseBody("text/event-stream".toMediaType()))
        )

        val events = SubjectQaRepositoryImpl(apiService)
            .stream("Xin chào", null)
            .toList()

        assertEquals(4, events.size)
        assertEquals(
            SubjectQaEvent.Status("analyze", "Đang phân tích câu hỏi"),
            events[0]
        )
        assertEquals(SubjectQaEvent.Delta("Xin "), events[1])
        assertEquals(SubjectQaEvent.Delta("chào"), events[2])
        val complete = events[3] as SubjectQaEvent.Complete
        assertEquals("Xin chào", complete.answer.answer)
        assertEquals("session-1", complete.answer.sessionId)
        assertEquals(listOf("DevOrbit"), complete.answer.sources)
    }
}
