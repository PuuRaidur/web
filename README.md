## Match-Me

Full-stack recommendation platform that connects users based on profiles and bio data.

## Database Seeding

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
