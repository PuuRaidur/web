# Eike Development Workflow

This document describes the implementation order for the parts primarily owned by Eike.

Focus areas:
- Authentication
- User system
- Profile system

These parts must be implemented first because most other features depend on them.

---

# Phase 1 – Backend Foundation

Before implementing features, make sure the basic backend structure exists.

Tasks:

1. Verify Spring Boot project runs
2. Connect PostgreSQL database
3. Configure database connection in `application.properties`
4. Create base package structure

Example structure:

com.matchme

- auth
- user
- profile
- common
- config

---

# Phase 2 – User Entity

Create the core `User` entity.

Fields may include:

- id
- email
- password_hash
- created_at

Important rules:

- email must be unique
- password must be stored hashed
- email must never be exposed to other users

Create:

- User entity
- UserRepository
- basic database migration

---

# Phase 3 – Authentication

Implement authentication endpoints.

Endpoints:

POST /auth/register  
POST /auth/login

Register flow:

1. receive email + password
2. hash password using bcrypt
3. create new user
4. store user in database

Login flow:

1. verify email exists
2. compare password with bcrypt
3. generate JWT token
4. return token to client

---

# Phase 4 – JWT Security

Add JWT authentication system.

Tasks:

1. JWT utility/service
2. JWT filter
3. Spring Security configuration
4. protected routes

Goal:

Authenticated requests should include a JWT token.

Example header:

Authorization: Bearer <token>

---

# Phase 5 – /me Endpoint

Implement endpoint that returns authenticated user info.

Endpoint:

GET /me

Returns:

- id
- username or name
- profile image link

Important:

Must use JWT to identify the user.

---

# Phase 6 – Profile System

Create profile-related entities.

Possible fields:

- user_id
- display_name
- about_me
- profile_picture_url
- location

Tasks:

1. Create Profile entity
2. Create ProfileRepository
3. Create ProfileService
4. Create ProfileController

Endpoints:

GET /me/profile  
PUT /me/profile

Users must be able to:

- create profile
- update profile
- view profile

---

# Phase 7 – Bio Data (Recommendation Data)

Create bio data used for recommendations.

Example fields:

- hobbies
- music_preferences
- food_preferences
- interests
- looking_for

Tasks:

1. create Bio entity
2. link Bio to user
3. create endpoints

Endpoints:

GET /me/bio  
PUT /me/bio

Important requirement:

At least **5 data points** must exist for recommendation algorithm.

---

# Phase 8 – Profile Completion Logic

Users must complete their profile before accessing recommendations.

Logic:

User can access recommendations only if:

- profile exists
- bio exists
- required fields are filled

Implement validation inside backend service.

---

# Phase 9 – Profile Picture Upload

Implement profile image support.

Tasks:

1. image upload endpoint
2. store image file or image URL
3. update profile image
4. delete profile image

If no image exists:

Frontend should display placeholder:

👤

---

# Phase 10 – Frontend Screens

Implement frontend views for these features.

Required screens:

Register page  
Login page  
Profile creation page  
Profile editing page

Also implement:

- protected routes
- logout functionality