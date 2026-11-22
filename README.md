# StayCation - YOUR COMFY SPACE

A hotel-booking **backend REST API** built with Spring Boot. Hotel managers list hotels and rooms and control availability and pricing; guests search, reserve a room, add guest details, pay through **Stripe Checkout**, and cancel for a refund.

> This repository contains the API only. `frontend.url` in the config points at a separate frontend, which is not part of this repo.

---

## Features

**For guests**
- Sign up / log in with JWT authentication (short-lived access token + refresh token in an HTTP-only cookie)
- Search hotels by city and date range, view hotel details and rooms
- Multi-step booking: reserve → add guests → pay → confirmed
- Stripe Checkout payment, confirmed asynchronously through a Stripe webhook
- Cancel a confirmed booking with an automatic Stripe refund
- Manage a personal list of saved guests and a profile

**For hotel managers (`HOTEL_MANAGER`)**
- Create, update, delete and activate hotels; add and manage rooms
- Per-room, per-date inventory: close dates, apply a surge factor
- View a hotel's bookings and a revenue report (confirmed bookings, total and average revenue)
- Change another user's role

**Platform**
- Dynamic pricing, recalculated hourly by a scheduled job
- Pessimistic locking when reserving inventory
- Uniform JSON response envelope and global exception handling
- OpenAPI / Swagger UI documentation

---

## Tech stack

| Area | Technology |
|---|---|
| Language / runtime | Java 21 |
| Framework | Spring Boot 3.5.4 (Web, Data JPA, Security, OAuth2 client starter) |
| Database | PostgreSQL (H2 is on the classpath but unused) |
| Auth | JWT via jjwt 0.12.6, BCrypt password hashing |
| Payments | Stripe Java SDK 30.0.0 |
| Mapping | ModelMapper 3.2.2 |
| API docs | springdoc-openapi (Swagger UI) |
| Other | Lombok, Jackson Hibernate6 module |
| Build | Maven (wrapper included) |

---

## Project structure

```
src/main/java/com/staycation/Staycation/
├── StaycationApplication.java     # entry point, @EnableScheduling
├── controller/                    # REST controllers (auth, hotels, bookings, admin, webhook, ...)
├── service/                       # business logic (booking, checkout, hotel, room, inventory, pricing job)
├── strategy/                      # dynamic-pricing strategies (decorator chain)
├── repository/                    # Spring Data JPA repositories, incl. locking/bulk-update queries
├── entity/                        # JPA entities + enums (Role, BookingStatus, Gender, PaymentStatus)
├── dto/                           # request/response objects
├── security/                      # JWT filter, JWT service, auth service, security config
├── advice/                        # ApiResponse envelope, global exception + response handlers
├── config/                        # Stripe, ModelMapper, Hibernate/Jackson config
├── exception/                     # ResourceNotFoundException, UnAuthorisedException
└── utils/AppUtils.java            # current-user helper
```

---

## Domain model

| Entity | Purpose |
|---|---|
| `User` | Account (email, BCrypt password, name, gender, DOB) with a set of `Role`s. Implements `UserDetails`. |
| `Hotel` | Owned by a `User`; has city, photos, amenities, embedded contact info, `active` flag. |
| `Room` | Belongs to a hotel: type, base price, total count, capacity, photos, amenities. |
| `Inventory` | One row per **room per date**: `totalCount`, `bookedCount`, `reservedCount`, `surgeFactor`, `price`, `closed`. Unique on (hotel, room, date). |
| `HotelMinPrice` | Cheapest price per hotel per date; refreshed hourly and used by search. |
| `Booking` | A user's booking of a room for a date range, with status, amount, guests and Stripe session id. |
| `Guest` | Person staying; belongs to the user who created it; linked to bookings. |
| `Payment` | Defined but not currently used by any code path. |

**Roles:** `GUEST` (default on sign-up), `HOTEL_MANAGER` (grants access to `/admin/**`), `ADMIN` (defined, not used in any access rule yet).

---

## How it works

### Booking lifecycle

```
RESERVED ──► GUESTS_ADDED ──► PAYMENTS_PENDING ──► CONFIRMED ──► CANCELLED
 (init)       (addGuests)      (payments)        (Stripe webhook)  (cancel + refund)
```

