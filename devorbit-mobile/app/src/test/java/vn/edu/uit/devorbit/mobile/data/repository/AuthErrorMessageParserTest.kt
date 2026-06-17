package vn.edu.uit.devorbit.mobile.data.repository

import org.junit.Assert.assertEquals
import org.junit.Test

class AuthErrorMessageParserTest {

    @Test
    fun `message from response map becomes thrown token error`() {
        val response = mapOf<String, Any>("message" to "OTP expired")

        val error = try {
            AuthErrorMessageParser.requireToken(response, "OTP verification failed")
            null
        } catch (e: Exception) {
            e
        }

        assertEquals("OTP expired", error?.message)
    }

    @Test
    fun `message from http error json is preferred over fallback`() {
        val message = AuthErrorMessageParser.messageFromErrorBody(
            """{"message":"Invalid OTP"}""",
            "OTP verification failed"
        )

        assertEquals("Invalid OTP", message)
    }

    @Test
    fun `malformed error body falls back to default message`() {
        val message = AuthErrorMessageParser.messageFromErrorBody(
            "not json",
            "OTP verification failed"
        )

        assertEquals("OTP verification failed", message)
    }
}
