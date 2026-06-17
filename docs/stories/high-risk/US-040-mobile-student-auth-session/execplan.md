# Exec Plan

## Goal

Align mobile student registration with the backend auth contract so registration does not create a false authenticated session.

## Scope

In scope:

- Mobile auth repository register/session behavior.
- Mobile OTP verification API call.
- Mobile resend OTP API call.
- Mobile auth screen OTP state.
- Unit proof for session policy, OTP validation, OTP error parsing, and auth state transitions.

Out of scope:

- Backend auth behavior changes.
- Mobile password reset flows.

## Risk Classification

Risk flags:

- Auth.
- Public contracts.
- Cross-platform.
- Existing behavior.
- Weak proof.

Hard gates:

- Auth.

## Work Phases

1. Discovery of backend and web auth contracts.
2. Add failing unit test for session policy.
3. Implement mobile register and OTP flow.
4. Implement resend OTP, OTP input validation, backend OTP error surfacing, and register-error cleanup.
5. Run mobile unit tests and debug build/install.
6. Update test matrix.

## Stop Conditions

Pause for human confirmation if:

- Backend endpoint paths differ from the discovered `/api/student/**` contract.
- Product direction changes to require login without OTP verification.
- Validation requirements need to be weakened.