1. **Init** – locks matching inventory rows, increments `reservedCount` for every date in the range, creates the booking as `RESERVED` with the computed price. A booking is considered expired 10 minutes after creation.
2. **Add guests** – attaches guests; status becomes `GUESTS_ADDED`.
3. **Payments** – creates a Stripe Checkout session and returns its URL; status becomes `PAYMENTS_PENDING`.
4. **Webhook** – on `checkout.session.completed`, the booking becomes `CONFIRMED` and inventory moves from *reserved* to *booked*.
5. **Cancel** – only `CONFIRMED` bookings; inventory is released and the Stripe payment is refunded.

`EXPIRED` exists in the `BookingStatus` enum but is not used yet.

### Dynamic pricing

Prices are computed with a chain of decorated strategies, applied in this order:

| Strategy | Effect |
|---|---|
| Base | The room's `basePrice` |
| Surge | × the date's `surgeFactor` (set by the manager) |
| Occupancy | × 1.2 when booked / total rooms is above 80% |
| Urgency | × 1.15 for dates within the next 7 days |
| Holiday | × 1.25 — **currently hard-coded to always apply** (see `CODE_REVIEW.md`) |

A scheduled job (`PricingUpdateService`, cron `0 0 * * * *`, every hour) recalculates inventory prices for the next year and rebuilds `HotelMinPrice`.

### Response format

Every response is wrapped:

```json
{ "timeStamp": "2026-11-22T16:46:00", "data": { }, "error": null }
```

On failure, `data` is `null` and `error` is `{ "status": "...", "message": "...", "subErrors": [] }`.

---

## API reference

Base path: **`/api/v1`** (set by `server.servlet.context-path`). Authenticated endpoints expect `Authorization: Bearer <accessToken>`.

### Auth
| Method | Path | Access | Notes |
|---|---|---|---|
| POST | `/auth/signup` | Public | Body: `email`, `password`, `name` |
| POST | `/auth/login` | Public | Body: `email`, `password`. Returns `accessToken`; sets `refreshToken` cookie |
| POST | `/auth/refresh` | Public | Reads the `refreshToken` cookie, returns a new `accessToken` |

### Browse
| Method | Path | Access | Notes |
|---|---|---|---|
| GET | `/hotels/search` | Public | JSON **body**: `city`, `startDate`, `endDate`, `roomsCount`, `page`, `size` |
| GET | `/hotels/{hotelId}/info` | Public | Hotel details with rooms |

### Bookings and profile
| Method | Path | Access | Notes |
|---|---|---|---|
| POST | `/bookings/init` | User | Body: `hotelId`, `roomId`, `checkInDate`, `checkOutDate`, `roomsCount` |
| POST | `/bookings/{bookingId}/addGuests` | User | Body: array of guests (`name`, `gender`, `age`, `dateOfBirth`) |
| POST | `/bookings/{bookingId}/payments` | User | Returns `{ "sessionUrl": "..." }` |
| POST | `/bookings/{bookingId}/cancel` | User | Cancels and refunds |
| GET | `/bookings/{bookingId}/status` | User | Returns `bookingStatus` |
| GET | `/users/profile` | User | |
| PATCH | `/users/profile` | User | Body: `name`, `dateOfBirth`, `gender` |
| GET | `/users/myBookings` | User | |
| POST / GET | `/guests` | User* | Create / list your saved guests |
| PUT / DELETE | `/guests/{id}` | User* | Update / delete a guest you own |

\* Intended to be authenticated, but not covered by a security rule today — see `CODE_REVIEW.md`.

### Hotel manager (`HOTEL_MANAGER`)
| Method | Path | Notes |
|---|---|---|
| POST / GET | `/admin/hotels` | Create hotel / list your hotels |
| GET / PUT / DELETE | `/admin/hotels/{hotelId}` | |
| PATCH | `/admin/hotels/{hotelId}/activate` | Activates the hotel and creates a year of inventory for its rooms |
| GET | `/admin/hotels/{hotelId}/bookings` | Bookings for a hotel you own |
| GET | `/admin/hotels/{hotelId}/reports?startDate=&endDate=` | Defaults to the last month. Returns `bookingCount`, `totalRevenue`, `avgRevenue` |
| POST / GET | `/admin/hotels/{hotelId}/rooms` | Create room / list rooms |
| GET / PUT / DELETE | `/admin/hotels/{hotelId}/rooms/{roomId}` | |
| GET | `/admin/inventory/rooms/{roomId}` | Per-date inventory |
| PATCH | `/admin/inventory/rooms/{roomId}` | Body: `startDate`, `endDate`, `closed`, `surgeFactor` |
| PATCH | `/admin/users/{userId}/role` | Body: `{ "role": "HOTEL_MANAGER" }` |

