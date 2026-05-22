# Security Audit — devorbit-api

**Date:** 2026-05-22
**Scope:** Spring Boot 4.0.6 backend API (Java 21)
**Method:** Manual code review against OWASP Top 10 + security-and-hardening skill checklist

---

## Rating Scale

| Severity | Meaning |
|----------|---------|
| 🔴 **CRITICAL** | Exploitable remotely, no auth required, high impact (data leak, RCE, account takeover) |
| 🟠 **HIGH** | Exploitable with constraints, moderate impact |
| 🟡 **MEDIUM** | Limited exploitability, low impact, or requires existing access |
| 🟢 **LOW** | Best-practice gap, unlikely to be exploited in current context |
| ✅ **PASS** | Properly handled |

---

## 1. Authentication (JWT Handling)

| Issue | Severity | Detail |
|-------|----------|--------|
| JWT signing & validation | ✅ **PASS** | jjwt 0.12.6 HMAC-SHA, `verifyWith()`, expiration check |
| Default secret in prod | ✅ **PASS** | Sentinel string checked; prod/staging profile throws `IllegalStateException` |
| Token type → role mapping | ✅ **PASS** | JWT `type` claim maps to `ROLE_` authority (ADMIN vs STUDENT) |
| Token revocation | 🟡 **MEDIUM** | No blacklist/revocation mechanism. Once issued, valid full 120 min. |
| JWT expiry default | 🟢 **LOW** | 120 min generous. Consider 30-60 min with refresh token flow. |
| Secret key derivation | ✅ **PASS** | `Keys.hmacShaKeyFor()` from UTF-8 bytes — secure |

**Token Structure:**
```
Claims: { sub: <username>, type: <ADMIN|STUDENT>, iat, exp }
IssuedAt + Expiration = current + jwtProperties.expirationMinutes (default 120)
```

---

## 2. Authorization

| Issue | Severity | Detail |
|-------|----------|--------|
| Admin endpoints | ✅ **PASS** | `.hasAuthority("ROLE_ADMIN")` on `/api/admin/**` |
| Student endpoints | ✅ **PASS** | `.authenticated()` on `/api/student/**` |
| Public endpoints | ✅ **PASS** | Explicit `.permitAll()` on open endpoints |
| Default deny | ✅ **PASS** | `.anyRequest().denyAll()` — whitelist-only model |
| Photobooth access | ✅ **PASS** | GET = permitAll, write = ROLE_ADMIN |
| Swagger/OpenAPI | ✅ **PASS** | Requires ROLE_ADMIN |
| Auth error responses | ✅ **PASS** | 401 → `{"error": "Unauthorized"}`, 403 → `{"error": "Forbidden"}` |
| Student registration | ✅ **PASS** | Checks duplicate studentCode and email before creating |

---

## 3. Input Validation

| Issue | Severity | Detail |
|-------|----------|--------|
| `@Valid` on controller params | ✅ **PASS** | AdminCourseController, PublicAiController, PhotoboothFrameController GET |
| **PhotoboothFrameController POST** | 🟡 **MEDIUM** | `@RequestBody PhotoboothFrameDTO dto` — **missing `@Valid`** |
| DTO validation annotations | ✅ **PASS** | `@NotBlank`, `@NotNull`, `@Email`, `@Size` on most DTOs |
| String length limits | ✅ **PASS** | AiQueryRequest: 500 chars, RoadmapGenerationRequest: 2000 / 200, StudentRegister: 6-100 pw |
| Path variable validation | 🟢 **LOW** | `@PathVariable Long id` — Spring auto-binds to Long, returns 400 on invalid. Safe. |
| LoginRequest validation | ✅ **PASS** | `@NotBlank` on username + password |

---

## 4. File Upload (SupabaseStorageService)

| Issue | Severity | Detail |
|-------|----------|--------|
| **No file type validation** | 🟠 **HIGH** | Uses `file.getContentType()` from client header — trivially spoofable. No magic byte check. |
| **No file size limit** | 🟠 **HIGH** | No `spring.servlet.multipart.max-file-size` or programmatic check. Attacker can upload arbitrary large files. |
| Filename handling | 🔶 **OK-ish** | UUID prepended to original filename, but original name preserved (not sanitized for path traversal) |
| Storage auth | ✅ **PASS** | Supabase key is not exposed client-side. Upload goes server → Supabase. |

**Code location:** `SupabaseStorageService.upload(MultipartFile file)` — 0 validation lines before sending to Supabase.

---

## 5. Rate Limiting

