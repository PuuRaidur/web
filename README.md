# Match-Me

Match-Me is a full-stack recommendation platform that connects people based on their profile and bio data. Users register, complete their profile, browse matches, send connection requests, and chat in real time once connected.

## Project Overview

- **Backend**: Spring Boot (Java 21), PostgreSQL, Flyway, JWT auth
- **Frontend**: React + TypeScript (Vite)
- **Realtime**: WebSocket (STOMP + SockJS)

## Requirements

- Java 21+
- Maven
- Node.js 18+
- PostgreSQL 16+

## Setup & Installation

### 1) Configure the database

Ensure PostgreSQL is running and you can connect to a database named `matchme`.

### 2) Server (Spring Boot)

```bash
cd "/Users/eikelangerbaur/Code/Code/match-me web/server"
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.

### 3) Client (Vite)

```bash
cd "/Users/eikelangerbaur/Code/Code/match-me web/client"
npm install
npm run dev
```

The UI will be available at `http://localhost:5173`.

## Usage Guide

### 1) Create an account

Register with email and password, then log in. JWT is stored in local storage for API access.

### 2) Complete your profile

Before accessing recommendations, you must fill out:

- Profile: display name, about me, location
- Bio: hobbies, music preferences, food preferences, interests, looking for

You can also upload or remove a profile picture.

### 3) Browse recommendations

Recommendations are ranked by a matching algorithm. You can:

- **Connect** to send a request
- **Dismiss** to remove that user from future recommendations

### 4) Manage requests

- **Pending**: outgoing requests you can cancel
- **Requests**: incoming requests you can accept or dismiss

### 5) Chat in real time

Connected users can chat with:

- real-time message delivery
- unread indicators
- typing indicator
- online/offline status

## API Endpoints (Core)

- `POST /auth/register`
- `POST /auth/login`
- `GET /me`
- `GET /me/profile`
- `PUT /me/profile`
- `POST /me/profile/picture`
- `DELETE /me/profile/picture`
- `GET /me/bio`
- `PUT /me/bio`
- `GET /users/{id}`
- `GET /users/{id}/profile`
- `GET /users/{id}/bio`
- `GET /recommendations`
- `POST /recommendations/dismiss/{userId}`
- `GET /connections`
- `POST /connections/request`
- `GET /connections/requests`
- `GET /connections/requests/outgoing`
- `POST /connections/accept`
- `POST /connections/dismiss`
- `POST /connections/cancel`
- `POST /chats/with?otherUserId=ID`
- `GET /chats`
- `GET /chats/{chatId}/messages?page=0&size=20`
- `POST /chats/{chatId}/messages`
- `POST /chats/{chatId}/read`

## Seeding & Reset

Seed scripts live in `scripts/` and use PostgreSQL.

### Reset the database (drop + recreate)

```bash
/Users/eikelangerbaur/Code/Code/match-me\ web/scripts/reset_db.sh
```

### Run migrations (Flyway)

```bash
cd "/Users/eikelangerbaur/Code/Code/match-me web/server"
mvn spring-boot:run
```

Wait for `Started ServerApplication`, then stop with `Ctrl+C`.

### Seed 100 users

```bash
/Users/eikelangerbaur/Code/Code/match-me\ web/scripts/seed.sh
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

## Notes / Extra Features

- Real-time chat (STOMP + SockJS)
- Online/offline status indicator
- Typing indicator
- Profile gating before recommendations
