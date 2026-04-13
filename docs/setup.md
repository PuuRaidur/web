# Teammate Setup (Match Me)

Selle juhise eesmark on saada sama lokaalne setup nagu minu branchis.

## Eeldused
- Java 21 (JDK)
- PostgreSQL (lokaalne instance)

## 1) Repo ja branch
```bash
git fetch
git checkout <branch-nimi>
```

## 2) Andmebaas ja salajased muutujad
`application.properties` kasutab tundlike väärtuste jaoks environment variable'eid.

Enne backendi käivitamist sea need:

```bash
export DB_USERNAME=<sinu_db_kasutaja>
export DB_PASSWORD=<sinu_db_parool>
export JWT_SECRET=<vahemalt_32_baiti_pikk_salajane_voti>
```

DB URL on vaikimisi `jdbc:postgresql://localhost:5432/matchme`.
Loo vajadusel andmebaas:

```sql
CREATE DATABASE matchme;
```

`JWT_SECRET` genereerimiseks sobib näiteks:

```bash
openssl rand -base64 64
```

## 3) Käivita backend
```bash
cd server
./mvnw spring-boot:run
```

Server stardib vaikimisi pordil `8080`.

## 4) Flyway migratsioonid
Flyway rakendab käivitamisel migratsioonid kaustast:
`/Users/eikelangerbaur/Code/Code/match-me web/server/src/main/resources/db/migration`

Hetkel on olemas `V1__create_users.sql`, mis loob `users` tabeli.

Kui DB-s on juba konfliktne skeem, siis:
- kas kustuta DB ja loo uuesti, või
- joonda skeem käsitsi ning käivita uuesti.