| Issue | Severity | Detail |
|-------|----------|--------|
| **No rate limiting anywhere** | 🔴 **CRITICAL** | No rate limiting on: auth login, registration, AI endpoints, or any other endpoint |
| Attack surface (unauthenticated) | 🔴 **CRITICAL** | `/api/admin/auth/login` — brute force admin passwords. `/api/student/login` — brute force student codes. `/api/student/register` — mass account creation. `/api/ai/**` — LLM abuse, cost bombing. |
| Missing dependency | — | `spring-boot-starter-actuator` not used for rate limiting; no Spring Cloud Gateway, no bucket4j, no custom filter |

---

## 6. Error Handling

| Issue | Severity | Detail |
|-------|----------|--------|
| Global exception handler | ✅ **PASS** | `@RestControllerAdvice` catches all exceptions |
| No stack trace leak | ✅ **PASS** | General exception returns `{"error": "Internal server error"}` — info hidden |
| Auth error ambiguity | ✅ **PASS** | Login failure: "Invalid username or password" — doesn't reveal which field is wrong |
| Validation errors | ✅ **PASS** | Returns field-level error messages in 400 response |
| Logging | ✅ **PASS** | General exception logged server-side via `log.error()` |

---

## 7. CORS Configuration

| Issue | Severity | Detail |
|-------|----------|--------|
| Dynamic origins | ✅ **PASS** | `CORS_ALLOWED_ORIGINS` env var, defaults to localhost:3000,5173 |
| Methods restricted | ✅ **PASS** | GET/POST/PUT/DELETE/OPTIONS only |
| Headers restricted | ✅ **PASS** | Authorization, Content-Type, Accept |
| Credentials | 🔶 **OK** | `allowCredentials(true)` with `allowedOriginPatterns` instead of wildcard |
| CSRF disabled | 🟢 **LOW** | `csrf.disable()` — accepted for stateless JWT API. Risk: no CSRF token for cookie-based sessions (not used here) |

---

## 8. Security Headers

| Issue | Severity | Detail |
|-------|----------|--------|
| Spring Security defaults | ✅ **PASS** | Spring Security 6.x applies default headers (CSP, HSTS, X-Frame-Options, X-Content-Type-Options) automatically |
| Explicit configuration | 🟢 **LOW** | No custom `.headers()` config in `SecurityConfig`. If defaults suffice, fine. But CSP content-policy defaults are lenient. |

Spring Security 6.x default headers applied:
- `Cache-Control: no-cache, no-store, max-age=0, must-revalidate`
- `X-Content-Type-Options: nosniff`
- `Strict-Transport-Security: max-age=31536000 ; includeSubDomains`
- `X-Frame-Options: DENY`
- no default CSP (adds X-XSS-Protection: 0)

These are acceptable defaults. CSP could be tightened for production.

---

## 9. SQL Injection

| Issue | Severity | Detail |
|-------|----------|--------|
| JPA repositories | ✅ **PASS** | All 17 repositories extend `JpaRepository` — parameterized queries by default |
| No native SQL seen | ✅ **PASS** | No `@Query(nativeQuery=true)` or raw `EntityManager` usage observed in reviewed services |
| Seed data | ✅ **PASS** | `data.sql` is static, no user input interpolation |

---

## 10. Secrets Management

| Issue | Severity | Detail |
|-------|----------|--------|
| `.env` in `.gitignore` | ✅ **PASS** | Confirmed via `git check-ignore` |
| `.env.example` committed | ✅ **PASS** | Template with placeholder values, no real secrets |
| JWT sentinel in production | ✅ **PASS** | Throws exception if default secret used in prod/staging |
| Database credentials | ✅ **PASS** | Via `DATABASE_*` env vars with sensible defaults |
| GitHub token | ✅ **PASS** | Via `GITHUB_TOKEN` env var |
| Supabase credentials | ✅ **PASS** | Via `SUPABASE_*` env vars |
| Mail credentials | ✅ **PASS** | Via `MAIL_*` env vars |

---

## Summary

| Severity | Count | Key Issues |
|----------|-------|------------|
| 🔴 CRITICAL | 1 | No rate limiting on auth or AI endpoints |
| 🟠 HIGH | 1 | File upload: no type/size validation |
| 🟡 MEDIUM | 3 | JWT no revocation, PhotoboothFrameDTO missing @Valid, public AI endpoints open |
| 🟢 LOW | 3 | 2hr JWT expiry, no explicit CSP config, CSRF disabled for stateless API |
| ✅ PASS | 21 | Auth, authorization, input validation, error handling, secrets, CORS, SQL injection |

---

