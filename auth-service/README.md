# POS Authentication Service

## Overview

A containerized authentication service for local network Point-of-Sale (POS) systems. It provides user registration and login functionality, issuing JWTs for secure access control without an internet dependency.

- **Technology Stack**: Python, Flask, MongoDB
- **Deployment**: Docker & Docker Compose
- **Security**: Bcrypt password hashing, JWT-based authentication

## Setup and Configuration

Follow these steps to deploy the service.

### 1. Create the .env File

This file stores secrets and configuration variables.

1. In the root `auth-service` directory, create a file named `.env`
2. Populate the file with the following content. Replace placeholder keys with strong, randomly generated secrets:

```env
# Flask and JWT Secrets
SECRET_KEY=your_strong_flask_secret_key
JWT_SECRET_KEY=your_strong_jwt_secret_key

# MongoDB Connection Details
MONGO_INITDB_ROOT_USERNAME=pos_user
MONGO_INITDB_ROOT_PASSWORD=strongpassword
MONGO_HOST=db
MONGO_PORT=27017
MONGO_DB_NAME=pos_auth
```

### 2. Build and Run the Service

The application stack is managed by Docker Compose.

1. Open a terminal in the `auth-service` directory
2. Run the following command to build and start the containers:

```bash
docker-compose up --build -d
```

3. To verify that the service and database are running, execute:

```bash
docker-compose ps
```

The status for `pos_auth_service` and `pos_mongo_db` should be "running".

## API Usage

The service is accessible at `http://localhost:5001`. Use an API client for testing.

### 1. Register a New User

- **Method**: POST
- **URL**: `http://localhost:5001/register`
- **Body** (raw, JSON):

```json
{
    "username": "manager_user",
    "password": "manager_password123",
    "role": "manager"
}
```

**Success Response (201 Created):**

```json
{
    "message": "User manager_user registered successfully"
}
```

### 2. Log In to Get a Token

- **Method**: POST
- **URL**: `http://localhost:5001/login`
- **Body** (raw, JSON):

```json
{
    "username": "manager_user",
    "password": "manager_password123"
}
```

**Success Response (200 OK):**

```json
{
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOi..."
}
```