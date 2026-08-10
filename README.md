<img width="1830" height="914" alt="Screenshot 2026-08-11 010722" src="https://github.com/user-attachments/assets/892c130e-5e01-4a28-9c6e-89a5708c7dff" />

<img width="1835" height="921" alt="Screenshot 2026-08-11 011112" src="https://github.com/user-attachments/assets/4da4d6ed-8a80-48aa-b40f-943b2e6a05f0" />

<img width="1836" height="916" alt="Screenshot 2026-08-11 010852" src="https://github.com/user-attachments/assets/86c9e944-3fe2-4738-ad1e-e2d0ef8406c6" />

<img width="1839" height="915" alt="Screenshot 2026-08-11 010949" src="https://github.com/user-attachments/assets/da79dcb6-8869-47d7-864b-a18d709283e1" />


# Shipment Tracking & Delivery Visibility Platform

A full-stack logistics platform that provides end-to-end shipment visibility, live delivery tracking, driver management, proof of delivery, customer support, and delivery analytics.

The platform provides role-based dashboards for **Admin, Customer, Driver, and Support** users.

---

## Features

- 🔐 JWT-based authentication and role-based access control
- 🔑 Google OAuth 2.0 authentication
- 📦 Shipment creation and lifecycle management
- 📍 Live driver and shipment tracking
- 🗺️ Interactive map-based delivery monitoring
- 🚚 Driver assignment and management
- 📱 Driver location and route tracking
- ⏱️ ETA and delivery monitoring
- ✍️ Digital delivery confirmation and signature capture
- 📄 Proof of Delivery (POD)
- 🔔 Shipment and support notifications
- 🎫 Customer support and ticket management
- 📊 Shipment analytics and delivery performance reports
- 🛣️ Route history tracking

---

## User Roles

### Admin
- Manage users and drivers
- Manage and monitor shipments
- Assign and monitor deliveries
- View live driver locations
- View delivery and route information
- Monitor support operations
- View analytics and reports

### Customer
- View personal shipments
- Track shipments
- View delivery status and ETA
- View delivery history
- View Proof of Delivery
- View digital signatures
- Raise support requests
- Receive shipment notifications

### Driver
- View assigned shipments
- Update shipment status
- Share live location
- View delivery routes
- Confirm deliveries
- Capture receiver signatures
- Submit delivery confirmation
- View delivery and route history

### Support
- Manage customer support requests
- Search shipment information
- View shipment and delivery status
- Verify delivery information
- View Proof of Delivery
- Investigate delivery complaints
- Escalate unresolved issues

---

## Technology Stack

### Backend

| Technology | Purpose |
|---|---|
| Java 17 | Programming Language |
| Spring Boot 3.5 | Backend Framework |
| Spring Security | Authentication & Authorization |
| JWT | Token-based Authentication |
| OAuth 2.0 | Google Authentication |
| Spring Data JPA | Database Access |
| Hibernate | ORM |
| PostgreSQL | Database |
| Maven | Build Tool |
| OpenPDF | PDF Generation |

### Frontend

| Technology | Purpose |
|---|---|
| React 19 | Frontend Framework |
| Vite | Build Tool |
| React Router | Routing |
| Axios | API Communication |
| Bootstrap 5 | UI |
| Bootstrap Icons | Icons |
| React-Toastify | Notifications |
| Chart.js / Recharts | Analytics |
| Leaflet / OpenStreetMap | Maps |
| jsPDF / AutoTable | Reports |

---

## Architecture

text
                    React Frontend
                         │
                         │ REST API
                         ▼
                  Spring Boot Backend
                         │
              ┌──────────┼──────────┐
              ▼          ▼          ▼
          PostgreSQL   Security   Tracking
           Database    JWT/OAuth    System

## Project Structure

Shipment-Tracking-Delivery-Visibility-Platform/
│
├── SHIPTRACK_JAVA/
│   │
│   ├── shiptrack-backend/
│   │   └── shiptrack-backend/
│   │       ├── src/main/java/com/shiptrack/
│   │       │   ├── config/
│   │       │   ├── controller/
│   │       │   ├── service/
│   │       │   ├── repository/
│   │       │   ├── entity/
│   │       │   ├── dto/
│   │       │   ├── security/
│   │       │   ├── exception/
│   │       │   └── util/
│   │       └── pom.xml
│   │
│   └── shiptrack-frontend/
│       ├── src/
│       │   ├── api/
│       │   ├── auth/
│       │   ├── components/
│       │   ├── pages/
│       │   └── styles/
│       ├── package.json
│       └── vite.config.js
│
└── README.md


## Shipment Workflow

Shipment Created
       ↓
Shipment Assigned
       ↓
Picked Up
       ↓
In Transit
       ↓
Out for Delivery
       ↓
Delivered
       ↓
Proof of Delivery


## Prerequisites

Install the following before running the project:

Java 17+
Maven 3.8+
Node.js 18+
npm
PostgreSQL

Google OAuth credentials are required for Google login.

## Database Setup

Create the PostgreSQL database:

CREATE DATABASE shiptrack;

Make sure PostgreSQL is running.

## Backend Setup

Navigate to: cd SHIPTRACK_JAVA/shiptrack-backend/shiptrack-backend

Configure the required environment variables and database credentials.

Start the backend: mvn spring-boot:run

Backend URL: http://localhost:8081

API Base URL: http://localhost:8081/api

## Frontend Setup

Navigate to: cd SHIPTRACK_JAVA/shiptrack-frontend

Install dependencies: npm install

Create a .env file in the frontend directory.

Example: VITE_GOOGLE_CLIENT_ID=your-client-id.apps.googleusercontent.com

Add other environment variables required by the application.

Start the frontend: npm run dev

Frontend URL: http://localhost:5173

## Environment Variables
- Backend
  DB_PASSWORD
  JWT_SECRET
  GOOGLE_CLIENT_ID

- Frontend
  VITE_GOOGLE_CLIENT_ID

If map services require an API key, configure the corresponding Vite environment variable locally.


## Available Scripts
- Backend
mvn spring-boot:run
mvn test
mvn package

- Frontend
npm run dev
npm run build
npm run preview
npm run lint

## Security

- The application uses:

JWT authentication
Spring Security
Role-based authorization
Google OAuth 2.0
Protected frontend routes
Protected backend APIs

Users can access only the features permitted by their assigned role.

- Current Dashboards
Dashboard	Main Responsibility
Admin	Platform management, drivers, shipments, monitoring and analytics
Customer	Personal shipments, tracking, delivery information and support
Driver	Assigned deliveries, live location, status updates and delivery confirmation
Support	Customer issues, shipment assistance and delivery verification


## Project Goal

The goal of this project is to provide a centralized logistics platform that connects:

Customers
    ↕
Shipment Platform
    ↕
Drivers
    ↕
Admin
    ↕
Support

The system provides complete visibility from shipment creation to final delivery and Proof of Delivery.
