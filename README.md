# AI Document Assistant - Backend

Spring Boot backend application for document management with JWT authentication.

## Prerequisites

- **Java 17+** (Microsoft OpenJDK 17.0.20 recommended)
- **Maven 3.9+**
- **PostgreSQL 12+** (running on localhost:5432)
- **Postman** (for API testing, optional)

## Setup Instructions

### 1. Database Setup

Create PostgreSQL database:
```sql
CREATE DATABASE ai_assistant;
CREATE USER postgres WITH PASSWORD 'postgres';
ALTER ROLE postgres WITH SUPERUSER;
```

Or use existing credentials in `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ai_assistant
    username: postgres
    password: postgres
```

### 2. Clone & Install

```bash
# Navigate to project directory
cd C:\Dream\ai\backend

# Install dependencies and build
mvn clean install

# Run application
mvn -DskipTests spring-boot:run
```

**Expected Output:**
```
Tomcat started on port 8080 (http) with context path '/'
Started AiAssistantApplication in X.XXX seconds
```

### 3. Verify Setup

```bash
curl http://localhost:8080/api/health
# Response: "App is running on port 8080"
```

## Application Properties

**Port:** 8080 (configured in `src/main/resources/application.properties`)

**Database:** PostgreSQL on `localhost:5432/ai_assistant`

**JWT Secret:** `mysecretkeymysecretkeymysecretkeymysecretkey12345` (in `JwtService.java`)

---

## API Endpoints

### Base URL: `http://localhost:8080`

---

## 1. Health Check (Public)

**Endpoint:** `GET /api/health`

**Authentication:** None (Public)

**Description:** Check if application is running

**Curl Example:**
```bash
curl http://localhost:8080/api/health
```

**Response (200 OK):**
```
"App is running on port 8080"
```

---

## 2. Authentication APIs

### 2.1 Register User

**Endpoint:** `POST /api/auth/register`

**Authentication:** None (Public)

**Request Body:**
```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Parameters:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| fullName | string | Yes | User's full name (not blank) |
| email | string | Yes | Valid email address |
| password | string | Yes | Password (not blank, min 6 chars) |

**Curl Example:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "fullName": "John Doe",
  "email": "john@example.com",
  "role": "USER"
}
```

---

### 2.2 Login User

**Endpoint:** `POST /api/auth/login`

**Authentication:** None (Public)

**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Parameters:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| email | string | Yes | Registered email address |
| password | string | Yes | User's password |

**Curl Example:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "fullName": "John Doe",
  "email": "john@example.com",
  "role": "USER"
}
```

**Note:** Save the `token` value for authenticated requests

---

## 3. Document APIs

All document endpoints require JWT authentication in header:
```
Authorization: Bearer {token}
```

### 3.1 Upload Document

**Endpoint:** `POST /api/documents/upload`

**Authentication:** Required (Bearer Token)

**Content-Type:** `multipart/form-data`

**Form Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| file | File | Yes | PDF, DOC, DOCX, TXT files |

**Postman Setup:**
- Method: POST
- URL: http://localhost:8080/api/documents/upload
- Headers: Authorization: Bearer {token}
- Body → form-data → Key: "file" (Type: File) → Select file

**Curl Example:**
```bash
curl -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@document.pdf" \
  http://localhost:8080/api/documents/upload
