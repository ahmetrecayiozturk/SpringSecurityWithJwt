# Spring Security With JWT

A modern Spring Boot application demonstrating how to implement stateless authentication and authorization using **Spring Security** and **JSON Web Tokens (JWT)**. This repository is ideal for developers looking to learn or bootstrap secure REST APIs with token-based authentication.

## Features

- **User Registration & Login**
  - Secure registration and login endpoints (`/auth/register`, `/auth/login`) with password hashing via BCrypt.
- **JWT-Based Authentication**
  - Stateless session management using JWTs for scalable and secure APIs.
- **Role-based Authorization**
  - Simple user roles; extendable for advanced permissions.
- **Spring Boot & Spring Security**
  - Uses Spring Boot’s dependency injection, REST controllers, and configuration best practices.
- **Test Endpoint**
  - `/auth/test` endpoint to verify JWT-protected resource access.
- **Docker Support**
  - Dockerfile provided for containerized deployments.

## Project Structure

```
app/
├── src/
│   └── main/
│       ├── java/org/example/
│       │   ├── App.java                # Spring Boot entry point
│       │   ├── AppConfig.java          # Global app configuration
│       │   ├── config/
│       │   │   └── SecurityConfig.java # Spring Security & JWT configuration
│       │   ├── controller/
│       │   │   └── AuthController.java # Auth endpoints (register, login, test)
│       │   ├── dto/
│       │   │   └── AuthRequest.java    # DTO for authentication requests
│       │   ├── model/
│       │   │   └── User.java           # User entity
│       │   ├── repository/
│       │   │   └── UserRepository.java # User JPA repository
│       │   ├── security/
│       │   │   └── JwtUtil.java        # JWT utilities
│       │   └── service/
│       │       └── CustomUserDetailsService.java # User details for authentication
├── build.gradle
├── Dockerfile
└── settings.gradle
```
## Project Schema 

User → [POST /auth/login] → AuthController
AuthController → AuthenticationManager + UserDetailsService → Authentication
AuthController → JwtUtil → Generate JWT Token
→ Token is returned to the user

User → [Every request with Authorization: Bearer <token>] → JwtFilter
JwtFilter → JwtUtil + UserDetailsService → Token validation
JwtFilter → SecurityContextHolder → Authenticate the user

(If the token is valid, the user can access the endpoint; if not, an error is returned!)

## Quick Start

### Prerequisites

- Java 21+
- Gradle (or use the included wrapper)
- Docker (optional)

### Running Locally

```bash
# Build and run with Gradle
cd app
./gradlew bootRun
```

### Using Docker

```bash
docker build -t spring-jwt-app .
docker run -p 8080:8080 spring-jwt-app
```

### API Endpoints

| Endpoint         | Method | Description                               | Auth Required |
|------------------|--------|-------------------------------------------|--------------|
| `/auth/register` | POST   | Register a new user                       | No           |
| `/auth/login`    | POST   | Authenticate user, receive JWT            | No           |
| `/auth/test`     | GET    | Test endpoint, returns if authenticated   | Yes (JWT)    |

#### Example Auth Request

```json
POST /auth/register
{
  "username": "john",
  "password": "secret"
}
```

#### Example Login Response

```json
{
  "token": "<JWT Token>"
}
```

Include the JWT token in the `Authorization` header for protected endpoints:

```
Authorization: Bearer <JWT Token>
```

## Extending

- Add more user roles/privileges by extending the `User` entity and security config.
- Integrate with databases by configuring your preferred datasource.

## License

This project is open source and available under the [MIT License](LICENSE).

---

> **Author:** [ahmetrecayiozturk](https://github.com/ahmetrecayiozturk)  
