# Airline Operations Dashboard

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

**Frontend**
- Angular 21
- TypeScript
- RxJS and Signals
- Reactive Forms
- Sass
- Vitest

**Backend**
- Java 25
- Spring Boot 4
- Spring Data JPA / Hibernate
- Jakarta Validation
- PostgreSQL
- Maven

**Testing and CI**
- JUnit and Mockito
- MockMvc controller tests
- Testcontainers with PostgreSQL
- Angular HTTP service tests
- GitHub Actions

## Repository Structure

- `frontend/` — Angular application
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

## Run Locally

### 1. Clone the repository

```bash
git clone https://github.com/BeyzaAkgun/airline-operations.git
cd airline-operations
```

### 2. Create the database

Create a PostgreSQL database named `airline_operations`:

```sql
CREATE DATABASE airline_operations;
```

The default connection settings are:

- Host: `localhost`
- Port: `5432`
- Database: `airline_operations`
- Username: `postgres`

Set `DB_PASSWORD` to the password of your local PostgreSQL user. Do not commit database credentials.

### 3. Start the backend

From the repository root, in PowerShell:

```powershell
cd backend
$env:DB_PASSWORD = "YOUR_LOCAL_POSTGRES_PASSWORD"
.\mvnw.cmd spring-boot:run
```

Alternatively, configure `DB_PASSWORD` in the IntelliJ run configuration and run `AirlineOperationsApiApplication`.

The API runs at:

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

The frontend currently uses `http://localhost:8080/api/flights` as its API URL. Backend CORS configuration allows the local Angular origin, `http://localhost:4200`.

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

Supported statuses: `SCHEDULED`, `DELAYED`, `CANCELED`.

### Create Flight Request

```json
{
  "flightNumber": "TEST01",
  "origin": "Istanbul",
  "destination": "Antalya",
  "status": "SCHEDULED",
  "departureTime": "2026-10-01T10:00:00Z",
  "gate": "A12"
}
```

The update request uses the same fields except `flightNumber`, which is supplied in the URL and remains unchanged.

### Update Status Request

```json
{
  "status": "DELAYED"
}
```

### Error Responses

The API provides a consistent error response structure:

```json
{
  "timestamp": "2026-10-01T10:00:00Z",
  "status": 400,
  "message": "Validation failed.",
  "path": "/api/flights",
  "fieldErrors": {
    "origin": "Origin is required."
  }
}
```

- `400` — Invalid request or validation failure
- `404` — Flight not found
- `409` — Duplicate flight number
- `500` — Unexpected server error

## Tests

### Frontend

From `frontend/`:

```powershell
npx ng test --watch=false
npm run build
```

The current frontend suite contains 7 tests covering the root component and HTTP service behavior.

### Backend

Start Docker, then run from `backend/`:

```powershell
.\mvnw.cmd test
```

On Linux or macOS:

```bash
bash mvnw test
```

The current backend suite contains 15 tests:

- 7 service unit tests
- 5 controller tests
- 2 PostgreSQL repository tests
- 1 application context test

Repository and application context tests use isolated PostgreSQL containers managed by Testcontainers. They do not require the local development database.

## Continuous Integration

GitHub Actions runs on pushes to `main`, pull requests targeting `main`, and manual triggers.

Two independent jobs run:

- **Frontend:** Install dependencies, build the Angular application, and run tests.
- **Backend:** Set up Java and run Maven tests, including Testcontainers tests.

This workflow validates the application; it does not deploy it.

## Planned Improvements

- Playwright end-to-end tests for browser-based flight management
- Integration of E2E tests into GitHub Actions
- Additional UI refinements and dependency maintenance