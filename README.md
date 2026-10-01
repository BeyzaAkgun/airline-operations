# Airline Operations Dashboard

[![CI](https://github.com/BeyzaAkgun/airline-operations/actions/workflows/ci.yml/badge.svg)](https://github.com/BeyzaAkgun/airline-operations/actions/workflows/ci.yml)

A full-stack application for managing flight operations, built with Angular, Spring Boot, and PostgreSQL.

## Features

- Flight dashboard with total and status-based summary cards
- Search by flight number, origin, and destination
- Filter flights by status
- Create, edit, and delete flights
- Delete confirmation
- Typed reactive forms with client-side validation
- Server-side validation and field-level error messages
- Duplicate flight number protection
- Loading, error, success, and empty states
- Responsive layout
- PostgreSQL persistence

Flight numbers are unique and cannot be changed after creation.

Departure times are entered in the user's local time and displayed in UTC in the flight table.

## Technology Stack

### Frontend

- Angular 21
- TypeScript
- RxJS and Signals
- Reactive Forms
- Sass

### Backend

- Java 25
- Spring Boot 4
- Spring Data JPA / Hibernate
- Jakarta Validation
- PostgreSQL
- Maven

### Testing and CI

- Vitest for Angular component and HTTP service tests
- JUnit and Mockito for backend service tests
- MockMvc for controller and validation tests
- Testcontainers with PostgreSQL for repository and application context tests
- Playwright for end-to-end browser tests
- GitHub Actions for automated builds and tests

## Repository Structure

- `frontend/` — Angular application
- `frontend/e2e/` — Playwright test scenarios
- `frontend/playwright.config.ts` — Playwright configuration
- `backend/` — Spring Boot REST API
- `.github/workflows/ci.yml` — Build and test workflow

Earlier Java, JavaScript, and TypeScript practice implementations are available in [airline-operations-practice](https://github.com/BeyzaAkgun/airline-operations-practice).

## Prerequisites

- Node.js 22.12 or later within the Node.js 22 release line
- npm
- JDK 25
- PostgreSQL
- Docker Desktop or a compatible Docker engine for backend integration tests

The backend includes the Maven Wrapper, so a separate Maven installation is not required.

The local commands below use PowerShell. On Linux or macOS, use `bash mvnw` instead of `.\mvnw.cmd` and configure environment variables using your shell.

## Run Locally

### 1. Clone the repository

```powershell
git clone https://github.com/BeyzaAkgun/airline-operations.git
cd airline-operations
```

### 2. Create the development database

Create a PostgreSQL database:

```sql
CREATE DATABASE airline_operations;
```

The default connection settings are:

| Setting | Value |
|---|---|
| Host | `localhost` |
| Port | `5432` |
| Database | `airline_operations` |
| Username | `postgres` |

The database password is read from the `DB_PASSWORD` environment variable. Do not commit real credentials.

### 3. Start the backend

From the repository root:

```powershell
cd backend
$env:DB_PASSWORD = "YOUR_LOCAL_POSTGRES_PASSWORD"
.\mvnw.cmd spring-boot:run
```

The environment variable applies to the current PowerShell session.

Alternatively, configure `DB_PASSWORD` in the IntelliJ run configuration and run `AirlineOperationsApiApplication`.

The API is available at:

```text
http://localhost:8080/api/flights
```

The current development configuration uses Hibernate `ddl-auto=update` to create or update the database schema.

When the flights table is empty, the application inserts five sample flights at startup. If any flights already exist, initialization is skipped.

### 4. Start the frontend

Open a separate terminal in the repository root:

```powershell
cd frontend
npm ci
npm start
```

Open:

```text
http://localhost:4200
```

The frontend currently uses `http://localhost:8080/api/flights` as its API URL.

Backend CORS configuration allows requests from `http://localhost:4200`.

## REST API

| Method | Endpoint | Description | Success |
|---|---|---|---|
| GET | `/api/flights` | List all flights | 200 |
| GET | `/api/flights?status=DELAYED` | Filter by status | 200 |
| GET | `/api/flights/{flightNumber}` | Retrieve a flight | 200 |
| POST | `/api/flights` | Create a flight | 201 |
| PUT | `/api/flights/{flightNumber}` | Update a flight | 200 |
| PATCH | `/api/flights/{flightNumber}/status` | Update flight status | 200 |
| DELETE | `/api/flights/{flightNumber}` | Delete a flight | 204 |

Supported statuses:

- `SCHEDULED`
- `DELAYED`
- `CANCELED`

### Create Flight Request

```json
{
  "flightNumber": "TEST01",
  "origin": "Istanbul",
  "destination": "Antalya",
  "status": "SCHEDULED",
  "departureTime": "2026-12-20T10:00:00Z",
  "gate": "A12"
}
```

### Update Flight Request

The flight number is supplied in the URL and remains unchanged.

```json
{
  "origin": "Istanbul",
  "destination": "London",
  "status": "DELAYED",
  "departureTime": "2026-12-20T11:00:00Z",
  "gate": "B10"
}
```

### Update Status Request

```json
{
  "status": "DELAYED"
}
```

### Validation

- Flight number is required, must not be blank, and must contain 2–10 characters.
- Flight numbers are trimmed and normalized to uppercase.
- Origin and destination are required and must not exceed 100 characters.
- Status and departure time are required.
- Departure time must use a valid date-time format with an offset.
- Gate is optional and must not exceed 10 characters.
- Duplicate flight numbers are rejected.

### Error Responses

The API uses a consistent error response structure. For example:

```json
{
  "timestamp": "2026-10-01T10:00:00Z",
  "status": 400,
  "message": "Validation failed.",
  "path": "/api/flights",
  "fieldErrors": {
    "origin": "Origin is required"
  }
}
```

| Status | Meaning |
|---|---|
| 400 | Invalid request or validation failure |
| 404 | Flight not found |
| 409 | Duplicate flight number |
| 500 | Unexpected server error |

Field-level validation messages are returned in `fieldErrors`. Other handled errors use an empty map.

## Tests

The current automated suite contains 24 tests:

| Suite | Tests |
|---|---:|
| Angular component and HTTP service tests | 7 |
| Backend service tests | 7 |
| Backend controller tests | 5 |
| PostgreSQL repository tests | 2 |
| Application context test | 1 |
| Playwright end-to-end tests | 2 |

### Frontend Tests and Build

From `frontend/`:

```powershell
npm ci
npx ng test --watch=false
npm run build
```

The Angular tests cover the root component and HTTP service behavior, including state updates after API responses.

### Backend Tests

Start Docker, then run from `backend/`:

```powershell
.\mvnw.cmd test
```

On Linux or macOS:

```bash
bash mvnw test
```

Backend coverage includes:

- Service behavior for creating, updating, deleting, and changing flight status
- Missing-flight and duplicate-flight handling
- Controller HTTP status codes, validation, and error responses
- Saving and retrieving flights through PostgreSQL
- Database enforcement of unique flight numbers
- Application startup with a test database

Repository and application context tests use isolated PostgreSQL containers managed by Testcontainers. They do not require the local development database or its password.

The PostgreSQL image may take additional time to download on the first run.

### End-to-End Tests

Playwright runs against the Angular frontend and the real Spring Boot API backed by a separate PostgreSQL database.

The current scenarios cover:

1. Loading flights on the dashboard.
2. Creating, searching for, editing, and deleting a flight through the UI.

#### 1. Create the E2E database

```sql
CREATE DATABASE airline_operations_e2e;
```

#### 2. Start the backend with the E2E profile

Stop any backend already running on port 8080.

From `backend/`, in PowerShell:

```powershell
$env:DB_PASSWORD = "YOUR_LOCAL_POSTGRES_PASSWORD"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=e2e"
```

Alternatively, add this program argument to the IntelliJ application run configuration:

```text
--spring.profiles.active=e2e
```

Configure `DB_PASSWORD` in that run configuration as well.

The `e2e` profile uses `application-e2e.properties` to connect to `airline_operations_e2e`. On an empty flights table, the application inserts its sample flights.

The dashboard-loading test expects the sample flight `TX23` to be present.

#### 3. Install Playwright and run tests

In a separate terminal, from `frontend/`:

```powershell
npm ci
npx playwright install chromium
npm run test:e2e
```

Playwright starts Angular automatically. Stop any existing Angular server on port 4200 before running the tests.

To watch the browser during execution:

```powershell
npm run test:e2e -- --headed
```

Tests run sequentially in Chromium. On failure, Playwright retains traces and screenshots under `frontend/test-results/`.

The CRUD test uses a generated flight number and attempts API cleanup in a `finally` block, including when the test fails.

To return to normal development, stop the E2E backend and restart without the `e2e` profile.

## Continuous Integration

[View workflow runs](https://github.com/BeyzaAkgun/airline-operations/actions)

GitHub Actions runs on:

- Pushes to `main`
- Pull requests targeting `main`
- Manual workflow triggers

The workflow contains three jobs:

### Frontend

- Set up Node.js
- Install dependencies with `npm ci`
- Build the Angular application
- Run Angular tests

### Backend

- Set up Java 25
- Run Maven tests, including PostgreSQL Testcontainers tests

### End-to-End

After the frontend and backend jobs succeed:

- Start a separate PostgreSQL service
- Install frontend dependencies and Chromium
- Package and start the backend with the `e2e` profile
- Wait for the API and sample flight to be available
- Run Playwright tests

The E2E job uploads backend logs and available Playwright failure diagnostics as the `e2e-diagnostics` artifact, retained for seven days.

CI uses a temporary test database and does not require local development credentials.

This workflow validates the application; it does not deploy it.

## Current Scope and Planned Improvements

- The frontend API URL and CORS origin currently target local development.
- The development and E2E profiles use Hibernate schema updates.
- Sample flights are inserted whenever the flights table is empty at startup.
- Further work includes UI refinements, dependency maintenance, and broader automated test coverage.