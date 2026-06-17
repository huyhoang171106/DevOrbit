# Design

## Domain Model

Mobile student session state is derived from a real JWT token. Registration profile data is not session data.

## Application Flow

1. Student submits registration details.
2. Mobile calls `POST /api/student/register`.
3. Mobile shows an OTP entry state and does not save a token.
4. Student may resend the OTP from the OTP entry state.
5. Student submits a six-digit OTP.
6. Mobile calls `POST /api/student/verify-otp`.
7. Mobile saves the returned token and enters the authenticated app.

## Interface Contract

- `POST /api/student/register` returns student profile fields and no token.
- `POST /api/student/verify-otp` returns `token`, `studentCode`, `fullName`, and `email`.
- `POST /api/student/resend-otp` accepts the registered email and sends a fresh email verification OTP.
- `POST /api/student/login` remains the explicit login endpoint.

## Data Model

No mobile or backend data model changes.

## UI / Platform Impact

The Android registration screen now has an OTP step. The home screen is reachable only after login or OTP verification. OTP input accepts only numeric characters and stores at most six digits. Invalid or expired OTP messages from the backend are shown in the OTP state.

## Observability

No new logs or telemetry.

## Alternatives Considered

1. Return directly to login after registration. This avoids the false session, but it leaves inactive accounts unable to log in until OTP is handled elsewhere.
