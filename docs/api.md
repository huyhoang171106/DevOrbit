# REST API and WebSocket Reference

This document provides a reference for the REST endpoints and WebSocket STOMP mappings exposed by the devorbit-api backend.

## Base Context Path
All REST endpoints are prefixed by `/api`. WebSockets are exposed on `/ws/community`.

## Authentication Requirements
- PermitAll: Public endpoints requiring no Authorization headers.
- ROLE_ADMIN: Protected endpoints requiring an administrator JWT token.
- ROLE_STUDENT: Protected endpoints requiring an active student JWT token.

## REST Endpoints

### 1. Public Courses
- GET /api/courses
  - Method: GET
  - Access: PermitAll
  - Description: Retrieve a paginated list of all active courses.
  - Query Params: page (default: 0), size (default: 10).
- GET /api/courses/{courseId}
  - Method: GET
  - Access: PermitAll
  - Description: Get detailed course syllabus, outcomes, assessments, and reference materials.

### 2. Public Repositories
- GET /api/repos
  - Method: GET
  - Access: PermitAll
  - Description: Retrieve a list of approved GitHub repositories.
  - Query Params: courseId (optional filter), language (optional filter), techStack (optional filter).
- POST /api/repos/candidate
  - Method: POST
  - Access: PermitAll
  - Description: Submit a new repository candidate for administrative review.
  - Request Body: courseId (Long), repoUrl (String), studentName (String).

### 3. Public Discovery Feed
- GET /api/discovery/feed
  - Method: GET
  - Access: PermitAll
  - Description: Get the discovery feed including newly approved repositories, active courses, and community highlights.

### 4. Public Tech Stacks
- GET /api/tech-stacks
  - Method: GET
  - Access: PermitAll
  - Description: List all technology stacks available in the system.

### 5. Public Photobooth
- GET /api/photobooth/frames
  - Method: GET
  - Access: PermitAll
  - Description: Fetch active photobooth overlay frames.
- POST /api/photobooth/frames
  - Method: POST
  - Access: ROLE_ADMIN
  - Description: Create a new photobooth frame configuration.
  - Request Body: frameUrl (String), title (String).

### 6. AI Features and Subject Q&A
- GET /api/ai/repo/{repoId}/summary
  - Method: GET
  - Access: PermitAll
  - Description: Get an AI-generated summary of a GitHub repository.
- GET /api/ai/repo/{repoId}/advice
  - Method: GET
  - Access: PermitAll
  - Description: Retrieve AI tutor advice or explanations for a specific repository.
- POST /api/ai/generate-roadmap
  - Method: POST
  - Access: PermitAll
  - Description: Generate a learning roadmap recommendations plan.
  - Request Body: year (Integer), field (String), experience (String).
- POST /api/ai/knowledge-graph/query
  - Method: POST
  - Access: PermitAll
  - Description: Submit a natural language question about the course knowledge graph.
  - Request Body: query (String).
- POST /api/ai/chat
  - Method: POST
  - Access: ROLE_STUDENT / ROLE_ADMIN (Authenticated)
  - Description: Interact with the conversational AI chat assistant.
  - Request Body: message (String), sessionId (UUID).
- GET /api/ai/chat/{sessionId}/history
  - Method: GET
  - Access: ROLE_STUDENT / ROLE_ADMIN (Authenticated)
  - Description: Get conversation logs for a chat session.
- POST /api/ai/subject-qa/query
  - Method: POST
  - Access: PermitAll
  - Description: Synchronous advisory Q&A utilizing RAG.
  - Request Body: question (String).
- POST /api/ai/subject-qa/stream
  - Method: POST
  - Access: PermitAll
  - Description: Stream RAG-based advisory answers using Server-Sent Events (SSE).
  - Headers: Accept: text/event-stream
  - Request Body: question (String).

### 7. Student Authentication
- POST /api/student/register
  - Method: POST
  - Access: PermitAll
  - Description: Register a new student profile.
  - Request Body: studentCode (String), fullName (String), email (String), password (String).
- POST /api/student/login
  - Method: POST
  - Access: PermitAll
  - Description: Authenticate student credentials and return a JWT.
  - Request Body: studentCode (String), password (String).
- POST /api/student/verify-otp
  - Method: POST
  - Access: PermitAll
  - Description: Submit verification OTP received in email.
  - Request Body: studentCode (String), otpCode (String).
- POST /api/student/forgot-password
  - Method: POST
  - Access: PermitAll
  - Description: Request a password reset OTP.
- POST /api/student/reset-password
  - Method: POST
  - Access: PermitAll
  - Description: Reset password with OTP code.

### 8. Student Features
- GET /api/student/profile
  - Method: GET
  - Access: ROLE_STUDENT
  - Description: Fetch profile details of the logged-in student.
- POST /api/student/bookmarks
  - Method: POST
  - Access: ROLE_STUDENT
  - Description: Bookmark a course or repository.
  - Request Body: bookmarkType (String), itemId (Long).
- GET /api/student/bookmarks
  - Method: GET
  - Access: ROLE_STUDENT
  - Description: List all bookmarks saved by the student.
- GET /api/student/community
  - Method: GET
  - Access: ROLE_STUDENT
  - Description: Retrieve a list of chat channels available to the student.
- GET /api/student/community/channels/{channelId}/messages
  - Method: GET
  - Access: ROLE_STUDENT
  - Description: Fetch message logs in a specific channel.
  - Query Params: page (default: 0), size (default: 50).

### 9. Admin Authentication
- POST /api/admin/auth/login
  - Method: POST
  - Access: PermitAll
  - Description: Authenticate administrator credentials and return an admin JWT.
  - Request Body: username (String), password (String).

### 10. Admin Dashboards
- GET /api/admin/stats
  - Method: GET
  - Access: ROLE_ADMIN
  - Description: Return aggregate counts (total students, repositories, pending candidates).
- GET /api/admin/repos/candidates
  - Method: GET
  - Access: ROLE_ADMIN
  - Description: List repository candidates pending review.
- POST /api/admin/repos/candidates/{candidateId}/approve
  - Method: POST
  - Access: ROLE_ADMIN
  - Description: Approve a candidate repository.
- POST /api/admin/repos/candidates/{candidateId}/reject
  - Method: POST
  - Access: ROLE_ADMIN
  - Description: Reject a candidate repository.

---

## WebSocket STOMP API

### Connection Details
- Connection Endpoint: `/ws/community`
- Protocol: STOMP over SockJS
- Required Connect Header: `Authorization: Bearer <STUDENT_JWT_TOKEN>`

### Message Mappings

#### Send Message
- Destination: `/app/chat.send/{channelId}`
- Role Required: ROLE_STUDENT
- Payload Format (JSON):
  ```json
  {
    "content": "Message text content"
  }
  ```

#### Listen Message
- Subscription Channel: `/topic/channel/{channelId}`
- Broadcast Format (JSON):
  ```json
  {
    "messageId": 456,
    "content": "Message text content",
    "studentCode": "2152xxxx",
    "senderName": "Student Full Name",
    "timestamp": "2026-06-17T14:20:09Z"
  }
  ```
