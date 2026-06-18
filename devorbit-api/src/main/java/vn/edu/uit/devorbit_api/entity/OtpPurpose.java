package vn.edu.uit.devorbit_api.entity;

/**
 * WHY an OTP code was sent.
 *
 * EMAIL_VERIFICATION — Verifying a new student's email during registration.
 *                      Required before the account becomes active.
 *
 * PASSWORD_RESET     — Proving identity before allowing a password change.
 *                      Sent when student uses "Forgot Password".
 *
 * The purpose field in the OTP table prevents using a verification OTP
 * to reset a password, and vice versa (security measure).
 */
public enum OtpPurpose {
    EMAIL_VERIFICATION,
    PASSWORD_RESET
}
