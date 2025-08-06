# Quest1 Demo POS System

[![View Codebase](https://img.shields.io/badge/View-Codebase-blue?style=flat-square)](#) [![Watch Demo](https://img.shields.io/badge/Watch-Demo%20Video-red?style=flat-square)](https://youtu.be/reocbJzh4jM)

Quest1 POS is a modern, offline-first Android Point-of-Sale (POS) application. It leverages the Ditto platform for real-time, peer-to-peer data synchronization, enabling seamless operation without an internet connection.


### Key Features

- User Authentication with secure login and registration
- Offline-First Architecture that works without internet connectivity
- Shopping Cart Management for adding, removing, and modifying items
- Payment Processing with simulated payment gateway integration and intelligent routing
- Real-time Analytics with live performance metrics and dashboards
- P2P Data Sync for real-time data synchronization across devices
- Modern UI built with Jetpack Compose

---

## Getting Started

Follow these instructions to get the complete system running on your local machine for development and testing purposes.

### Prerequisites

Ensure you have the following installed and configured on your system:

- **Android Studio**: Latest stable version recommended
- **Docker & Docker Compose**: For running backend microservices
- **Ditto Account**: Free account required to obtain an App ID from the [Ditto Portal](https://portal.ditto.live)

---

## System Setup

### 1. Android Application Setup

Configure your local environment variables to build and run the mobile application.

#### 1.1 Create `local.properties` File

In the root directory of the Android project, create a new file named `local.properties`.

#### 1.2 Add Configuration Variables

Copy the following configuration into your `local.properties` file and replace the placeholder values:

```properties
# Ditto Platform Configuration
DITTO_APP_ID=<YOUR_DITTO_APP_ID>
DITTO_AUTH_URL=<YOUR_DITTO_AUTH_URL>
DITTO_WS_URL=<YOUR_DITTO_WS_URL>

# Local Backend Services Configuration
AUTH_SERVICE_BASE_URL=http://<YOUR_LOCAL_IP_ADDRESS>:5001/
PAYMENT_SERVICE_BASE_URL=http://<YOUR_LOCAL_IP_ADDRESS>:5002/
```

**Configuration Parameters:**

- `DITTO_APP_ID`: Your unique application identifier from the Ditto Portal
- `DITTO_AUTH_URL`: Authentication URL from the Ditto Portal
- `DITTO_WS_URL`: The WebSocket URL for the Ditto Big Peer for cloud sync
- `AUTH_SERVICE_BASE_URL`: Base URL for the authentication service container
- `PAYMENT_SERVICE_BASE_URL`: Base URL for the payment service container

**Important Note**: Ensure the IP address used for local backend services is accessible from your Android device or emulator. Use your computer's local network IP, or for emulator `10.0.2.2`.

---

### 2. Backend Services Setup

The application relies on three backend microservices run as Docker containers:

#### 2.1 Authentication Service (auth-service)

This service handles user registration and login, issuing JWTs for the POS application.

##### A. Create `.env` File

Navigate to the `auth-service/` directory and create a file named `.env`:

```bash
# Flask and JWT Secrets (use strong, random keys)
SECRET_KEY=<YOUR_FLASK_SECRET_KEY>
JWT_SECRET_KEY=<YOUR_JWT_SECRET_KEY>

# MongoDB Connection Details
MONGO_INITDB_ROOT_USERNAME=pos_user
MONGO_INITDB_ROOT_PASSWORD=<YOUR_MONGO_PASSWORD>
MONGO_HOST=db
MONGO_PORT=27017
MONGO_DB_NAME=pos_auth
```

##### B. Run the Container

From the `auth-service/` directory, execute:

```bash
docker-compose up --build -d
```

The service will be available on your local machine at **port 5001**.

#### 2.2 Authentication Webhook (auth-webhook)

This service validates JWTs issued by the Authentication Service for Ditto cloud integration. It must be deployed to a publicly accessible URL.

##### A. Create `.env` File

Navigate to the `auth-webhook/` directory and create a file named `.env`:

```bash
# JWT Secret
JWT_SECRET_KEY=<YOUR_JWT_SECRET_KEY>
```

**Critical Note**: The `JWT_SECRET_KEY` here must be identical to the one set for the auth-service.

##### B. Run the Container

From the `auth-webhook/` directory, execute:

```bash
docker-compose up --build -d
```

The service will run locally on **port 8004**. You must deploy this service or expose this port to a public URL (e.g., using [ngrok](https://ngrok.com/)) that the Ditto cloud can reach.

#### 2.3 Payment Service (payment-service)

This service simulates payment processing with various gateways and runs locally on your network.

##### A. Run the Container

From the `payment-service/` directory, execute:

```bash
docker-compose up --build -d
```

The service will be available on your local machine at port 5002.

--- 
### 3. Ditto Portal Configuration

Configure your Ditto app to use your deployed authentication webhook:

1. Log in to the [Ditto Portal](https://portal.ditto.live) and select your application
2. Navigate to the **Connect** tab
3. Under **Authentication mode**, select "Online with Authentication"
4. In the **Authentication webhooks** section, click "New webhook"
5. Set the **Name** to `auth-webhook` (must match exactly)
6. Set the **URL** to your public webhook URL followed by `/auth` (e.g., `https://your-public-webhook-url.com/auth`)
7. Save the webhook configuration

---

### 4. Building and Running the Application

1. **Sync Gradle Files**: Open the Android project in Android Studio and allow Gradle to sync
2. **Select a Device**: Choose a physical device or emulator connected to the same network as your backend services
3. **Run the App**: Click the "Run" button in Android Studio to build and deploy the application



