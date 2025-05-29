# GastoZen

## Overview

GastoZen is a expense management application that uses Firebase Authentication and Firestore for secure user management
and data storage.

## Setup

### Firebase Configuration

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com)
2. Download your service account key file
3. Place it in `src/main/resources` as `gastozen-firebase-adminsdk.json`

### Application Properties

```properties
 spring.security.filter.order=10
server.port=8080
```

## Authentication

### Register New User

```bash
 POST /api/auth/register
 Content-Type: application/json

 {
   "name": "John Doe",
   "email": "john@example.com",
   "password": "password123",
   "age": 30,
   "salary": 5000.0,
   "phone": "123456789",
   "occupation": "Software Engineer"
 }
```

### Login

```bash
 POST /api/auth/login
 Content-Type: application/json

 {
   "email": "john@example.com",
   "password": "password123"
 }
```

### Protected Endpoints

All protected endpoints require a valid Firebase ID token in the Authorization header:

```bash
 Authorization: Bearer <your-firebase-token>
```

## Expense Management

### Create Expense

```bash
 POST /api/gastos
 Authorization: Bearer <token>
 Content-Type: application/json

 {
   "valor": 100.0,
   "data": "2024-03-15",
   "descricao": "Grocery shopping"
 }
```

### List Expenses

```bash
 GET /api/gastos
 Authorization: Bearer <token>
```

## Security

The application implements the following security measures:

- Firebase Authentication for user management
- JWT token validation for protected endpoints
- Stateless session management
- Protected routes configuration

### Protected Routes

```java
 /api/gastos/** - Expense management endpoints
 /api/protegido/** - Protected resources
 /api/users/** - User management endpoints
```

### Public Routes

```java
 /api/auth/login -
Login endpoint
 /api/auth/register -
Registration endpoint
 /api/publico/** - Public resources
```

## Error Handling

The application returns standard HTTP status codes:

- 200: Success
- 201: Resource created
- 400: Bad request
- 401: Unauthorized
- 403: Forbidden
- 404: Not found
- 500: Internal server error

For detailed error messages, check the response body.