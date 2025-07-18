# Healthcare Management System with AI-Powered Query Capabilities

## Overview

A comprehensive healthcare management system designed to manage facilities and patient records with AI-powered natural language query capabilities. The system provides RESTful APIs for facility and patient management, along with an intelligent query endpoint that can process natural language questions about the data.

## Table of Contents
- [System Architecture](#system-architecture)
- [Technology Stack](#technology-stack)
- [Setup Instructions](#setup-instructions)
- [API Documentation](#api-documentation)
- [AI Integration](#ai-integration)
- [Database Schema](#database-schema)
- [Audit Logging](#audit-logging)
- [Error Handling](#error-handling)
- [Future Enhancements](#future-enhancements)

## System Architecture

The system follows a layered architecture:

1. **Presentation Layer**: REST controllers handling HTTP requests/responses
2. **Service Layer**: Business logic and transaction management
3. **Repository Layer**: Data access and persistence
4. **Integration Layer**: AI service integration (Ollama)
5. **Database Layer**: PostgreSQL for data storage

Key architectural decisions:
- **Microservices-ready**: Designed to be easily split into separate services if needed
- **Soft Deletes**: All entities support soft deletion for data recovery
- **Audit Logging**: Comprehensive change tracking for all entities
- **AI Integration**: Hybrid approach combining direct queries for known patterns with LLM fallback

## Technology Stack

- **Backend**: Spring Boot 3.1 (Java 17)
- **Database**: PostgreSQL 15
- **AI Integration**: Ollama (local LLM)
- **API Documentation**: OpenAPI 3.0 (Swagger UI)
- **Build Tool**: Maven
- **Containerization**: Docker
- **Validation**: Jakarta Validation API
- **Mapping**: ModelMapper
- **Testing**: JUnit 5

## Setup Instructions

### Prerequisites
- Docker and Docker Compose
- JDK 17+
- Maven 3.8+

### Local Development Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/your-repo/healthcare-system.git
   cd healthcare-system
   ```

2. Start the services:
   ```bash
   docker-compose up -d
   ```

3. Wait for all services to initialize (especially Ollama which may take a few minutes to download the model)

4. Access the application:
    - API: http://localhost:8888
    - Swagger UI: http://localhost:8888/swagger-ui.html
    - PostgreSQL: localhost:5432 (username: postgres, password: 123)
    - Ollama: http://localhost:11434

### Building from Source

1. Build the application:
   ```bash
   mvn clean package
   ```

2. Run the application:
   ```bash
   java -jar target/healthcare-system-0.0.1-SNAPSHOT.jar
   ```

## API Documentation

The API follows RESTful principles and uses JSON for request/response payloads. All endpoints are documented via Swagger UI.

### Base URL
`http://localhost:8888/api`

### Facility Management

#### List Facilities
```
GET /facilities
```
Parameters:
- `page` (default: 0)
- `size` (default: 10)
- `sort` (default: "name,asc")
- `name` (filter by name)
- `type` (filter by type)

#### Get Facility Details
```
GET /facilities/{id}
```

#### Create Facility
```
POST /facilities
```
Required fields: name, type, address

#### Update Facility
```
PUT /facilities/{id}
```

#### Delete Facility (Soft Delete)
```
DELETE /facilities/{id}
```

#### List Patients by Facility
```
GET /facilities/{facilityId}/patients
```

### Patient Management

#### List Patients
```
GET /patients
```
Parameters:
- `page` (default: 0)
- `size` (default: 10)
- `sort` (default: "lastName,asc")
- `search` (search by name)
- `dob` (filter by date of birth)
- `gender` (filter by gender)

#### Get Patient Details
```
GET /patients/{id}
```

#### Create Patient
```
POST /patients
```
Required fields: facilityId, firstName, lastName, dateOfBirth, gender

#### Update Patient
```
PUT /patients/{id}
```

#### Delete Patient
```
DELETE /patients/{id}
```

### AI Query Endpoint

#### Natural Language Query
```
POST /chat
```
Request body:
```json
{
  "query": "List patients from facility 1"
}
```

#### Complex Query
```
POST /chat
```
Request body:
```json
{
  "query": "Which facilities have more than 2 patients?"
}
```

Supported query patterns:
- "List patients from facility X"
- "Which facilities have more than 50 patients?"
- Other complex queries about patients and facilities

## AI Integration

The system uses a hybrid approach for AI queries:

1. **Direct Query Handling**: For known patterns (like listing patients by facility), the system directly queries the database for faster responses
2. **LLM Fallback**: For complex or unrecognized queries, the system forwards the request to Ollama (local LLM)

Configuration:
- Default model: llama2
- Configurable via environment variables:
    - `OLLAMA_API_BASE_URL`
    - `OLLAMA_MODEL`

## Database Schema

### Facilities Table
```sql
CREATE TABLE facilities (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    address TEXT NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

### Patients Table
```sql
CREATE TABLE patients (
    id BIGSERIAL PRIMARY KEY,
    facility_id BIGINT REFERENCES facilities(id),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(20) NOT NULL,
    address TEXT,
    phone_number VARCHAR(50),
    email VARCHAR(255),
    insurance_number VARCHAR(100),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

### Audit Log Table
```sql
CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    changed_by VARCHAR(100),
    changed_at TIMESTAMP NOT NULL
);
```

Indexes are created on frequently queried fields like facility names, patient names, and foreign keys.

## Audit Logging

The system maintains a comprehensive audit log of all changes to facilities and patients. Audit logs can be queried by:
- Entity type
- Entity ID
- Action type (CREATE, UPDATE, DELETE)
- Date range

Example endpoints:
```
GET /api/audit-logs
GET /api/audit-logs/entity-type/{entityType}
GET /api/audit-logs/entity/{entityType}/{entityId}
GET /api/audit-logs/action/{action}
GET /api/audit-logs/date-range?start=...&end=...
```

## Error Handling

The system provides consistent error responses with:
- HTTP status code
- Error code
- User-friendly message
- Technical details (in development mode)

Error types:
- `400 Bad Request`: Validation errors
- `404 Not Found`: Resource not found
- `409 Conflict`: Duplicate resources
- `500 Internal Server Error`: Unexpected errors


## POSTMAN COLLECTION
Please use the attached `health.json` collection.

Thank you!
