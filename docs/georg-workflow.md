# Georg Development Workflow

This document describes the implementation order for the parts primarily owned by Georg.

Focus areas:
- Recommendations
- Connection requests
- Connected users

These features depend on the user and profile system.

---

# Phase 1 – Recommendation Data Access

Before implementing recommendation logic, backend must be able to read:

- user profiles
- bio data
- user location

Tasks:

1. create services to load user bio data
2. create services to load profile information
3. filter out current user

---

# Phase 2 – Recommendation Algorithm

Implement recommendation scoring system.

Rules:

- use at least **5 bio data points**
- assign score based on similarity
- stronger matches appear first

Example scoring idea:

shared hobbies → +2  
same music taste → +2  
similar interests → +2  
same location → +3

Total score determines ranking.

---

# Phase 3 – Location Filtering

Recommendations must not include users who are too far away.

Simplest implementation:

- users must be in the same city

Tasks:

1. store location in profile
2. filter candidates by location

---

# Phase 4 – Dismissed Recommendations

If a user dismisses a recommendation, it must not appear again.

Create table:

dismissed_recommendations

Fields:

- user_id
- dismissed_user_id

Recommendation query must exclude these users.

---

# Phase 5 – Recommendation Endpoint

Implement endpoint:

GET /recommendations

Rules:

- return **maximum 10 users**
- return **only user IDs**
- sort by match score

Frontend will fetch additional data separately.

---

# Phase 6 – Connection Requests

Users must be able to send connection requests.

Create table:

connection_requests

Fields:

- sender_id
- receiver_id
- created_at

Endpoints:

POST /connections/request

---

# Phase 7 – View Requests

Users must be able to see incoming requests.

Endpoint:

GET /connections/requests

Return:

list of user IDs.

---

# Phase 8 – Accept or Dismiss Request

Endpoints:

POST /connections/accept  
POST /connections/dismiss

Accepting a request creates a connection.

---

# Phase 9 – Connected Users

Create table:

connections

Fields:

- user1_id
- user2_id

Endpoint:

GET /connections

Returns:

list of connected user IDs.

---

# Phase 10 – Disconnect

Users must be able to disconnect.

Endpoint:

DELETE /connections/{id}

---

# Phase 11 – Frontend Screens

Implement UI for these features.

Screens:

Recommendations page  
Connection requests page  
Connected users page

Actions:

- connect
- dismiss
- accept request
- disconnect

Recommendation UI may be implemented as:

- list view
  or
- swipe-style cards