```

**Response (200 OK):**
```json
{
  "id": 1,
  "fileName": "document.pdf",
  "originalFileName": "document.pdf",
  "fileType": "application/pdf",
  "fileSize": 2048,
  "uploadedAt": "2026-08-03T23:10:59.181"
}
```

---

### 3.2 Get All Documents (Paginated)

**Endpoint:** `GET /api/documents`

**Authentication:** Required (Bearer Token)

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| page | integer | 0 | Page number (0-indexed) |
| size | integer | 10 | Documents per page |
| search | string | "" | Search filename (optional) |

**Postman Setup:**
- Method: GET
- URL: http://localhost:8080/api/documents?page=0&size=10&search=
- Headers: Authorization: Bearer {token}

**Curl Example:**
```bash
curl -H "Authorization: Bearer YOUR_TOKEN" \
  "http://localhost:8080/api/documents?page=0&size=10"
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "fileName": "document.pdf",
      "originalFileName": "document.pdf",
      "fileType": "application/pdf",
      "fileSize": 2048,
      "uploadedAt": "2026-08-03T23:10:59.181"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "number": 0,
  "size": 10,
  "empty": false
}
```

---

### 3.3 Get Single Document

**Endpoint:** `GET /api/documents/{id}`

**Authentication:** Required (Bearer Token)

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | integer | Yes | Document ID (from list endpoint) |

**Postman Setup:**
- Method: GET
- URL: http://localhost:8080/api/documents/1
- Headers: Authorization: Bearer {token}

**Curl Example:**
```bash
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8080/api/documents/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "fileName": "document.pdf",
  "originalFileName": "document.pdf",
  "fileType": "application/pdf",
  "fileSize": 2048,
  "uploadedAt": "2026-08-03T23:10:59.181"
}
```

---

### 3.4 Download Document

**Endpoint:** `GET /api/documents/download/{id}`

**Authentication:** Required (Bearer Token)

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | integer | Yes | Document ID |

**Postman Setup:**
- Method: GET
- URL: http://localhost:8080/api/documents/download/1
- Headers: Authorization: Bearer {token}
- **File will auto-download** (Content-Type: application/octet-stream)

**Curl Example:**
```bash
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8080/api/documents/download/1 \
  -o downloaded_file.pdf
```

**Response (200 OK):**
- File downloads with original filename
- Header: `Content-Disposition: attachment; filename="document.pdf"`

---

### 3.5 Delete Document

**Endpoint:** `DELETE /api/documents/{id}`

**Authentication:** Required (Bearer Token)

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | integer | Yes | Document ID |

**Postman Setup:**
- Method: DELETE
- URL: http://localhost:8080/api/documents/1
- Headers: Authorization: Bearer {token}

**Curl Example:**
```bash
curl -X DELETE \
  -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8080/api/documents/1
```

**Response (200 OK):**
```json
"Document deleted successfully"
```

---

## Quick Start Example

### 1. Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Test User","email":"test@test.com","password":"pass123"}'
```
**Save the `token` from response**

### 2. Upload Document
```bash
TOKEN="your_token_here"
curl -H "Authorization: Bearer $TOKEN" \
  -F "file=@myfile.pdf" \
  http://localhost:8080/api/documents/upload
```
**Save the `id` from response (e.g., 1)**

### 3. List Documents
```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/documents
```

### 4. Download Document
```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/documents/download/1 \
  -o downloaded_file.pdf
```

### 5. Delete Document
```bash
curl -X DELETE \
  -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/documents/1
```

---

## Project Structure

```
src/main/java/com/virendra/aiassistant/
├── auth/
│   ├── controller/    # AuthController (register, login)
│   ├── service/       # AuthenticationService
│   ├── entity/        # User, Role
│   ├── dto/          # RegisterRequest, LoginRequest, AuthResponse
│   └── repository/    # UserRepository
├── document/
│   ├── controller/    # DocumentController (upload, list, download, delete)
│   ├── service/       # DocumentService
│   ├── entity/        # Document
│   ├── dto/          # DocumentResponse
│   ├── repository/    # DocumentRepository
│   └── util/         # FileStorageUtil, FileValidator
├── security/
│   ├── JwtAuthenticationFilter   # JWT token validation
│   ├── JwtService               # Token generation & parsing
│   ├── SecurityConfig           # Spring Security configuration
│   ├── CustomUserDetails        # User details implementation
│   └── CustomUserDetailsService # Load user by email
├── health/
│   └── controller/    # HealthController (health check)
└── AiAssistantApplication.java  # Main entry point
```

---

## Error Responses

### 400 Bad Request
```json
{
  "fullName": "must not be blank",
  "email": "must be a valid email address"
}
```

