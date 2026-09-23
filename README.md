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