### Webhook
| Method | Path | Notes |
|---|---|---|
| POST | `/webhook/payment` | Stripe calls this. Verified with the `Stripe-Signature` header |

Interactive docs: `http://localhost:<port>/api/v1/swagger-ui/index.html`

---

## Getting started

### Prerequisites
- JDK 21
- PostgreSQL
- A Stripe account (test mode is fine) and, optionally, the [Stripe CLI](https://stripe.com/docs/stripe-cli)

### 1. Clone
```bash
git clone https://github.com/SOHAMSABLE/Staycation.git
cd Staycation
```

### 2. Create the database
Create a PostgreSQL database whose name **exactly matches** the one in `spring.datasource.url` (PostgreSQL database names are case-sensitive when quoted; using an all-lowercase name such as `staycation` avoids surprises).

### 3. Configure
Use `src/main/resources/application-example.properties` as the template for `src/main/resources/application.properties`:

```properties
spring.application.name=Staycation
spring.datasource.url=jdbc:postgresql://localhost:5432/staycation
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.port=8080
server.servlet.context-path=/api/v1

jwt.secretKey=YOUR_JWT_SECRET_KEY          # at least 32 characters
frontend.url=http://localhost:3000          # where Stripe redirects after payment

stripe.secret.key=YOUR_STRIPE_SECRET_KEY    # sk_test_...
stripe.webhook.secret=YOUR_STRIPE_WEBHOOK_SECRET   # whsec_...
```

| Property | Purpose |
|---|---|
| `jwt.secretKey` | HMAC signing key for tokens. Treat as a secret; use at least 32 characters. |
| `frontend.url` | Base URL for Stripe's success (`/payments/success`) and cancel (`/payments/failure`) redirects. |
| `stripe.secret.key` | Stripe API key. The app will not start without it. |
| `stripe.webhook.secret` | Signing secret used to verify webhook calls. |
| `spring.jpa.hibernate.ddl-auto` | `create` drops and recreates the schema on every start; use `update` to keep data locally. |

Prefer environment variables for secrets (for example `JWT_SECRETKEY`, `STRIPE_SECRET_KEY`), and never commit real values.

### 4. Run
```bash
./mvnw spring-boot:run          # Windows: mvnw.cmd spring-boot:run
```

### 5. Receive Stripe webhooks locally
```bash
stripe listen --forward-to localhost:8080/api/v1/webhook/payment
```
Copy the `whsec_...` value it prints into `stripe.webhook.secret`.

### 6. Create your first hotel manager
Sign-up always creates a `GUEST`, and there is no seed data, so promote a user directly in the database after signing up:

```sql
INSERT INTO app_user_roles (user_id, roles) VALUES (1, 'HOTEL_MANAGER');
```

Hibernate derives these table and column names; if the statement fails, run `\d app_user_roles` (or list tables with `\dt`) and adjust.

### Typical flow
1. `POST /auth/signup`, then `POST /auth/login`, and keep the `accessToken`.
2. As a manager: `POST /admin/hotels` → `POST /admin/hotels/{id}/rooms` → `PATCH /admin/hotels/{id}/activate`.
3. As a guest: `GET /hotels/search` → `POST /bookings/init` → `POST /bookings/{id}/addGuests` → `POST /bookings/{id}/payments`, then open the returned `sessionUrl` and pay with a Stripe test card.
4. The webhook confirms the booking; check it with `GET /bookings/{id}/status`.

---

## Testing

The repository currently has a single `contextLoads` test, which needs a running PostgreSQL and Stripe configuration to pass. There are no unit or integration tests yet.

```bash
./mvnw test
```

---

## Known limitations

A code review of this project found several security and correctness issues that should be fixed before any real deployment. See **`CODE_REVIEW.md`**.

## Roadmap ideas
- Request validation (`spring-boot-starter-validation`) and consistent HTTP status codes
- Automatic release of expired reservations
- Database migrations (Flyway/Liquibase) instead of `ddl-auto`
- Unit and integration tests (Testcontainers + Stripe test events), CI pipeline, Dockerfile / docker-compose
- A real holiday calendar for the holiday pricing strategy
- Add a `LICENSE` file

## Author
[SOHAMSABLE](https://github.com/SOHAMSABLE)
