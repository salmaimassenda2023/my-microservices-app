# 🛒 E-Commerce Microservices Platform

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-3.6-black.svg)](https://kafka.apache.org/)
[![Tests](https://img.shields.io/badge/tests-passing-brightgreen.svg)]()
[![Coverage](https://img.shields.io/badge/coverage-50.3%25-yellow.svg)]()

> **Event-Driven Microservices Architecture** with Spring Cloud, Apache Kafka, and React Dashboard

A production-ready e-commerce platform demonstrating advanced microservices patterns, event-driven architecture, and comprehensive testing strategies.

---

## 🎯 Architecture Overview

**[Insérer votre diagramme d'architecture ici]**

### Core Services
- **Customer Service** - User management with domain-driven design
- **Product Service** - Product catalog with inventory management  
- **Order Service** - Order processing orchestration
- **Payment Service** - Payment processing integration
- **Notification Service** - Async email notifications via Kafka

### Infrastructure
- **API Gateway** - Spring Cloud Gateway with OAuth2 (Keycloak)
- **Service Discovery** - Netflix Eureka for dynamic service registration
- **Config Server** - Centralized configuration management
- **Distributed Tracing** - Zipkin for request tracing across services

---

## 🏗️ Technical Architecture

### 1. Domain-Driven Design (DDD)

**[Insérer diagramme DDD - Bounded Contexts]**

Each microservice is designed as an independent bounded context with:
- **Entities** - Core business objects with unique identity
- **Value Objects** - Immutable objects defined by attributes
- **Aggregates** - Consistency boundaries for business logic
- **Repositories** - Data access abstraction
- **Domain Services** - Business operations spanning multiple aggregates

**Example: Order Service**
```
Order Aggregate Root
├── Order (Entity)
├── OrderLine (Value Object)
├── Address (Value Object)
└── PaymentDetails (Value Object)
```

### 2. Event-Driven Architecture

**Communication Patterns:**

**Synchronous** (REST/HTTP)
- `Order → Product` - Stock verification via **FeignClient**
- `Order → Customer` - Customer validation via **RestTemplate**
- `Order → Payment` - Payment processing via **FeignClient**

**Asynchronous** (Kafka Events)
- `Order → Notification` - Order confirmation events
- `Payment → Notification` - Payment status events

**Benefits:**
- ✅ Service decoupling
- ✅ Scalability & resilience
- ✅ Non-blocking operations
- ✅ Event replay capability

---

## 🔧 Technology Stack

### Backend
| Layer | Technology |
|-------|-----------|
| **Framework** | Spring Boot 3.2, Spring Cloud 2023 |
| **Communication** | Apache Kafka, OpenFeign, RestTemplate |
| **Security** | Keycloak OAuth2, JWT, Spring Security |
| **Gateway** | Spring Cloud Gateway (WebFlux) |
| **Service Discovery** | Netflix Eureka |
| **Tracing** | Zipkin, Micrometer |
| **Database** | PostgreSQL, MongoDB |
| **Migration** | Flyway |
| **Validation** | Hibernate Validator |
| **Email** | JavaMailSender, Thymeleaf templates |

### Frontend & Testing
| Component | Technology |
|-----------|-----------|
| **Dashboard** | React 18, Vite, Tailwind CSS, Recharts |
| **Unit Tests** | JUnit 5, Mockito, AssertJ |
| **Integration** | Spring Boot Test, TestContainers |
| **API Tests** | REST Assured, Postman/Newman |
| **Performance** | Apache JMeter |
| **Coverage** | JaCoCo |
| **CI/CD** | GitHub Actions |

---

## 🚀 Key Features

### 1. API Gateway with Load Balancing
- Centralized routing with Spring Cloud Gateway
- Client-side load balancing via Spring Cloud LoadBalancer
- Dynamic service discovery through Eureka
- OAuth2 authentication with Keycloak

### 2. Kafka Event-Driven Communication
- **Topics:** `order-topic`, `payment-topic`, `notification-topic`
- **Patterns:** Event sourcing, CQRS, Saga orchestration
- **Serialization:** JSON with schema validation
- **Guaranteed delivery:** At-least-once semantics

### 3. Security & Authentication
- Centralized authentication with **Keycloak**
- JWT token validation at Gateway level
- Role-based access control (RBAC)
- Secure inter-service communication

### 4. Distributed Tracing
- **Zipkin** integration for request correlation
- **Trace ID** propagation across services
- Performance bottleneck identification
- End-to-end latency monitoring

### 5. Database Management
- **Flyway** versioned migrations
- PostgreSQL for transactional data
- MongoDB for notification history
- Schema evolution tracking

### 6. Resilience Patterns
- Circuit Breaker (Resilience4j - planned)
- Retry mechanisms with exponential backoff
- Timeout configurations
- Fallback responses

---

## 📊 Testing Strategy

**[Insérer capture du dashboard de tests]**

### Automated Testing Coverage

| Test Type | Count | Coverage | Status |
|-----------|-------|----------|--------|
| **Unit Tests** | 245+ | 50.3% | ✅ Passing |
| **Integration Tests** | 128+ | 72.5% | 🔄 In Progress |
| **API Tests** | 89+ | - | 🔄 In Progress |
| **E2E Tests** | 10+ | - | 📋 Planned |

**CI/CD Pipeline:**
- ✅ Automated unit tests on every push
- ✅ JaCoCo coverage reports
- ✅ GitHub Pages dashboard deployment
- 🔄 Postman/Newman API tests (in progress)
- 🔄 JMeter performance tests (in progress)

**Access Test Reports:** [GitHub Pages Dashboard](https://votre-username.github.io/my-microservices-app/)

---

## 🎨 React Testing Dashboard

Interactive dashboard for test results visualization:
- ✅ Real-time coverage metrics
- ✅ Service-level test breakdowns
- ✅ Historical trend analysis
- ✅ Beautiful charts with Recharts
- ✅ Responsive design with Tailwind CSS

---

## 🏃 Quick Start

### Prerequisites
```bash
Java 21+
Docker & Docker Compose
Node.js 20+ (for dashboard)
Maven 3.9+
```

### Run All Services
```bash
# Start infrastructure (Kafka, Zipkin, Keycloak, Databases)
docker-compose up -d

# Start Config Server
cd config-server && mvn spring-boot:run

# Start Eureka Server
cd discovery-server && mvn spring-boot:run

# Start Gateway
cd gateway && mvn spring-boot:run

# Start all microservices
cd services/customer-service && mvn spring-boot:run
cd services/product-service && mvn spring-boot:run
cd services/order-service && mvn spring-boot:run
cd services/payment-service && mvn spring-boot:run
cd services/notification-service && mvn spring-boot:run
```

### Access Points
- **API Gateway:** http://localhost:8222
- **Eureka Dashboard:** http://localhost:8761
- **Zipkin:** http://localhost:9411
- **Keycloak Admin:** http://localhost:8080 (admin/admin)
- **Test Dashboard:** http://localhost:5173 (dev mode)

---

## 📁 Project Structure

```
my-microservices-app/
├── .github/
│   └── workflows/          # CI/CD pipelines
├── config-server/          # Centralized configuration
├── discovery-server/       # Eureka service registry
├── gateway/                # API Gateway + Security
├── services/
│   ├── customer-service/   # User management
│   ├── product-service/    # Catalog management
│   ├── order-service/      # Order orchestration
│   ├── payment-service/    # Payment processing
│   └── notification-service/ # Async notifications
├── dashboard/              # React testing dashboard
├── test-data/              # Test results & metrics
└── docker-compose.yml      # Infrastructure setup
```

---

## 🎓 Learning Outcomes

This project demonstrates proficiency in:

✅ **Microservices Architecture** - Service decomposition, bounded contexts, inter-service communication  
✅ **Event-Driven Systems** - Kafka producers/consumers, async messaging, event sourcing  
✅ **Spring Ecosystem** - Boot, Cloud, Security, Data JPA, WebFlux  
✅ **API Design** - RESTful APIs, FeignClient, API Gateway patterns  
✅ **Security** - OAuth2/OIDC, JWT, Keycloak integration  
✅ **Distributed Systems** - Service discovery, load balancing, distributed tracing  
✅ **Database Management** - Flyway migrations, multi-database strategy  
✅ **Testing** - Unit, integration, API, and performance testing  
✅ **DevOps** - Docker, CI/CD with GitHub Actions  
✅ **Frontend** - React, modern UI development with Tailwind CSS

---

## 🚧 Roadmap

- [ ] Circuit Breaker with Resilience4j
- [ ] Redis caching layer
- [ ] Kafka Streams for real-time analytics
- [ ] GraphQL API Gateway
- [ ] Kubernetes deployment manifests
- [ ] Prometheus + Grafana monitoring
- [ ] End-to-End tests with Selenium

---

## 📝 Documentation

- [Architecture Decision Records](./docs/ADR.md)
- [API Documentation](./docs/API.md)
- [Testing Strategy](./docs/TESTING.md)
- [Deployment Guide](./docs/DEPLOYMENT.md)

---

## 👨‍💻 Author

**[Votre Nom]**  
🎓 Engineering Student | PFE Search  
💼 Seeking internship in Event-Driven Microservices & Full-Stack Development

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-blue)](https://linkedin.com/in/votre-profil)
[![Email](https://img.shields.io/badge/Email-Contact-red)](mailto:votre.email@example.com)

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

⭐ **If you find this project useful, please consider giving it a star!**