### 401 Unauthorized
```
No response body (empty)
Status: 401
```
**Cause:** Missing or invalid JWT token

### 403 Forbidden
```
No response body (empty)
Status: 403
```
**Cause:** Invalid JWT token or token expired

### 404 Not Found
```json
{
  "message": "Document not found"
}
```

---

## Troubleshooting

### Port 8080 already in use
```bash
# Find process on port 8080
netstat -ano | findstr :8080

# Kill process (Windows)
taskkill /PID <PID> /F
```

### Database connection error
```
Check PostgreSQL is running:
- URL: jdbc:postgresql://localhost:5432/ai_assistant
- Username: postgres
- Password: postgres
```

### JWT token expired
- Tokens expire in 24 hours
- Login again to get new token
- Add `Authorization: Bearer {new_token}` to request

### File upload fails
- Ensure file format is supported (PDF, DOC, DOCX, TXT)
- Use `multipart/form-data` (NOT JSON)
- Check file size limits

---

## Development

### Build
```bash
mvn clean install
```

### Run Tests
```bash
mvn test
```

### Run with Debug
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--debug"
```

---

## Technologies Used

- **Spring Boot 4.1.0** - Web framework
- **Spring Security** - Authentication & authorization
- **JWT (JJWT)** - Token-based auth
- **JPA/Hibernate** - Database ORM
- **PostgreSQL** - Database
- **Lombok** - Code generation
- **Maven** - Build tool

---

## Default Credentials (Development Only)

- **Email:** (Any registered email)
- **Password:** (User's password)
- **JWT Secret:** `mysecretkeymysecretkeymysecretkeymysecretkey12345` (dev default)

---

## Security: JWT Secret Management

### Development (Default)

Uses hardcoded secret in `application.properties`:
```properties
app.jwt.secret=mysecretkeymysecretkeymysecretkeymysecretkey12345
```

No environment variable needed.

```bash
mvn -DskipTests spring-boot:run
```

### Production (Required)

**MUST set `JWT_SECRET` environment variable before running!**

**Step 1: Generate Strong Secret (32+ characters)**

Windows PowerShell:
```powershell
$secret = -join ((65..90) + (97..122) + (48..57) | Get-Random -Count 50 | ForEach-Object {[char]$_})
Write-Output $secret
```

Linux/Mac:
```bash
openssl rand -base64 32
```

**Step 2: Set Environment Variable**

Windows (Command Prompt):
```cmd
set JWT_SECRET=your-generated-secret-key-here
```

Windows (PowerShell):
```powershell
$env:JWT_SECRET='your-generated-secret-key-here'
```

Linux/Mac:
```bash
export JWT_SECRET='your-generated-secret-key-here'
```

**Step 3: Run with Production Profile**

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

**If JWT_SECRET is not set → Application fails to start with error:**
```
IllegalArgumentException: JWT_SECRET environment variable is not set.
```

### CI/CD Pipeline

**GitHub Actions Example:**
```yaml
- name: Run Application
  env:
    JWT_SECRET: ${{ secrets.JWT_SECRET }}
  run: mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

**GitLab CI Example:**
```yaml
run:
  variables:
    JWT_SECRET: $JWT_SECRET  # Set in GitLab Secrets
  script:
    - mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

### Security Checklist

- ✅ Secret NOT hardcoded in `application-prod.yml`
- ✅ Secret MUST be set via `JWT_SECRET` env var
- ✅ App fails if secret missing (fail-safe)
- ✅ Secret minimum 32 characters enforced
- ✅ `.gitignore` prevents accidental commits
- ✅ Secrets stored in CI/CD, not in repo
- ✅ Secret rotatable via env var change

⚠️ **Change JWT secret in production!** Default is for development only.

---

## License

© 2026 AI Document Assistant. All rights reserved.

---

## Support

For issues or questions, check:
1. PostgreSQL is running
2. Port 8080 is available
3. JWT token is valid
4. File format is supported
