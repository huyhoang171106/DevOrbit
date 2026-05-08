$path = "D:\devorbit\docs\deployment-guide.md"

$block = @"
### 1.2. Kiến trúc tổng thể

#### Sơ đồ luồng dữ liệu (Data Flow Diagram)

```
+----------------------------------------------------------+
|                      NGƯỜI DÙNG                           |
+----+----------------------+---------------------+---------+
     |                      |                     |
     v                      v                     v
+----------+        +---------------+        +-----------+
|  Trình   |        |    Mobile     |        |   Admin   |
|  duyệt   |        |   (Android)   |        |  (Browser)|
|  Student |        |               |        |           |
+-----+----+        +-------+-------+        +-----+-----+
      |                      |                      |
      |  HTTP/HTTPS          |  HTTP/HTTPS          |  HTTP/HTTPS
      |                      |                      |
      v                      v                      v
+----------------------------------------------------------+
|                       NGINX (Port 80)                      |
|  +------------------------------------------------------+ |
|  |  location / -> React SPA (index.html)                | |
|  |  location /api/ -> proxy_pass http://api:8080        | |
|  +------------------------------------------------------+ |
+-----------------------------+----------------------------+
                              |
                              v
                    +--------------------+
                    |  Spring Boot API    |
                    |   (Port 8080)       |
                    |  +---------------+  |
                    |  |JWT Filter     |  |
                    |  +---------------+  |
                    |  |Controllers    |  |
                    |  +---------------+  |
                    |  |Services       |  |
                    |  +---------------+  |
                    |  |Repositories   |  |
                    |  +---------------+  |
                    +----------+---------+
                               |
                               v
                    +--------------------+
                    |  PostgreSQL 16      |
                    |  (Port 5432)        |
                    +--------------------+
```

Mobile App (emulator): Retrofit -> http://10.0.2.2:8080 (trực tiếp)
Mobile App (thiết bị thật): Retrofit -> https://api.example.com

#### Sơ đồ luồng xác thực (Authentication Flow)

```
+----------+        +---------------+        +-----------+
|  Client  |        |  Nginx / API  |        |   JWT     |
|          |        |               |        |  Service  |
+----+-----+        +-------+-------+        +-----+-----+
     |                      |                      |
     |  POST /login         |                      |
     |  (credentials)       |                      |
     +--------------------->|                      |
     |                      |  validate creds      |
     |                      +--------------------->|
     |                      |  generate JWT        |
     |                      |<---------------------+
     |<---- JWT token ------|                      |
     |                      |                      |
     |  GET /protected      |                      |
     |  Authorization:      |                      |
     |  Bearer <token>      |                      |
     +--------------------->|                      |
     |                      | JwtAuthFilter        |
     |                      | validates token      |
     |                      +--------------------->|
     |                      | set SecurityContext  |
     |                      |<---------------------+
     |<---- response -------|                      |
```
"@
Add-Content -Path $path -Value $block -Encoding UTF8
Write-Host "Added section 1.2"
