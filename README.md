# Finance Data Processing and Access Control Backend (Java)

A complete Java backend implementation for the assignment using Spring Boot, JPA, and H2 with role-based access control and dashboard analytics APIs.

## Tech Stack

- Java 17
- Spring Boot 3 (Web, Validation, Data JPA)
- H2 database (file-based persistence)
- Maven

## What This Project Covers

This implementation satisfies all core requirements:

1. User and role management
2. Financial records CRUD + filtering
3. Dashboard summary and analytics endpoints
4. Backend access control logic (RBAC interceptor guard)
5. Validation and structured error handling
6. Data persistence using H2 (file mode)

Optional enhancements included:

- Pagination for records list
- Search support on records
- Soft delete for financial records
- Seed data for quick testing
- Basic test class

## Architecture Overview

- `controller`: REST endpoints
- `service`: business logic
- `model`: JPA entities and enums
- `repository`: persistence access
- `security`: role guard annotation + interceptor
- `exception`: centralized API error handling
- `dto`: request/response objects
- `specification`: reusable filtering logic for records

## RBAC Model

Roles:

- `VIEWER`: read records and dashboard, cannot modify
- `ANALYST`: read records and dashboard, cannot modify
- `ADMIN`: full access to users and records

Status:

- `ACTIVE`: user can access APIs
- `INACTIVE`: user is blocked

Authentication approach for assignment simplicity:

- Mock auth header: `X-User-Id`
- RBAC guard (`RbacInterceptor`) loads user from DB, validates ACTIVE status, then checks role requirements via `@RequireRoles`

## Seeded Users

On first run, sample users are created:

- Admin: id `1`, email `admin@finance.local`
- Analyst: id `2`, email `analyst@finance.local`
- Viewer: id `3`, email `viewer@finance.local`

Use `X-User-Id` header with these IDs.

## Run Locally

### Prerequisites

- Java 17+
- Maven 3.9+

### Start

```bash
mvn spring-boot:run
```

The API starts on `http://localhost:8080`.

Health check:

```bash
curl http://localhost:8080/api/health
```

## API Endpoints

All protected endpoints require `X-User-Id`.

### Users (Admin)

- `GET /api/users`
- `GET /api/users/{userId}`
- `POST /api/users`
- `PUT /api/users/{userId}`
- `DELETE /api/users/{userId}`
- `GET /api/users/me` (all active roles)

### Financial Records

- `POST /api/records` (Admin)
- `GET /api/records/{recordId}` (Viewer/Analyst/Admin)
- `GET /api/records` (Viewer/Analyst/Admin)
- `PUT /api/records/{recordId}` (Admin)
- `DELETE /api/records/{recordId}` (Admin, soft delete)

List filters on `GET /api/records`:

- `from` (ISO date)
- `to` (ISO date)
- `category`
- `type` (`INCOME` or `EXPENSE`)
- `search` (matches notes/category)
- `page` (default 0)
- `size` (default 20, max 100)

### Dashboard

- `GET /api/dashboard/summary?from=YYYY-MM-DD&to=YYYY-MM-DD`
- `GET /api/dashboard/category-totals?from=YYYY-MM-DD&to=YYYY-MM-DD&type=EXPENSE`
- `GET /api/dashboard/trends?from=YYYY-MM-DD&to=YYYY-MM-DD&bucket=MONTH`
- `GET /api/dashboard/recent-activity?limit=10`

## Sample Requests

### Create record (Admin)

```bash
curl -X POST http://localhost:8080/api/records \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -d '{
    "amount": 125.50,
    "type": "EXPENSE",
    "category": "Transport",
    "date": "2026-04-01",
    "notes": "Taxi and metro"
  }'
```

### List records as Viewer

```bash
curl "http://localhost:8080/api/records?page=0&size=10&type=EXPENSE" \
  -H "X-User-Id: 3"
```

### Get dashboard summary as Analyst

```bash
curl "http://localhost:8080/api/dashboard/summary?from=2026-01-01&to=2026-12-31" \
  -H "X-User-Id: 2"
```

## Validation and Errors

Input validation is handled using Jakarta Validation annotations. Invalid requests return `400` with a structured body:

```json
{
  "timestamp": "2026-04-05T11:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/records",
  "validationErrors": [
    "amount: must be greater than or equal to 0.01"
  ]
}
```

Other status behaviors:

- `401`: missing/invalid `X-User-Id`
- `403`: inactive user or insufficient role
- `404`: resource not found
- `500`: unexpected internal error

## Assumptions and Tradeoffs

- For assignment simplicity, authentication is mocked via header instead of JWT/session.
- H2 file database is used for easy local setup.
- Records are soft-deleted to preserve history semantics.
- Admin-only write operations are strict to make role behavior explicit.

## How This Maps to Evaluation Criteria

- Backend design: layered architecture, DTO separation, reusable specs
- Logical thinking: explicit RBAC and status checks in interceptor
- Functionality: complete user, record, and dashboard APIs
- Code quality: clear naming and separation of concerns
- Data modeling: normalized entities, enums, audit timestamps
- Validation and reliability: request validation + global error handler
- Documentation: setup, API map, assumptions, tradeoffs included
