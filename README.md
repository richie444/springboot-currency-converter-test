# Currency Conversion Application

A microservices-based application for converting currencies using real-time exchange rates from ExchangeRate-API.

## Architecture

The application consists of two Spring Boot services:
- **Rate Service**: Handles fetching exchange rates from an external API
- **Main Service**: Handles currency conversion calculations and stores conversion history

## Technologies Used

- Java 17
- Spring Boot 3.x
- Spring Data JDBC
- PostgreSQL
- Docker
- Jakarta Validation
- WebClient
- JWT Authentication

## Prerequisites

- Docker and Docker Compose
- Java 17+ (for local development)
- Free API key from [ExchangeRate-API](https://www.exchangerate-api.com/)

## How to Run

### Using Docker Compose

1. Clone the repository:
   ```
   git clone <repository-url>
   cd currency-conversion-app
   ```

2. Set your ExchangeRate-API key as an environment variable:
   ```
   export EXCHANGE_RATE_API_KEY=your_api_key_here
   ```

3. Start the application using Docker Compose:
   ```
   docker-compose up --build
   ```

The services will be available at:
- Main Service: http://localhost:8080
- Rate Service: http://localhost:8081

## API Usage

### Authentication

The application uses JWT-based authentication. Before accessing protected endpoints, you need to obtain a JWT token:

```
POST /api/auth/login
```

Request body:
```json
{
  "username": "user",
  "password": "password"
}
```

Response:
```json
{
  "token": "eyJhbG...",
  "username": "user",
  "role": "ROLE_USER"
}
```

Use this token in subsequent requests as a Bearer token in the Authorization header:
```
Authorization: Bearer eyJhbG...
```

#### Using the Bearer Token

When making any authenticated request, include the token in the HTTP header as follows:

- Add an `Authorization` header
- The value must begin with the word `Bearer` followed by a space
- After the space, paste your JWT token

Example header:
```
Authorization: Bearer eyJhbG...
```

##### Example using curl:
```bash
curl -X GET http://localhost:8080/api/protected-endpoint \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
```

##### Troubleshooting
- Make sure there's exactly one space between "Bearer" and the token
- The token should be copied completely without any extra spaces
- If using Postman, select "Bearer Token" in the Authorization tab and paste the token without "Bearer" prefix
- Token expiration: JWT tokens typically expire after a set period (usually 24 hours)

#### Default Users

Two default users are created at startup:
- Regular user: username=`user`, password=`password`, role=`ROLE_USER`
- Admin user: username=`admin`, password=`admin123`, role=`ROLE_ADMIN`

### User Management (Admin only)

#### Create a new user
```
POST /api/users
```

Request body:
```json
{
  "username": "newuser",
  "password": "newpassword",
  "role": "ROLE_USER"
}
```

Response:
```json
{
  "id": 3,
  "username": "newuser",
  "role": "ROLE_USER"
}
```

#### Get all users
```
GET /api/users
```

#### Get user details
```
GET /api/users/{username}
```

Response:
```json
{
  "id": 3,
  "username": "newuser",
  "role": "ROLE_USER",
  "enabled": true,
  "createdAt": "2025-05-09T12:30:00"
}
```

#### Change password
```
PUT /api/users/{username}/password
```

Request body:
```json
{
  "newPassword": "newSecurePassword"
}
```

#### Enable or disable a user
```
PUT /api/users/{username}/status
```

Request body:
```json
{
  "enabled": false
}
```

#### Update a user's role
```
PUT /api/users/{username}/role
```

Request body:
```json
{
  "role": "ROLE_ADMIN"
}
```

#### Delete a user
```
DELETE /api/users/{username}
```

### Rate Service Endpoints

#### Get Status
```
GET /status
```
Returns the service status.

#### Get Exchange Rate
```
GET /rate?from=USD&to=EUR
```
Returns the current exchange rate between two currencies.

### Main Service Endpoints

#### Get Status
```
GET /status
```
Returns the service status.

#### Convert Currency
```
POST /convert
```

Request body:
```json
{
  "from": "USD",
  "to": "EUR",
  "amount": 100.00
}
```

Response:
```json
{
  "from": "USD",
  "to": "EUR",
  "amount": 100.00,
  "convertedAmount": 85.00,
  "rate": 0.85
}
```

## Database Schema

The application uses the following database schema:

### Conversions Table
Stores the history of currency conversions:

```sql
CREATE TABLE conversions (
    id SERIAL PRIMARY KEY,
    from_currency VARCHAR(3) NOT NULL,
    to_currency VARCHAR(3) NOT NULL,
    amount DECIMAL(19, 4) NOT NULL,
    converted_amount DECIMAL(19, 4) NOT NULL,
    rate DECIMAL(19, 6) NOT NULL,
    timestamp TIMESTAMP NOT NULL
);
```

### Users Table
Stores user authentication details:

```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## Security Implementation

- JWT-based stateless authentication
- Database-backed user management with roles
- Password encryption using BCrypt
- Method-level security with @PreAuthorize
- Role-based access control

## Development

### Build Services Locally

To build the services locally:

```
cd rate-service
./gradlew build

cd ../main-service
./gradlew build
```

### Testing

Run unit tests:

```
cd rate-service
./gradlew test

cd ../main-service
./gradlew test
```# Java-Spring-boot-Test
