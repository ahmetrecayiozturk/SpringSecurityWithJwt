# 🔐 Spring Security with JWT

A robust and secure Spring Boot application implementing JWT (JSON Web Token) authentication with Spring Security. This project demonstrates modern authentication patterns, stateless security architecture, and RESTful API design.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Latest-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)](#quick-start)

## 📋 Table of Contents

- [🔐 Spring Security with JWT](#-spring-security-with-jwt)
  - [📋 Table of Contents](#-table-of-contents)
  - [✨ Features](#-features)
  - [🏗️ Architecture](#️-architecture)
  - [🚀 Quick Start](#-quick-start)
    - [Prerequisites](#prerequisites)
    - [Local Development](#local-development)
    - [Docker Deployment](#docker-deployment)
  - [📡 API Endpoints](#-api-endpoints)
  - [💡 Usage Examples](#-usage-examples)
    - [User Registration](#user-registration)
    - [User Login](#user-login)
    - [Accessing Protected Endpoints](#accessing-protected-endpoints)
  - [🛠️ Configuration](#️-configuration)
  - [🧪 Testing](#-testing)
  - [🚨 Troubleshooting](#-troubleshooting)
  - [🤝 Contributing](#-contributing)
  - [📄 License](#-license)
  - [👤 Author](#-author)

## ✨ Features

- 🔒 **JWT Authentication**: Stateless authentication using JSON Web Tokens
- 🔐 **Spring Security Integration**: Comprehensive security configuration with custom filters
- 👥 **User Management**: User registration and authentication system
- 🔑 **Password Encryption**: BCrypt password hashing for enhanced security
- 🗄️ **PostgreSQL Integration**: Robust database connectivity with JPA/Hibernate
- 🐳 **Docker Ready**: Containerized deployment with multi-stage builds
- 📊 **RESTful API**: Clean and intuitive REST endpoints
- ⚡ **High Performance**: Stateless architecture for scalability
- 🛡️ **CSRF Protection**: Configurable CSRF protection mechanisms
- 📝 **Detailed Logging**: Comprehensive logging for debugging and monitoring

## 🏗️ Architecture

This application follows a layered architecture pattern with clear separation of concerns:

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Client App    │────│  REST API Layer  │────│ Security Filter │
│  (Frontend/API) │    │   Controllers    │    │   JWT Filter    │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                              │                         │
                              ▼                         ▼
                    ┌──────────────────┐    ┌─────────────────┐
                    │  Service Layer   │────│ Security Config │
                    │  Business Logic  │    │ Authentication  │
                    └──────────────────┘    └─────────────────┘
                              │                         │
                              ▼                         ▼
                    ┌──────────────────┐    ┌─────────────────┐
                    │ Repository Layer │────│   JWT Utility   │
                    │   Data Access    │    │ Token Management│
                    └──────────────────┘    └─────────────────┘
                              │
                              ▼
                    ┌──────────────────┐
                    │ PostgreSQL DB    │
                    │   Data Storage   │
                    └──────────────────┘
```

### Key Components:

- **JWT Filter**: Intercepts requests and validates JWT tokens
- **Security Configuration**: Defines authentication and authorization rules
- **User Service**: Handles user operations and authentication logic
- **JWT Utility**: Manages token generation, validation, and extraction
- **User Repository**: Data access layer for user management

## 🚀 Quick Start

### Prerequisites

- ☕ **Java 21** or later
- 🐘 **PostgreSQL** database
- 🐳 **Docker** (optional, for containerized deployment)

### Local Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/ahmetrecayiozturk/SpringSecurityWithJwt.git
   cd SpringSecurityWithJwt
   ```

2. **Option A: Using Local PostgreSQL**
   
   Set up a local PostgreSQL database and update `app/src/main/resources/application-dev.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/springjwt
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```
   
   Then run with dev profile:
   ```bash
   ./gradlew bootRun --args='--spring.profiles.active=dev'
   ```

3. **Option B: Using Docker Compose (Recommended)**
   ```bash
   docker-compose up -d
   ```
   This will start both the application and PostgreSQL database.

4. **Verify the application**
   ```bash
   curl http://localhost:8080/auth/test
   # Should return 401 Unauthorized (expected for protected endpoint)
   ```

### Docker Deployment

#### Using Docker Compose (Full Stack)
```bash
# Start application with database
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

#### Manual Docker Build

**Option 1: Multi-stage build (builds inside Docker)**
```bash
# Build the Docker image
docker build -t spring-security-jwt .

# Run with external database
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/your-db \
  -e SPRING_DATASOURCE_USERNAME=your-username \
  -e SPRING_DATASOURCE_PASSWORD=your-password \
  spring-security-jwt
```

**Option 2: Simple build (build locally first)**
```bash
# Build the project locally first
./gradlew clean build -x test

# Build simple Docker image
docker build -f Dockerfile.simple -t spring-security-jwt-simple .

# Run the container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/your-db \
  -e SPRING_DATASOURCE_USERNAME=your-username \
  -e SPRING_DATASOURCE_PASSWORD=your-password \
  spring-security-jwt-simple
```

## 📡 API Endpoints

| Method | Endpoint      | Description                    | Authentication |
|--------|---------------|--------------------------------|----------------|
| POST   | `/auth/register` | Register a new user         | ❌ None        |
| POST   | `/auth/login`    | Authenticate user and get JWT | ❌ None        |
| GET    | `/auth/test`     | Test protected endpoint     | ✅ JWT Required |

### Request/Response Schemas

**Registration Request:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Login Request:**
```json
{
  "username": "string", 
  "password": "string"
}
```

**Login Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

## 💡 Usage Examples

### User Registration

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "securePassword123"
  }'
```

**Response:**
```
User registered successfully
```

### User Login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "securePassword123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huX2RvZSIsImlhdCI6MTYzOTQ4NjQwMCwiZXhwIjoxNjM5NDkwMDAwfQ.signature"
}
```

### Accessing Protected Endpoints

```bash
curl -X GET http://localhost:8080/auth/test \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Response:**
```json
{
  "message": "kullanıcı doğrulandı"
}
```

## 🛠️ Configuration

### Environment Variables

Copy the example environment file and customize it for your needs:
```bash
cp .env.example .env
```

Available environment variables:
- `SPRING_DATASOURCE_URL`: PostgreSQL connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `JWT_SECRET`: Secret key for JWT token signing
- `JWT_EXPIRATION`: Token expiration time in milliseconds
- `SERVER_PORT`: Application port (default: 8080)

### Security Configuration

The application uses a custom security configuration that:

- Disables CSRF for stateless API
- Permits public access to registration and login endpoints
- Requires authentication for all other endpoints
- Uses JWT filter for token validation
- Implements BCrypt password encoding

### JWT Configuration

- **Token Expiration**: 1 hour (3600000 ms)
- **Signature Algorithm**: HS256
- **Secret Key**: Configurable (should be externalized in production)

### Database Configuration

The application supports PostgreSQL with the following default configuration:
- **Database**: PostgreSQL
- **ORM**: Hibernate/JPA
- **Connection Pool**: HikariCP (default)
- **DDL Strategy**: Update (creates/updates tables automatically)

## 🧪 Testing

Run the test suite:

```bash
./gradlew test
```

Run specific test classes:

```bash
./gradlew test --tests "*AuthControllerTest"
```

## 🚨 Troubleshooting

### Common Issues

**Database Connection Issues**
```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# View application logs
docker-compose logs app

# Reset database
docker-compose down -v && docker-compose up -d
```

**JWT Token Issues**
- Ensure the `Authorization` header includes `Bearer ` prefix
- Check token expiration (default: 1 hour)
- Verify the JWT secret is properly configured

**Build Issues**
```bash
# Clean and rebuild
./gradlew clean build

# Check Java version
java -version  # Should be Java 21+
```

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Commit your changes**
   ```bash
   git commit -m 'Add amazing feature'
   ```
4. **Push to the branch**
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Open a Pull Request**

### Development Guidelines

- Follow Java coding conventions
- Write comprehensive tests for new features
- Update documentation for API changes
- Ensure all tests pass before submitting PR
- Use meaningful commit messages

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👤 Author

**Ahmet Recayi Öztürk**

- 🐱 GitHub: [@ahmetrecayiozturk](https://github.com/ahmetrecayiozturk)
- 📧 Email: [Contact via GitHub](https://github.com/ahmetrecayiozturk)

---

⭐ **Star this repository if you find it helpful!**

🐛 **Found a bug?** [Open an issue](https://github.com/ahmetrecayiozturk/SpringSecurityWithJwt/issues)

💬 **Have questions?** [Start a discussion](https://github.com/ahmetrecayiozturk/SpringSecurityWithJwt/discussions)