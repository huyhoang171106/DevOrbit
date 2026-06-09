package vn.edu.uit.devorbit.mobile.ui.viewmodel

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthSessionPolicyTest {

    @Test
    fun `register does not create session`() {
        assertFalse(AuthSessionPolicy.isAuthenticatedAfterRegister())
    }

    @Test
    fun `blank token is not authenticated`() {
        assertFalse(AuthSessionPolicy.isAuthenticatedWithToken(""))
    }

    @Test
    fun `non-blank token is authenticated`() {
        assertTrue(AuthSessionPolicy.isAuthenticatedWithToken("eyJhbGciOiJIUzI1NiJ9.test"))
    }
}
