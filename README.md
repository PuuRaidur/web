# Match-Me

Match-Me is a full-stack recommendation platform that connects people based on their profile and bio data. Users register, complete their profile, browse matches, send connection requests, and chat in real time once connected.

## 🏗️ Project Overview

- **Backend**: Spring Boot (Java 21), PostgreSQL, Flyway, JWT auth
- **Frontend**: React + TypeScript (Vite)
- **Realtime**: WebSocket (Socket.IO)

## ⚙️ Requirements

- Java 21+
- Maven
- Node.js 18+
- PostgreSQL 16+

## 🚀 Setup & Installation

### 1. Configure the database

Ensure PostgreSQL is running and you can connect to a database named `matchme`.

**Database Setup:**
```bash
# Connect to PostgreSQL as superuser
psql -U postgres

# Create database
CREATE DATABASE matchme;
\q
```

**Set Environment Variables:**
Before starting the backend, you need to set these environment variables:
```bash
export DB_USERNAME=your_postgres_username
export DB_PASSWORD=your_postgres_password
export JWT_SECRET=at_least_32_byte_long_secret_key_here
```

To generate a secure JWT secret, you can use:
```bash
openssl rand -base64 64
```

### 2. Server (Spring Boot)

```bash
cd server
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

WebSocket endpoint: `http://localhost:8081`

### 3. Client (Vite)

```bash
cd client
npm install
npm run dev
```

The UI will be available at `http://localhost:5173`.

## 📖 Usage Guide

### 1. Create an account

Register with email and password, then log in. JWT is stored in local storage for API access.

### 2. Complete your profile

Before accessing recommendations, you must fill out:

- Profile: display name, about me, location
- Bio: hobbies, music preferences, food preferences, interests, looking for

You can also upload or remove a profile picture.

### 3. Browse recommendations

Recommendations are ranked by a matching algorithm. You can:

- **Connect** to send a request
- **Dismiss** to remove that user from future recommendations

### 4. Manage requests

- **Pending**: outgoing requests you can cancel
- **Requests**: incoming requests you can accept or dismiss

### 5. Chat in real time

Connected users can chat with:

- Real-time message delivery
- Unread indicators
- Typing indicator
- Online/offline status

## 📡 API Endpoints

### Authentication
- `POST /auth/register` - Register a new user
- `POST /auth/login` - Authenticate user

### User Information
- `GET /me` - Get current user details
- `GET /me/profile` - Get current user's profile
- `PUT /me/profile` - Update current user's profile
- `POST /me/profile/picture` - Upload profile picture
- `DELETE /me/profile/picture` - Remove profile picture
- `GET /me/bio` - Get current user's bio
- `PUT /me/bio` - Update current user's bio

### User Lookup
- `GET /users/{id}` - Get user summary
- `GET /users/{id}/profile` - Get user's profile
- `GET /users/{id}/bio` - Get user's bio

### Recommendations
- `GET /recommendations` - Get user recommendations
- `POST /recommendations/dismiss/{userId}` - Dismiss a recommendation

### Connections
- `GET /connections` - Get user's connections
- `POST /connections/request` - Send connection request
- `GET /connections/requests` - Get incoming connection requests
- `GET /connections/requests/outgoing` - Get outgoing connection requests
- `POST /connections/accept` - Accept connection request
- `POST /connections/dismiss` - Dismiss connection request
- `POST /connections/cancel` - Cancel outgoing connection request

### Chat
- `POST /chats/with?otherUserId=ID` - Get or create chat with user
- `GET /chats` - Get user's chat conversations
- `GET /chats/{chatId}/messages?page=0&size=20` - Get chat messages
- `POST /chats/{chatId}/messages` - Send a message
- `POST /chats/{chatId}/read` - Mark messages as read

## 🌱 Seeding & Reset

Seed scripts live in `scripts/` and use PostgreSQL.

### Reset the database (drop + recreate)

```bash
./scripts/reset_db.sh
```

### Run migrations (Flyway)

```bash
cd server
mvn spring-boot:run
```

Wait for `Started ServerApplication`, then stop with `Ctrl+C`.

### Seed 100 users

```bash
./scripts/seed.sh
```

All seeded users share the password:

```
password123
```

Example accounts:

```
user1@example.com
user2@example.com
...
user100@example.com
```

## 🧪 Testing Real-time Features

There's a helper script to test real-time chat functionality:

```bash
./scripts/chat_test.sh
```

Follow the prompts to test WebSocket connectivity and message sending.

## 🔧 Development Notes

### Environment Variables

Both the frontend and backend support environment variables:

**Frontend (.env file)**:
- `VITE_API_URL` - API base URL (default: http://localhost:8080)
- `VITE_SOCKET_URL` - WebSocket URL (default: http://localhost:8081)

**Backend (application.properties)**:
- `spring.datasource.url` - Database connection URL
- `spring.datasource.username` - Database username
- `spring.datasource.password` - Database password
- `jwt.secret` - JWT signing secret

### Key Database Entities

1. **Users**: Core authentication entity with email/password
2. **Profiles**: Extended profile information including location
3. **Bio**: Personal interests and preferences for matching
4. **Connection Requests**: Pending relationship requests
5. **Connections**: Established relationships between users
6. **Chats**: Conversation threads between connected users
7. **Messages**: Individual chat messages
8. **Chat Reads**: Message read status tracking

### Architecture Patterns

- **JWT Authentication**: Stateless authentication with token refresh
- **RESTful API Design**: Consistent resource-oriented endpoints
- **Real-time Updates**: WebSocket for instant messaging and presence
- **Database Migrations**: Version-controlled schema changes with Flyway
- **Client-side Routing**: SPA navigation with React Router

## ✨ Extra Features

- Real-time chat (Socket.IO)
- Online/offline status indicator
- Typing indicator
- Profile gating before recommendations
- Distance-based matching preferences
- Form validation and error handling
- Responsive UI design

## 🛠️ Troubleshooting

**Issue**: Connection refused when starting server
**Solution**: Ensure PostgreSQL is running and database credentials are correct

**Issue**: "Profile incomplete" error when accessing recommendations
**Solution**: Complete all required profile fields in the Profile Setup section

**Issue**: WebSocket connection failing
**Solution**: Verify Socket.IO server is running on port 8081 and check CORS settings

## Afterword

I give big thanks to my teammate Eike Langerbaur who helped us a lot in the development of this project.
She is a really structured, nice and efficient teammate to work with.
Without her, the project would have been much more difficult for me.