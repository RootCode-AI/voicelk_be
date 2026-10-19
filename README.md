# VoiceLK Backend API

VoiceLK is a robust Text-to-Speech (TTS) platform backend built with **Spring Boot**, **Supabase (PostgreSQL & Storage)**, and **Google Gemini AI**. It provides a comprehensive REST API to handle user queries, generate AI-powered responses, store and stream generated audio files, and manage user accounts (both guest and registered).

## 🚀 Tech Stack & Architecture

- **Framework**: Spring Boot 3 / Java
- **Database**: Supabase PostgreSQL (via Spring Data JPA)
- **File Storage**: Supabase Storage REST API
- **AI Integration**: Google Gemini API (`gemini-3.5-flash`)
- **Authentication**: Spring Security with JWT (JSON Web Tokens)
- **Push Notifications / Auth Sync**: Firebase Admin SDK (Optional integration)

### Core Architecture
1. **Controller Layer**: Exposes RESTful endpoints. Security is managed via `WebSecurityConfig`.
2. **Service Layer**: Contains core business logic (e.g., `QueryAnswerServiceImpl` coordinates Gemini API calls, `SupabaseStorageServiceImpl` handles multipart file uploads to cloud buckets).
3. **Repository Layer**: Extends `JpaRepository` for seamless PostgreSQL interaction.
4. **Security Layer**: Stateless JWT authentication filter. Specific endpoints (like `/api/ask` and `/api/guest`) are exposed publicly, while others require a valid `Bearer` token.

---

## 🛠️ Environment Setup

To run this project locally, copy [`.env.example`](.env.example) to `.env` and fill it in (or inject the same variables via your IDE). `JWT_SECRET` is required and must be at least 32 characters.

For Firebase, either place `firebase-service-account.json` in `src/main/resources/` (it is git-ignored) or set `FIREBASE_CREDENTIALS_JSON`.

---

## ☁️ Deploying (Azure, `api.voicelk.dev`)

The [`Dockerfile`](Dockerfile) builds a self-contained image; Azure Container Apps (or App Service for Containers) runs it. Nothing secret is in the image — set every variable from `.env.example` as an app setting / secret, with `FIREBASE_CREDENTIALS_JSON` holding the service account JSON.

1. Build and push the image, create the app with ingress target port `8082` (or set `PORT`).
2. Point `TTS_BRIDGE_URL` at the bridge on the VPS over HTTPS and set `TTS_BRIDGE_API_KEY`.
3. Custom domain: add `CNAME api → <app>.<region>.azurecontainerapps.io` and `TXT asuid.api → <verification id>`, then bind `api.voicelk.dev` with a free managed certificate. `.dev` is HTTPS-only in browsers.
4. CORS allows `https://chat.voicelk.dev` and the local Vite server by default; override with `CORS_ALLOWED_ORIGINS`.
5. The client address comes from the proxy's `X-Forwarded-For`, trusted only from internal proxy addresses. After deploying, check that new guest records carry real public addresses; if they all show one internal address, set `SERVER_TOMCAT_REMOTEIP_INTERNALPROXIES` to a regex matching the ingress.

---

## 📡 API Endpoints Detailed

### 1. Authentication (`/auth`)
*Endpoints to handle JWT token generation and user registration.*
- `POST /auth/login` - Authenticate a user and return a JWT.
- `POST /auth/register` - Register a new user.

### 2. Guest User Management (`/api/guest`) - **🔓 Public**
*For users interacting without creating a persistent account.*
- `POST /api/guest` - Create a new guest user (captures IP address for tracking).
- `GET /api/guest/{userId}` - Get guest details by ID.
- `GET /api/guest/session/{sessionId}` - Fetch guest details using their browser session ID.

### 3. Ask / Query & Answer (`/api/ask`) - **🔓 Public**
*The core feature flow for submitting questions to the AI.*
- `POST /api/ask` - Submit a text query. The backend calls the Gemini API, saves the `Query` and `Answer` entities, and returns the AI-generated response.
- `GET /api/ask/{queryId}` - Retrieve a specific query and its corresponding answer.
- `GET /api/ask/history/{userId}` - Retrieve the Q&A history for a specific user.

### 4. Audio Management (`/api/audios`) - **🔒 Protected**
*Handles the upload and retrieval of generated TTS audio files.*
- `POST /api/audios/upload` - **(Multipart/form-data)** Uploads an `.mp3` or `.wav` file directly to the Supabase `audios` bucket and links it to an `answerId` in the database.
- `GET /api/audios/{audioId}` - Retrieve metadata about a specific audio file.
- `GET /api/audios/{audioId}/stream` - Directly streams or redirects to the Supabase URL for browser playback.
- `DELETE /api/audios/{audioId}` - Deletes an audio file.

### 5. Queries (`/api/queries`) - **🔒 Protected**
*Direct access to the `Query` table for admin or historical purposes.*
- `POST /api/queries` - Manually create a query record.
- `GET /api/queries/user/{userId}` - Get all queries by a specific user.
- `GET /api/queries/topic/{syllabusTopic}` - Filter queries by a specific educational topic.

### 6. User Management (`/api/users` & `/api/reg`) - **🔒 Protected**
- `POST /api/reg` - Admin/Internal endpoint to create registered users.
- `GET /api/reg/email/{email}` - Check if a user exists by email.
- `GET /api/users/{userId}` - Retrieve generic user data.

### 7. Feedback & Logs (`/api/feedbacks` & `/api/download-logs`) - **🔒 Protected**
- `POST /api/feedbacks` - Submit a rating/review for an audio response.
- `GET /api/feedbacks/audio/{audioId}` - Get feedback for a specific audio file.
- `POST /api/download-logs` - Log when a user downloads an audio file (for analytics).
- `GET /api/download-logs/user/{userId}` - View download history for a user.

---

## 🗄️ Database Entity Relationships

- **User (Parent)** -> One-to-Many -> **Query**
- **Query** -> One-to-One -> **Answer** *(Linked via `query_id`)*
- **Answer** -> One-to-One -> **Audio** *(Linked via `answer_id`)*
- **Audio** -> One-to-Many -> **DownloadLog**
- **Audio** -> One-to-One -> **UserFeedback**

*Note: Infinite recursion issues during JSON serialization are prevented using Jackson `@JsonIgnore` annotations on backward references (e.g., inside the `Query` entity).*

---

## 🏃‍♂️ How to Run

1. Clone the repository.
2. Setup your `.env` file based on the template above.
3. Build the project:
   ```bash
   mvn clean install
   ```
4. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```
5. The API will be available at `http://localhost:8082`.
