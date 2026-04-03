#!/usr/bin/env bash
set -euo pipefail

BASE_URL="http://localhost:8080"
EMAIL1="test1@example.com"
PASS1="password123"
EMAIL2="user2@example.com"
PASS2="password123"

# Ensure server is up
if ! curl -s "$BASE_URL" >/dev/null 2>&1; then
  echo "Server not reachable at $BASE_URL"
  exit 1
fi

# Login user1
TOKEN1=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL1\",\"password\":\"$PASS1\"}" | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')

# If user2 doesn't exist, register; then login
TOKEN2=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL2\",\"password\":\"$PASS2\"}" | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')

if [[ -z "$TOKEN2" ]]; then
  TOKEN2=$(curl -s -X POST "$BASE_URL/auth/register" \
    -H "Content-Type: application/json" \
    -d "{\"email\":\"$EMAIL2\",\"password\":\"$PASS2\"}" | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')
fi

if [[ -z "$TOKEN1" || -z "$TOKEN2" ]]; then
  echo "Failed to obtain tokens"
  exit 1
fi

# Create or get chat between user1 and user2 (assumes user2 id = 3 if newly created)
# NOTE: If user2 has a different id, set OTHER_USER_ID accordingly.
OTHER_USER_ID=3

CHAT_JSON=$(curl -s -X POST "$BASE_URL/chats/with?otherUserId=$OTHER_USER_ID" \
  -H "Authorization: Bearer $TOKEN1")

CHAT_ID=$(echo "$CHAT_JSON" | sed -n 's/.*"chatId":\([0-9]*\).*/\1/p')

if [[ -z "$CHAT_ID" ]]; then
  echo "Failed to create or find chat"
  echo "$CHAT_JSON"
  exit 1
fi

# Send message as user1
curl -s -X POST "$BASE_URL/chats/$CHAT_ID/messages" \
  -H "Authorization: Bearer $TOKEN1" \
  -H "Content-Type: application/json" \
  -d '{"content":"Hello from user1!"}' >/dev/null

# Send message as user2
curl -s -X POST "$BASE_URL/chats/$CHAT_ID/messages" \
  -H "Authorization: Bearer $TOKEN2" \
  -H "Content-Type: application/json" \
  -d '{"content":"Hi user1, got it!"}' >/dev/null

# Fetch history
HISTORY=$(curl -s -X GET "$BASE_URL/chats/$CHAT_ID/messages?page=0&size=20" \
  -H "Authorization: Bearer $TOKEN1")

echo "Chat ID: $CHAT_ID"
echo "History: $HISTORY"
