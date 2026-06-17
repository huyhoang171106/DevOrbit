# Overview

## Current Behavior

Mobile registration calls `POST /api/student/register`, treats the response as if it contains a JWT token, and sets the app as logged in. The backend register endpoint only creates an inactive student account and sends OTP; it does not return a token.

## Target Behavior

Mobile registration remains inside the auth flow. After registration, the app asks for the email OTP and calls `POST /api/student/verify-otp`. Only responses with a non-blank JWT token create a mobile session and route to the home screen.
The OTP screen lets the student resend the email verification OTP, limits OTP input to six digits, and surfaces backend error messages such as invalid or expired OTP responses.

## Affected Users

- Student.

## Affected Product Docs

- `README.md`
- `devorbit-mobile/AGENTS.md`
- `docs/TEST_MATRIX.md`

## Non-Goals

- Changing backend student auth endpoints.
- Adding forgot-password mobile screens.
