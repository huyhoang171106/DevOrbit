# Hướng dẫn setup môi trường — DevOrbit Mobile & Admin

Tài liệu này hướng dẫn cài đặt môi trường để chạy source code Android của **devorbit-mobile** (app Student) và **devorbit-admin** (app Admin) sau khi giải nén file zip dự án.

---

## 1. Yêu cầu hệ thống

| Phần mềm | Phiên bản | Ghi chú |
|----------|-----------|---------|
| **JDK** | 17 hoặc 21 | JVM target là 17, dùng JDK 17 hoặc 21 đều được. **KHÔNG dùng JDK 25** (Kotlin 2.0.21 không hỗ trợ). |
| **Android Studio** | mới nhất | Tải từ [developer.android.com/studio](https://developer.android.com/studio) |
| **Android SDK** | 35 (compileSdk) | SDK Manager trong Android Studio sẽ tự động cài nếu chưa có |
| **PostgreSQL** | 15+ | Có thể cài local hoặc dùng Supabase (cloud) |
| **Git** | bất kỳ | Optional |

### 1.1. Cài đặt JDK

> Dùng JDK 17 **hoặc** 21 đều được.

**Cách 1 — Eclipse Temurin (khuyến nghị):**
- JDK 17: https://adoptium.net/temurin/releases/?version=17
- JDK 21: https://adoptium.net/temurin/releases/?version=21

**Cách 2 — Dùng JDK đi kèm Android Studio:**
`C:\Program Files\Android\Android Studio\jbr`

Kiểm tra sau khi cài:

```powershell
java -version
```

Kết quả hiển thị `openjdk version "17.0.x"` hoặc `"21.0.x"`.

### 1.2. Cài đặt Android Studio

1. Tải và cài Android Studio theo hướng dẫn trên trang chủ.
2. Lần đầu mở, chọn **More Actions → SDK Manager**.
3. Trong tab **SDK Platforms**, tick **Android 15.0 (API 35)** → Apply.
4. Trong tab **SDK Tools**, đảm bảo **Android SDK Build-Tools 35** đã được cài.

### 1.3. Cài đặt PostgreSQL (nếu dùng local)

1. Tải PostgreSQL 15+ từ https://www.postgresql.org/download/windows/
2. Trong quá trình cài, nhớ mật khẩu user `postgres`.
3. Mở **pgAdmin** hoặc PowerShell và tạo database:

```powershell
createdb -U postgres devorbit_db
```

> Nếu không muốn cài local, có thể dùng Supabase (cloud, miễn phí). Khi đó sẽ cần URL kết nối từ Supabase dashboard.

---

## 2. Sau khi giải nén file zip

### 2.1. Xóa local.properties cũ

File `devorbit-mobile/local.properties` và `devorbit-admin/local.properties` chứa đường dẫn SDK của máy cũ. **Phải xóa 2 file này** trước khi mở project:

```powershell
Remove-Item devorbit-mobile/local.properties
Remove-Item devorbit-admin/local.properties
```

Android Studio sẽ tự tạo lại file mới khi mở project.

### 2.2. Kiểm tra file .env

Trong thư mục `devorbit-api/` đã có file `.env`. Mở file này và kiểm tra/điền các thông tin sau:

- `DATABASE_URL` — đường dẫn tới database (xem mục 3)
- `DATABASE_USERNAME` — thường là `postgres`
- `DATABASE_PASSWORD` — mật khẩu database
- `JWT_SECRET` — chuỗi bí mật ít nhất 256 bit (có thể dùng bất kỳ chuỗi dài nào)
- `OPENCODE_API_KEY` — không cần để chạy thử, có thể để trống
- `FIREWORKS_API_KEY` — có thể để trống nếu chỉ chạy thử
- `EXA_API_KEY` — có thể để trống nếu chỉ chạy thử

Các API key trên chỉ cần khi muốn dùng tính năng AI Tutor, embedding, tìm kiếm.

---

## 3. Cơ sở dữ liệu

### 3.1. Tạo database

**Nếu dùng PostgreSQL local:**

```powershell
psql -U postgres -c "CREATE DATABASE devorbit_db;"
```

(nếu chưa tạo ở bước 1.3)

**Nếu dùng Supabase:**
1. Tạo project tại https://supabase.com
2. Copy connection string ở **Project Settings → Database → Connection string**

### 3.2. Import schema

Chạy file SQL schema lên database vừa tạo:

```powershell
psql -U postgres -d devorbit_db -f supabase_complete_schema.sql
```

(Nếu dùng Supabase, mở SQL Editor và paste nội dung file `supabase_complete_schema.sql` vào rồi Run.)

---

## 4. Chạy Backend (bắt buộc)

Android apps cần backend API chạy để hoạt động. Phải làm bước này trước.

### 4.1. Cấu hình database

Kiểm tra file `devorbit-api/.env` đã điền đúng `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` chưa (xem mục 2.2).

Ví dụ với PostgreSQL local:

```env
DATABASE_URL=jdbc:postgresql://localhost:5432/devorbit_db
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=123456
```

### 4.2. Build & chạy

```powershell
cd devorbit-api
.\mvnw.cmd compile -B
.\run.bat
```

- Lần đầu chạy sẽ tải dependencies (~2-5 phút tuỳ internet).
- Backend chạy trên `http://localhost:8080`.
- Kiểm tra: mở trình duyệt vào `http://localhost:8080/swagger-ui.html` (nếu có) hoặc `http://localhost:8080/api/courses`.

> Có thể tắt backend bằng `Ctrl+C`.

---

## 5. Cấu hình API URL cho Android

Trong thư mục gốc của từng module đã có sẵn file `.env`:

- `devorbit-mobile/.env` → `MOBILE_API_BASE_URL=http://10.0.2.2:8080`
- `devorbit-admin/.env` → `ADMIN_API_BASE_URL=http://10.0.2.2:8080`

**Giải thích:**
- `10.0.2.2` là địa chỉ đặc biệt để Android Emulator truy cập tới máy host (localhost). Giá trị này phù hợp với môi trường dev mặc định.
- Nếu chạy trên **máy thật** (phone, tablet), cần đổi thành IP thật của máy chạy backend (ví dụ: `http://192.168.1.100:8080`).
- Các app đọc biến này lúc build và inject vào `BuildConfig.API_BASE_URL`.

---

## 6. Build & chạy Android apps

Mở PowerShell hoặc CMD tại thư mục gốc đã giải nén.

### 6.1. Build bằng Gradle Wrapper

**devorbit-mobile:**

```powershell
cd devorbit-mobile
.\gradlew.bat compileDebugKotlin
.\gradlew.bat assembleDebug
```

**devorbit-admin:**

```powershell
cd devorbit-admin
.\gradlew.bat compileDebugKotlin
.\gradlew.bat assembleDebug
```

Giải thích:
- `compileDebugKotlin` — kiểm tra code có lỗi cú pháp / kiểu không (~2-13 giây).
- `assembleDebug` — build full file APK debug (~5 phút, lần đầu có thể lâu hơn do tải dependencies).

> Nếu dùng **Git Bash**, thêm `--no-daemon` để tránh lỗi stale daemon:
> ```bash
> cmd.exe //c "gradlew.bat compileDebugKotlin --no-daemon"
> ```

### 6.2. Chạy trên Android Studio

1. Mở Android Studio → **File → Open**.
2. Chọn thư mục `devorbit-mobile` (hoặc `devorbit-admin`).
3. Đợi Gradle sync hoàn tất (Android Studio tự động chạy).
4. Chọn device (emulator hoặc máy thật) → nhấn **Run**.

---

## 7. Xử lý lỗi thường gặp

| Lỗi | Nguyên nhân | Cách fix |
|-----|-------------|----------|
| `JAVA_HOME is set to an invalid directory` | JDK không đúng | Kiểm tra JAVA_HOME trỏ tới JDK 17 hoặc 21 |
| `Unsupported class file major version 67` | JDK 25 không được hỗ trợ | Cài JDK 17 hoặc 21 và set JAVA_HOME |
| `SDK location not found` | local.properties cũ hoặc thiếu | Xóa local.properties cũ (mục 2.1) rồi mở Android Studio để nó tự tạo lại |
| `Could not find compileSdk 35` | Chưa cài SDK 35 | Mở SDK Manager → cài Android 15.0 (API 35) |
| `Daemon is busy` | Stale Gradle daemon | Thêm `--no-daemon` hoặc chạy `.\gradlew.bat --stop` |
| `Connect to 10.0.2.2:8080 refused` | Backend chưa chạy hoặc sai URL | Chạy backend theo mục 4 |
| H2 database error / bảng không tồn tại | Database chưa có schema | Import file `supabase_complete_schema.sql` vào database |
| `kapt: language version 2.0+ ... falling back to 1.9` | Cảnh báo vô hại | Có thể bỏ qua |

---

## 8. Tóm tắt luồng chạy

```
Giải nén zip
  → Xóa local.properties cũ
  → Kiểm tra .env (database, JWT secret...)
  → Import schema SQL vào database
  → Chạy backend (.\run.bat)
  → Kiểm tra API alive
  → Build Android (gradlew.bat assembleDebug)
  → Run trên Android Studio
```
