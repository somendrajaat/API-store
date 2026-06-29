# API Store

An open-source platform for publishing, subscribing to, and securely consuming APIs through a unified API gateway.

> **All APIs. One Place.**
>
> Publish APIs, subscribe securely, and consume them through a unified API gateway—without relying on a centrally curated marketplace.

---

## Overview

Modern applications rely on APIs for everything from AI models and payment gateways to weather services and communication platforms. As the number of providers grows, managing subscriptions, authentication, API keys, and usage becomes increasingly fragmented.

API Store sits between API providers and API consumers, providing a unified platform for publishing, subscribing to, and consuming APIs through a single gateway.

Unlike traditional API marketplaces, API Store follows a community-first approach rather than a centrally curated one. Developers are free to publish APIs without requiring approval from a central authority. Future versions will introduce community-driven discovery, reputation, and rating systems to help surface high-quality APIs.

The platform handles authentication, API key management, request routing, rate limiting, and usage tracking, allowing developers to focus on building and consuming APIs instead of managing the surrounding infrastructure.

---

## Why API Store?

Managing APIs shouldn't require managing multiple dashboards, authentication mechanisms, subscriptions, and API keys across different providers.

API Store provides a consistent platform for both API providers and API consumers.

For providers, it offers a straightforward way to publish and manage APIs.

For consumers, it provides a unified experience for subscribing to APIs and accessing them through a secure gateway.

The platform is designed to be self-hostable, extensible, and production-ready, making it suitable as the foundation for an API marketplace or an internal API management platform.

---

## Why I Built This

Commercial API marketplaces already exist and solve many real-world problems. The goal of API Store is different.

I wanted to build the backend infrastructure that powers an API marketplace from scratch. This project focuses on the engineering challenges behind authentication, API publishing, subscriptions, API key management, gateway routing, rate limiting, and usage analytics while keeping the architecture modular and easy to understand.

---

## Features

### For API Providers

* Publish APIs
* Update API metadata
* Ownership validation
* Usage analytics

### For API Consumers

* Browse available APIs
* Subscribe to APIs
* Secure API key generation
* Unified gateway access

### Platform Features

* JWT authentication
* BCrypt password hashing
* API Gateway
* Redis-based rate limiting
* Usage tracking
* Flyway database migrations
* Docker deployment
* Health monitoring
* Global exception handling

---

## REST API

### Authentication

| Method | Endpoint       | Description         |
| ------ | -------------- | ------------------- |
| POST   | /auth/register | Register a new user |
| POST   | /auth/login    | Authenticate user   |

---

### APIs

| Method | Endpoint                | Description              |
|--------|-------------------------|--------------------------|
| GET    | /apis                   | List all APIs of user    |
| GET    | /apis/all               | List all APIs            |
| POST   | /apis                   | Publish an API           |
| GET    | /apis/me/subscription   | View subscribed APIs     |
| POST   | /apis/{id}/subscription | Subscribe to an API      |
| DELETE | /subscriptions/{id}     | Cancel subscription      |
| GET    | /apis/{apiId}/usage     | View API usage analytics |

---

### Gateway

```
/gateway/{apiId}/**
```

The gateway validates the consumer's API key, enforces rate limits, forwards the request to the target API, records usage statistics, and returns the provider's response.

---

### Request Flow

1. User authenticates using JWT.
2. Provider publishes an API.
3. Consumer subscribes to the API.
4. A unique API key is generated and securely stored.
5. Consumer sends requests through the gateway.
6. API key is validated.
7. Rate limit is enforced.
8. Request is forwarded to the target provider.
9. Usage statistics are recorded.
10. Provider response is returned to the consumer.

---

## Engineering Decisions

### Why JWT?

JWT enables stateless authentication, making the platform easier to scale without maintaining server-side sessions.

### Why Redis?

Redis provides extremely fast in-memory operations, making it ideal for implementing request counting and rate limiting.

### Why Flyway?

Database schema changes are version-controlled, ensuring every deployment uses the same database structure.

### Why Hash API Keys?

API keys are treated like passwords. They are never stored in plain text, reducing the impact of a potential database compromise.

### Why an API Gateway?

A gateway provides a single entry point for all API requests, allowing authentication, rate limiting, request forwarding, and usage tracking to be handled consistently.

### Why Docker?

Docker provides a reproducible deployment environment so the entire platform can run consistently across development and production environments.

---

## Performance Testing

The API Gateway was load tested using Apache JMeter to evaluate stability and throughput under concurrent traffic.

### Test Environment

* Apache JMeter
* Spring Boot 3
* PostgreSQL
* Redis
* Docker
* JVM: G1GC (`-Xms512m -Xmx512m`)

### Workload

* 100 concurrent users
* 2,000 total requests
* Gateway endpoint proxying JSONPlaceholder

### Results

* Successfully processed 2,000 requests
* 0% application errors
* Stable CPU and memory usage during testing
* JVM remained healthy with only Young Generation GC events
* No Full GC or OutOfMemoryError observed

### Observations

* End-to-end latency was primarily affected by the external API being proxied.
* JVM garbage collection accounted for only a small fraction of total execution time.
* HikariCP tuning had minimal impact because database access was not the primary bottleneck.

---

## Roadmap

### Near Future

* Community ratings and reviews
* API search
* Categories and tags
* Provider profiles

### Long Term

* API versioning
* Billing and monetization
* Multi-instance API Gateway
* Distributed rate limiting
* Response caching
* Provider dashboard
* Web frontend

---

## Contributing

Contributions are welcome.

If you discover a bug, have an improvement, or would like to add a feature, feel free to open an issue or submit a pull request.

Please keep contributions focused, well documented, and aligned with the project's architecture.

---

## License

This project is licensed under the MIT License.
