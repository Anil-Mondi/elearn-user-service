# User Service

## Overview

The **User Service** is responsible for managing user accounts, authentication, authorization, profile management, and password recovery within the E-Learn Microservices Platform.

It serves as the identity management component of the system and provides secure JWT-based authentication for all platform users.

---

## Responsibilities

* User Registration
* User Login
* JWT Token Generation
* Password Encryption (BCrypt)
* Forgot Password
* Password Reset
* User Profile Management
* User Validation
* Authentication APIs
* Publish User Events (Future)

---

## Technology Stack

* Java 17
* Spring Boot 3
* Spring Security
* Spring Data JPA
* Spring Cloud Eureka Client
* Spring Cloud OpenFeign
* JWT Authentication
* H2 Database (Current)
* MySQL (Future)
* Apache Kafka
* Spring Boot Actuator
* Spring Mail
* OpenAPI / Swagger

---

## Features

### Authentication

* User Registration
* Secure Login
* JWT Token Generation
* BCrypt Password Encryption
* Stateless Authentication

---

### User Management

* View User Profile
* Update User Information
* Role Management
* User Validation

---

### Password Management

* Forgot Password
* Reset Password
* Email-based Password Recovery (Upcoming)

---

## Architecture

```text
Angular Frontend
        │
        ▼
   API Gateway
        │
        ▼
   User Service
        │
        ├──────────────► Database
        │
        ├──────────────► Kafka
        │
        └──────────────► Notification Service
```

---

## REST APIs

### Authentication

| Method | Endpoint                     | Description     |
| ------ | ---------------------------- | --------------- |
| POST   | `/api/users/register`        | Register User   |
| POST   | `/api/users/login`           | User Login      |
| POST   | `/api/users/forgot-password` | Forgot Password |
| POST   | `/api/users/reset-password`  | Reset Password  |

---

### User Profile

| Method | Endpoint             | Description         |
| ------ | -------------------- | ------------------- |
| GET    | `/api/users/profile` | Get User Profile    |
| PUT    | `/api/users/profile` | Update User Profile |

---

## Database

Current Database

* H2 Database

Future Migration

* MySQL

Future Enhancements

* Flyway Database Migration
* Optimized Indexing
* Audit Columns

---

## Security

The User Service implements secure authentication using:

* Spring Security
* JWT Authentication
* BCrypt Password Encoding
* Stateless Session Management
* Protected REST APIs

---

## Eureka Integration

The service automatically registers with Eureka Server.

Example:

```text
USER-SERVICE
```

This enables dynamic service discovery across the microservices ecosystem.

---

## Monitoring

Spring Boot Actuator endpoints are enabled.

Available endpoints:

* `/actuator/health`
* `/actuator/info`
* `/actuator/prometheus`
* `/actuator/metrics`

---

## Future Enhancements

* Email Verification
* Refresh Token Support
* OTP Login
* Mobile Authentication
* Account Locking
* Login Attempt Tracking
* Audit Logging
* User Activity History
* Kafka Event Publishing
* Redis Session Cache

---

## Project Structure

```text
user-service
│
├── controller
├── service
├── repository
├── entity
├── dto
├── mapper
├── security
├── exception
├── config
├── util
├── constant
├── resources
└── UserServiceApplication.java
```

---

## Role in E-Learn Platform

The User Service acts as the identity and authentication provider for the E-Learn platform. It manages user accounts, secures access using JWT, and provides authentication services for all other microservices through the API Gateway.

---

## Future Event Flow

```text
User Registration
        │
        ▼
User Service
        │
        ▼
Kafka Event
        │
        ▼
Notification Service
        │
        ▼
Welcome Email
```

---

## Author

**Anil Mondi**

