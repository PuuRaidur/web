## Team Work Split

This project is developed by two people, and both contributors will work on both backend and frontend parts of the application.  
Instead of splitting the work strictly into "frontend" and "backend", we split it by **features**. This allows both team members to participate in the full-stack development process.

### Branch Strategy

We are using separate development branches for each team member:

- `eike-dev`
- `georg-dev`

The main integration flow is:

- Each developer works on their own branch
- Changes are pushed regularly
- Features are merged through pull requests / review before going into the shared main branch

### General Working Agreement

Before implementing features, both team members agree together on:

- database structure
- API endpoint contracts
- authentication flow
- shared naming conventions
- folder/package structure
- matching logic rules
- chat event flow

This helps avoid conflicts during integration and ensures both frontend and backend connect correctly.

---

## Feature Ownership

### Eike
Primary responsibility:

- Authentication
    - register
    - login
    - logout
    - JWT authentication flow
    - protected routes on backend

- User Profile
    - `/me`
    - `/me/profile`
    - `/me/bio`
    - profile editing
    - profile completion rules
    - profile image upload / update / delete
    - placeholder image fallback

Frontend responsibilities in this area:

- login/register views
- profile creation/editing pages
- guarded navigation for incomplete profiles

Backend responsibilities in this area:

- auth endpoints
- password hashing with bcrypt
- JWT generation and validation
- user/profile/bio endpoints
- profile validation logic

---

### Georg
Primary responsibility:

- Recommendations
    - recommendation algorithm
    - recommendation scoring
    - maximum 10 recommendations
    - dismissed recommendations
    - location-based filtering

- Connections
    - send connection request
    - list incoming requests
    - accept request
    - dismiss request
    - list connected users
    - disconnect functionality

Frontend responsibilities in this area:

- recommendations page
- recommendation cards/list
- connect / dismiss actions
- connection requests view
- connected users view

Backend responsibilities in this area:

- recommendation endpoint
- matching logic
- request/connection endpoints
- filtering rules
- permission checks

---

## Shared Responsibility

### Chat and Realtime Features
This part of the project will be implemented together, because it is the most complex and integration-heavy feature.

Shared feature scope:

- chat creation/resume between connected users
- one chat history per connected pair
- message sending
- paginated message history
- unread message indicator
- realtime updates with WebSocket
- chat ordering by most recent message
- timestamps for messages

Suggested practical split inside this shared part:

**Eike**
- chat/message backend structure
- entities and repositories
- REST endpoints for message history
- pagination logic

**Georg**
- chat UI
- chat list
- unread indicators
- realtime message rendering in frontend

Both work together on:

- WebSocket integration
- event design
- testing realtime behavior
- final chat flow validation

---

## Development Plan

### Phase 1 – Project Setup
Done together:

- initialize backend project
- initialize frontend project
- connect PostgreSQL
- define database schema
- define package/folder structure
- define API contracts

### Phase 2 – Authentication and Profile
Primary owner: **Eike**  
Reviewer/support: **Georg**

### Phase 3 – Recommendations and Connections
Primary owner: **Georg**  
Reviewer/support: **Eike**

### Phase 4 – Chat and Realtime
Implemented together

### Phase 5 – Seed Data, Testing, Polish
Done together:

- create seed script for fake users
- load at least 100 test users
- test recommendation quality
- test permissions and security
- responsive UI improvements
- README finalization

---

## Code Review / Collaboration Rules

- Each member develops primarily on their own branch
- Large structural decisions are discussed together before implementation
- Each major feature should be reviewed by the other team member
- No direct work is done on `main`
- Integration happens only after testing and review

---

## Goal of This Work Split

The goal of this split is:

- both developers learn both backend and frontend
- work is divided clearly
- integration problems are reduced
- complex features such as chat are built collaboratively
- the project remains understandable for both team members