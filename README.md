# 🏸 BadmintonGo - Professional Badminton Pickup & Matchmaking Platform

### ⭐️ Project Vision (The Inspiration)
As a passionate badminton player, I identified two major pain points in the current pickup (open play) ecosystem:

1. **Information Fragmentation**: Players must join countless social media groups to find available court sessions.
2. **High Coordination Overhead**: Registration and waitlists are managed manually by organizers, which is time-consuming and prone to human error.

**BadmintonGo** is designed to be a "one-stop" integrated web platform that achieves **automated registration, intelligent waitlist management, and instant notifications**. It allows organizers to focus on the game while the system handles the administrative complexity.

---

### 🚀 Core Design Philosophy
- **Zero Friction**: Aggregate all court information into a single portal, allowing players to find and join matches with one click.
- **Automated Workflow**: Eliminate the need for manual confirmation. When a player cancels, the system automatically promotes the next person on the waitlist.
- **High Concurrency Assurance**: Optimized for "flash-sale" registration scenarios to ensure absolute data consistency without overbooking.

---

### 🏗️ System Architecture

This project utilizes a **Monorepo** structure, demonstrating proficiency in full-stack development and microservices governance.

#### **Backend Tech Stack**
- **Java 21**: Core language utilizing **Virtual Threads (Project Loom)** to optimize high-concurrency I/O performance.
- **Spring Boot 3.4**: Core application framework.
- **Redis (Redisson)**: 
  - **Distributed Locking**: For managing concurrent resource competition.
  - **Lua Scripting**: To achieve atomic inventory deduction and prevent overbooking.
  - **ZSet (Sorted Set)**: To implement a fair and efficient "Automated Waitlist System."
- **PostgreSQL**: Primary persistent data store.
- **Flyway**: Database version control to ensure consistent and traceable migrations.

#### **Frontend Tech Stack**
- **React / Next.js**: Responsive and interactive user interface.
- **Tailwind CSS**: Modern UI styling and layout.

---

### 🛠️ Key Technical Implementations

1. **Atomic Registration System**:
   Combines Redis Lua scripts for pre-deducting slots at the memory level. This significantly reduces database load and ensures data integrity under heavy traffic, validated via JMeter concurrency testing.
2. **Intelligent Waitlist & Auto-Promotion**:
   When a confirmed player cancels, the system triggers a background service that identifies the next person based on registration timestamps (ZSet Score), promoting them to "confirmed" status in milliseconds.
3. **Microservices Data Decoupling**:
   Implements a strategic **denormalization** approach between "Court Service" and "Pickup Service" to maximize query performance while maintaining eventual consistency through application logic.

---

### How to start the backend service

1. **Docker**:
   start:"docker-compose up -d"
   shutdown:"docker-compose down -v"
---

### 📂 Project Structure

```text
BadmintonGo/
├── badmintongo-backend/                # Parent Maven Project (Multi-module)
│   ├── gateway-service [8080]          # API Gateway: Routing, Rate Limiting & Security
│   ├── auth-service    [8081]          # Auth Service: Identity Management & JWT Provider
│   ├── court-service   [8082]          # Venue Service: Court Info & S3/MinIO Assets
│   ├── session-service [8083]          # Session Service: Matchmaking & Redis-based Waitlist
│   ├── registration-service [8084]     # Enrollment Service: Player Registration & Redis-based Waitlist
│   ├── notification-service [8085]     # Notify Service: Async Messaging (RabbitMQ/Email)
│   ├── common-service                  # Common Module: Shared POJOs, DTOs & Utils
│   └── docker-compose.yml              # Infra Orchestration (Postgres, Redis, RabbitMQ)
├── badmintongo-frontend    [3000]      # Next.js Application: Responsive Web Interface
└── tests/jmeter/                       # Performance Suite: Stress & Concurrency Tests