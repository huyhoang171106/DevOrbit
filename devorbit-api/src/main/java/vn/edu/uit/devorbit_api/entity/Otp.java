package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * OTP = One-Time Password sent to a student's email for verification.
 *
 * Maps to the "otps" table.
 * Used in two flows:
 *   1. EMAIL_VERIFICATION — During registration, verify the student owns the email.
 *   2. PASSWORD_RESET — During "Forgot Password", verify identity before reset.
 *
 * Security design:
 *   - OTP expires after a few minutes (expiresAt)
 *   - Each email + purpose combination can only have ONE active OTP
 *     (sending a new one overwrites the old one)
 *   - OTP code is 6 digits, stored in plaintext (not a password)
 *     because it's short-lived and single-use
 *
 * Lifecycle:
 *   Register → Otp(email=EMAIL_VERIFICATION) → Verify → Delete OTP
 *   Forgot PW → Otp(email=PASSWORD_RESET) → Verify → Delete OTP + Reset PW
 */
@Entity
@Table(name = "otps")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Email address the OTP was sent to. */
    @Column(nullable = false)
    private String email;

    /**
     * Why this OTP was sent.
     * EMAIL_VERIFICATION or PASSWORD_RESET.
     * Prevents using a verification OTP to reset a password.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false, length = 30)
    private OtpPurpose purpose;

    /** The 6-digit OTP code. */
    @Column(name = "otp_code", nullable = false, length = 6)
    private String otpCode;

    /** When this OTP expires (usually 5-10 minutes after creation). */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
