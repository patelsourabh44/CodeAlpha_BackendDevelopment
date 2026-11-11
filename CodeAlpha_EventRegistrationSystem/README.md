# CodeAlpha_EventRegistrationSystem

Backend Development Internship - Task 2. Java 17 + Spring Boot 3 + Spring Data JPA + Spring Security (HTTP Basic, BCrypt) + H2 file DB.

## Run
```
mvn spring-boot:run
```
Open http://localhost:8082. Demo organizer: `admin@event.com` / `admin123` (three sample events are seeded).

## Roles
- **ATTENDEE**: browse events, register, view and cancel own registrations.
- **ORGANIZER**: everything above, plus create / edit / delete own events and see attendees.

## API
| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | public | `{name,email,password,role}` |
| GET | `/api/auth/me` | user | Current user (also used to check login) |
| GET | `/api/events?q=` | public | List / search events |
| GET | `/api/events/{id}` | public | Event details |
| POST | `/api/events` | organizer | Create event |
| PUT | `/api/events/{id}` | organizer (owner) | Update event |
| DELETE | `/api/events/{id}` | organizer (owner) | Delete event and its registrations |
| GET | `/api/events/{id}/registrations` | organizer (owner) | Attendee list |
| POST | `/api/events/{id}/register` | user | Register for an event |
| GET | `/api/registrations/my` | user | My registrations |
| DELETE | `/api/registrations/{id}` | user (owner) | Cancel registration |

Authenticate with HTTP Basic, e.g. `curl -u admin@event.com:admin123 localhost:8082/api/auth/me`.

Event body example: `{"title":"Demo","description":"...","location":"Bhopal","eventDate":"2027-01-15T10:00:00","capacity":50}`

## Design notes
- Capacity is enforced with a pessimistic lock on the event row, so the last seat cannot be double-booked.
- A unique constraint on (user, event) prevents duplicate registrations.
- For the demo, sign-up lets users pick the ORGANIZER role; in production, restrict that.
