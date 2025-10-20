# Las Dunas Rent A Car 🚗

A comprehensive microservices-based car rental system built with Spring Cloud and Angular.

## 📋 Overview

Las Dunas Rent A Car is a modern, cloud-native application designed for managing car rental operations. The system follows a microservices architecture pattern, providing scalability, maintainability, and independent deployment capabilities.

## 🏗️ Architecture

The application consists of the following components:

### Microservices
- **Ms-Rental** (Port 8081): Core rental management service handling vehicle rentals, reservations, and inventory
- **Ms-Security** (Port 8082): Authentication and authorization service
- **Ms-Comments** (Port 8084): Customer reviews and comments management
- **Ms-Pricing** (Port 8085): Dynamic pricing and rate calculation service
- **Ms-Promo**: Promotions and discount management service (in development, not yet deployed)

### Infrastructure Services
- **Eureka** (Port 8761): Service discovery and registration
- **Spring Cloud Config** (Port 3030): Centralized configuration management
- **Spring Cloud Gateway** (Port 8077): API Gateway and routing

### Frontend
- **Angular Frontend** (Port 4200): Modern web interface for end-users

### Databases
- **MySQL**: Relational database for Ms-Rental and Ms-Security services
- **MongoDB**: NoSQL database for Ms-Comments and Ms-Promo services

## 🛠️ Technology Stack

### Backend
- **Java 17**
- **Spring Boot 3.4.4**
- **Spring Cloud 2024.0.1**
- **Spring Data JPA**
- **Spring Cloud Netflix Eureka**
- **Spring Cloud Config**
- **Spring Cloud Gateway**
- **Spring Cloud OpenFeign**
- **Lombok**

### Frontend
- **Angular 19.2**
- **TypeScript**
- **Angular CLI 19.2.8**

### Databases
- **MySQL 5.7**
- **MongoDB (latest)**

### DevOps & Tools
- **Docker & Docker Compose**
- **Maven**
- **Jib** (for containerization)
- **Nginx** (for frontend)

## 🚀 Getting Started

### Prerequisites

Before running the application, ensure you have the following installed:

- **Docker** (version 20.10 or higher)
- **Docker Compose** (version 2.0 or higher)
- **Java 17** (for local development)
- **Maven 3.6+** (for local development)
- **Node.js 18+** and **npm** (for frontend development)

### Running with Docker Compose

The easiest way to run the entire application is using Docker Compose:

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd Las_Dunas_Rent_A_Car
   ```

2. **Navigate to the docker-compose directory:**
   ```bash
   cd docker-compose
   ```

3. **Start all services:**
   ```bash
   docker-compose up -d
   ```

4. **Check service health:**
   ```bash
   docker-compose ps
   ```

5. **Access the application:**
   - Frontend: http://localhost:4200
   - API Gateway: http://localhost:8077
   - Eureka Dashboard: http://localhost:8761

6. **Stop all services:**
   ```bash
   docker-compose down
   ```

7. **Stop and remove volumes (clean slate):**
   ```bash
   docker-compose down -v
   ```

## 🔧 Local Development

### Building Microservices

Each microservice can be built independently:

```bash
cd Ms-Rental
./mvnw clean package
```

Or build with Docker image:

```bash
./mvnw clean package jib:dockerBuild
```

### Running Microservices Locally

To run a microservice locally:

```bash
cd Ms-Rental
./mvnw spring-boot:run
```

**Note:** Ensure the infrastructure services (Eureka, Config Server, and databases) are running before starting individual microservices.

### Frontend Development

1. **Navigate to the frontend directory:**
   ```bash
   cd Front-Dunas
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Start the development server:**
   ```bash
   ng serve
   ```

4. **Access the application:**
   - Open your browser to http://localhost:4200

5. **Build for production:**
   ```bash
   ng build --configuration production
   ```

## 📦 Service Dependencies

The services start in the following order to respect dependencies:

1. **Databases** (MongoDB, MySQL)
2. **Config Server**
3. **Eureka Server**
4. **Microservices** (Ms-Security, Ms-Rental, Ms-Pricing, Ms-Comments, Ms-Promo*)
5. **Gateway**
6. **Frontend**

*Ms-Promo is currently in development and not included in the Docker Compose deployment.

Each service includes health checks to ensure proper startup sequencing.

## 🌐 API Gateway Routes

All microservice APIs are accessed through the Spring Cloud Gateway at port 8077:

- `/api/rental/**` → Ms-Rental (8081)
- `/api/security/**` → Ms-Security (8082)
- `/api/comments/**` → Ms-Comments (8084)
- `/api/pricing/**` → Ms-Pricing (8085)

## 🧪 Testing

### Running Unit Tests

For Spring Boot microservices:
```bash
cd Ms-Rental
./mvnw test
```

For Angular frontend:
```bash
cd Front-Dunas
npm test
```

### Running End-to-End Tests

For Angular frontend:
```bash
cd Front-Dunas
ng e2e
```

## 📝 Environment Configuration

Configuration files are managed by Spring Cloud Config Server. Create an `env/.env` file in the `docker-compose` directory with necessary environment variables.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Authors

- **Esteban Bare** - [GitHub Profile](https://github.com/Esteban-Bare)

## 🙏 Acknowledgments

- Spring Cloud team for the excellent microservices framework
- Angular team for the powerful frontend framework
- All contributors who help improve this project
