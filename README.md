# Multi-Schema Insurance Quote Application — Backend API

A high-performance, event-driven Spring Boot 3 microservice architecture for managing multi-stage insurance quote applications. The system leverages PostgreSQL multi-schema partitioning, Redis caching, Kafka message streaming, and Flyway database migrations.

---

## 🔗 Sibling Repository & Integrated Local Setup

* **Frontend Sibling Repository:** [insurance-quote-frontend](https://github.com/misaelgaray/insurance-quote-ui)

### Running Both Repositories Together via Docker Compose
The primary `docker-compose.yml` file located in the root of this backend repository orchestrates the entire multi-container stack (Backend, Frontend, PostgreSQL, Redis, Kafka, and Kafbat UI).

Run by using `docker compose up -d --build`

1. Ensure both `insurance-quote-backend` and `insurance-quote-frontend` repositories are cloned into the same parent folder:
   ```text
   parent-folder/
   ├── insurance-quote-backend/   <-- You are here
   └── insurance-quote-frontend/

2. From the root directory of this backend repository, launch the full stack:
   ```text
   docker compose up -d --build

3. Verify container statuses:

* **Frontend Application: http://localhost:3000**

* **Backend REST API: http://localhost:8080/api/v1/quotes**

* **Kafka Management UI (Kafbat): http://localhost:8085**

## 🚀 Setup & Execution Instructions

### Prerequisites
* Java Development Kit: JDK 21

* Build Tool: Maven 3.9+

* Containerization: Docker Desktop & Docker Compose v2+

## 🧪 Running Tests
The application uses JUnit 5, Mockito, and Testcontainers for comprehensive integration and unit testing.
   ```text
   # Run all unit tests
   mvn test
   
   # Run specific test class
   mvn test -Dtest=QuoteServiceTest
   
   # Run integration tests with embedded containers
   mvn verify
   ```

## Problem Approach & Thought Process

Before writing any line of code, the architecture was planned around key enterprise domain characteristics of insurance platforms. The design was planed to decouple domains in order to be scalable in the future. There is a `client`package which contains a interface to communicate between domains. If architecture needs to be migrated to microservices, we only need to change the implementation to Feign or any Async communication:

1. Separation of Concerns across Business Domains: Catalog reference data, quote lifecycle management, and underwriting risk assessment naturally belong to distinct logical boundaries.

2. Strict Rule Validation Rules: Senior applicants (> 65 years old) require supplemental health risk evaluations before premium calculations can occur.

3. Resilience & Asynchronous Event Processing: Quote state changes (e.g., SUBMITTED, EXPIRED) need to trigger asynchronous processing (underwriting reviews, external notification delivery) without blocking the user thread.

Rather than building a single monolithic schema, the data layer was split into 3 dedicated PostgreSQL schemas, so in the future if the architecture needs to scale to a distributed architecture, it'll be easier to split domains into microservices:

1. catalogs: Product tiers (BASIC, STANDARD, PREMIUM) and coverage items.

2. quoting: Active quote drafts, applicant information, selected plans, and status transitions.

3. underwriting: Supplemental health questionnaire assessments for risk scoring.