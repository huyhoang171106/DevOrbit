package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.Otp;

import vn.edu.uit.devorbit_api.entity.OtpPurpose;
import java.util.Optional;

/**
 * OTP REPOSITORY = data access for one-time passwords (email verification + password reset).
 *
 * findTopByEmailAndPurposeOrderByCreatedAtDesc = get the MOST RECENT OTP
 * for a given email + purpose. "Top" = LIMIT 1 in SQL.
 *
 * OTPs expire after a few minutes. Expired OTPs are cleaned up periodically
 * or deleted after successful verification.
 *
 * Security: when a new OTP is generated, the old one is deleted first (resend flow).
 * This prevents multiple valid OTPs existing for the same email.
 */
@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {

    /**
     * Find the most recent OTP for an email (regardless of purpose).
     * Used as a fallback in verification flows.
     */
    Optional<Otp> findTopByEmailOrderByCreatedAtDesc(String email);

    /**
     * Find the most recent OTP for an email + specific purpose.
     * This is the MAIN method used during OTP verification.
     * The purpose distinction prevents using a password-reset OTP for email verification.
     */
    Optional<Otp> findTopByEmailAndPurposeOrderByCreatedAtDesc(String email, OtpPurpose purpose);

    /** Delete all OTPs for an email (used after successful verification). */
    void deleteByEmail(String email);

    /** Delete OTPs for a specific email + purpose (used when resending OTP). */
    void deleteByEmailAndPurpose(String email, OtpPurpose purpose);
}
