package vn.edu.uit.devorbit.mobile.ui.viewmodel

/**
 * Deterministic session policy — no token means not authenticated.
 */
object AuthSessionPolicy {

    /**
     * After registration the backend sends OTP; no JWT is issued yet.
     * The app must not treat registration as a login.
     */
    fun isAuthenticatedAfterRegister(): Boolean = false

    /**
     * A non-blank token from verify-otp or login grants an authenticated session.
     */
    fun isAuthenticatedWithToken(token: String): Boolean = token.isNotBlank()
}
