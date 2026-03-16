## Test register/login to ensure auth flow works

1) Start the server
```bash
cd "/Users/eikelangerbaur/Code/Code/match-me web/server"
mvn spring-boot:run
```
2) Register
   Open a new terminal and run:
```bash
curl -X POST http://localhost:8080/auth/register \
-H "Content-Type: application/json" \
-d '{"email":"test1@example.com","password":"password123"}'
```
Expected: {"token":"<jwt>"}

3) Login
```bash
curl -X POST http://localhost:8080/auth/login \
-H "Content-Type: application/json" \
-d '{"email":"test1@example.com","password":"password123"}'
```
Expected: {"token":"<jwt>"}