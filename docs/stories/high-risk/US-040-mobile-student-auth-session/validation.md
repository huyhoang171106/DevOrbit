# Validation

## Proof Strategy

Unit proof covers the session policy, OTP validation, OTP error-message parsing, and auth state transitions. Gradle debug build covers Android compile and packaging. Emulator install is a platform check when a device is connected.

## Test Plan

| Layer | Cases |
| --- | --- |
| Unit | Register success is not authenticated; non-blank token authenticates; OTP input sanitizes to six digits; OTP backend messages are parsed; switching to login clears register errors |
| Integration | Not run; backend contract was inspected from controller/service code |
| E2E | Not run |
| Platform | Android debug build; install to emulator when `adb` reports a connected device |
| Performance | Not applicable |
| Logs/Audit | Not applicable |

## Fixtures

- No external fixtures.

## Commands

```text
devorbit-mobile: .\gradlew.bat :app:testDebugUnitTest --tests vn.edu.uit.devorbit.mobile.ui.viewmodel.AuthSessionPolicyTest
devorbit-mobile: .\gradlew.bat :app:testDebugUnitTest --tests vn.edu.uit.devorbit.mobile.ui.viewmodel.AuthValidationTest
devorbit-mobile: .\gradlew.bat :app:testDebugUnitTest --tests vn.edu.uit.devorbit.mobile.data.repository.AuthErrorMessageParserTest
devorbit-mobile: .\gradlew.bat :app:testDebugUnitTest --tests vn.edu.uit.devorbit.mobile.ui.viewmodel.AuthStateTransitionsTest
devorbit-mobile: .\gradlew.bat :app:testDebugUnitTest
devorbit-mobile: .\gradlew.bat :app:assembleDebug
devorbit-mobile: .\gradlew.bat :app:installDebug
```

## Acceptance Evidence

- `.\gradlew.bat :app:testDebugUnitTest --tests vn.edu.uit.devorbit.mobile.ui.viewmodel.AuthSessionPolicyTest` passed.
- `.\gradlew.bat :app:testDebugUnitTest --tests vn.edu.uit.devorbit.mobile.ui.viewmodel.AuthValidationTest` passed.
- `.\gradlew.bat :app:testDebugUnitTest --tests vn.edu.uit.devorbit.mobile.data.repository.AuthErrorMessageParserTest` passed.
- `.\gradlew.bat :app:testDebugUnitTest --tests vn.edu.uit.devorbit.mobile.ui.viewmodel.AuthStateTransitionsTest` passed.
- `.\gradlew.bat :app:testDebugUnitTest` passed.
- `.\gradlew.bat :app:assembleDebug` passed.
- `.\gradlew.bat :app:installDebug` did not complete because `adb devices` returned no connected devices.