## Priority Remediation

### 🔴 1. Add Rate Limiting (Critical)

Spring Boot rate limiting options (pick one):

**Option A: Bucket4j (lightweight, no infra)**
```xml
<dependency>
    <groupId>com.bucket4j</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.7.0</version>
</dependency>
```

**Option B: Spring Cloud Gateway with Redis (scalable)**
Requires gateway layer — overkill for single API.

**Option C: Custom Filter (simplest)**
```java
@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    private final Map<String, AtomicInteger> counters = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS = 100;
    private static final long WINDOW_MS = 60_000;

    @Override
    protected void doFilterInternal(HttpServletRequest request, ...)
            throws ServletException, IOException {
        String key = request.getRemoteAddr() + ":" + request.getRequestURI();
        // Check & increment counter, return 429 if exceeded
    }
}
```

**Minimal first step:** Add rate limiting on:
- `POST /api/admin/auth/login` — 10 req/min/IP
- `POST /api/student/login` — 10 req/min/IP
- `POST /api/student/register` — 3 req/min/IP
- `POST /api/ai/**` — 20 req/min/IP
- `POST /api/student/send-otp` — 5 req/min/IP

### 🟠 2. Validate File Uploads (High)

In `SupabaseStorageService.upload()`:

```java
public Map<String, String> upload(MultipartFile file) {
    // 1. Check file size
    if (file.getSize() > 5 * 1024 * 1024) { // 5MB
        throw new BadRequestException("File too large. Max 5MB.");
    }

    // 2. Check content type (first line defense)
    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
        throw new BadRequestException("Only image files allowed.");
    }

    // 3. Verify magic bytes
    byte[] header = new byte[8];
    try (InputStream is = file.getInputStream()) {
        is.read(header);
    }
    if (!isPng(header) && !isJpeg(header) && !isWebp(header)) {
        throw new BadRequestException("Invalid image format.");
    }

    // 4. Sanitize filename
    String originalName = file.getOriginalFilename();
    if (originalName != null) {
        originalName = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
    String fileName = UUID.randomUUID() + "_" + originalName;
    // ... rest of upload
}

private boolean isPng(byte[] header) {
    return header[0] == (byte)0x89 && header[1] == 0x50 && header[2] == 0x4E && header[3] == 0x47;
}
private boolean isJpeg(byte[] header) {
    return header[0] == (byte)0xFF && header[1] == (byte)0xD8 && header[2] == (byte)0xFF;
}
private boolean isWebp(byte[] header) {
    return header[0] == 0x52 && header[1] == 0x49 && header[2] == 0x46 && header[3] == 0x46;
}
```

Also add to `application.yaml`:
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 5MB
      max-request-size: 10MB
```

### 🟡 3. Add @Valid to PhotoboothFrameController (Medium)

```java
@PostMapping
public ResponseEntity<PhotoboothFrameDTO> upsertFrame(
    @RequestBody @Valid PhotoboothFrameDTO dto) {  // ← add @Valid
```

Also add validation annotations to `PhotoboothFrameDTO`.

### 🟡 4. Consider JWT Refresh Token Flow (Medium)

Not urgent but recommended:
- Short-lived access token (15-30 min)
- Long-lived refresh token (7-30 days) stored in httpOnly cookie
- `/api/auth/refresh` endpoint

---

## Good Security Practices Already in Place

- BCrypt password hashing
- JWT with HMAC-SHA validation
- Default secret sentinel blocks production
- denyAll() fallback for unmatched routes
- Auth error messages don't leak info
- Global exception handler hides stack traces
- Environment-based secrets (no hardcoded creds)
- `.env` gitignored
- Input validated at controller boundary with @Valid
- CORS restricted to configurable origins
- String length limits on AI query inputs (500 char max)
- Student email format validated with @Email
- Password minimum length 6 enforced
- Role-based access: ROLE_ADMIN vs ROLE_STUDENT
- Swagger protected by ROLE_ADMIN
- Photobooth GET is public, write requires admin

---

## Quick Wins (Low Effort)

| Fix | Effort | Impact |
|-----|--------|--------|
| Rate limit login endpoints | 1 hour | Blocks brute force |
| Rate limit AI endpoints | 1 hour | Blocks LLM abuse |
| File upload validation | 2 hours | Prevents RCE/media attacks |
| Add `@Valid` to PhotoboothFrameDTO | 15 min | Closes unvalidated input |
| Add `spring.servlet.multipart.max-file-size` | 5 min | Limits upload abuse |

---

*Audit performed using security-and-hardening skill checklist + manual code review.*
