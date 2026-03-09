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

## 2) Andmebaas
Default seadistus on `application.properties` failis:
`/Users/eikelangerbaur/Code/Code/match-me web/server/src/main/resources/application.properties`

Seal on:
- URL: `jdbc:postgresql://localhost:5432/matchme`
- kasutaja: `postgres`
- parool: `postgres`

Vali üks variant:

A) Tee DB kasutajaga `postgres` ja parooliga `postgres`
```sql
CREATE USER postgres WITH PASSWORD 'postgres';
CREATE DATABASE matchme OWNER postgres;
```

B) Kasuta oma olemasolevat kasutajat/parooli ja muuda `application.properties` vastavalt.

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

