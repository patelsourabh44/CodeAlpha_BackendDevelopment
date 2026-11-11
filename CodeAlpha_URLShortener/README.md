# CodeAlpha_URLShortener

Backend Development Internship - Task 1. Java 17 + Spring Boot 3 + Spring Data JPA + H2 (file DB, no setup needed).

## Run
```
mvn spring-boot:run
```
Open http://localhost:8081 for the web UI.

## API
| Method | Path | Description |
|---|---|---|
| POST | `/api/shorten` | Body `{"url":"https://...","alias":"optional"}` -> creates a short code (201) |
| GET | `/{code}` | 302 redirect to the original URL (counts the click) |
| GET | `/api/stats/{code}` | Click count and details |
| GET | `/api/urls` | 20 most recent links |
| DELETE | `/api/urls/{code}` | Delete a link |

Example: `curl -X POST localhost:8081/api/shorten -H "Content-Type: application/json" -d '{"url":"https://github.com"}'`

## Tests
`mvn test`

## Design notes
- Codes are 7 random base62 characters (SecureRandom), checked for uniqueness; custom aliases are supported.
- Only http/https URLs are accepted.
- Data persists in `./data` (H2 file database).
