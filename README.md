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
-Swagger UI - http://localhost:8080/swagger-ui/index.html#/

## Prerequisites

- Docker and Docker Compose
- Java 17+ (for local development)
- Free API key from [ExchangeRate-API](https://www.exchangerate-api.com/)

## How to Run

### Using Docker Compose

1. Clone the repository:
   ```
   git clone <repository-url>
   cd springboot-currency-converter-test
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
curl -X GET http://localhost:8080/api/convert \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
```


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
```

## Running Tests

You can run the unit tests for each service using Gradle. Make sure you are in the correct service directory before running the commands below.

### Run All Tests for Both Services

```zsh
cd rate-service
./gradlew test

cd ../main-service
./gradlew test
```

### Run Only Controller or Service Tests

You can run only specific test classes or patterns using the `--tests` option. For example:

- Run only controller tests in rate-service:
  ```zsh
  cd rate-service
  ./gradlew test --tests '*controller*'
  ```
- Run only service tests in main-service:
  ```zsh
  cd main-service
  ./gradlew test --tests '*service*'
  ```
