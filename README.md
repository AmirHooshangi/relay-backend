# Relay Backend

A simple IoT data processing system. Simulates devices, processes events through Kafka, stores aggregations in PostgreSQL, and exposes a REST API.

## Quick Start

```bash
docker compose build
docker compose up -d
```

This starts:
- Kafka (port 9092)
- PostgreSQL with TimescaleDB (port 5432)
- Simulator (generates device events)
- Processor (aggregates events)
- API (port 8080)

View logs:
```bash
docker compose logs -f
```

Stop everything:
```bash
docker compose down
```

## API

All endpoints require a Bearer token: `supermetrics-api-token-2024`

### Examples

Get aggregations by device:
```bash
curl -H "Authorization: Bearer supermetrics-api-token-2024" \
  "http://localhost:8080/api/events/device/device-123?startTime=2024-01-01T00:00:00Z&endTime=2024-01-01T23:59:59Z"
```

Get aggregations by zone:
```bash
curl -H "Authorization: Bearer supermetrics-api-token-2024" \
  "http://localhost:8080/api/events/zone/zone-1?startTime=2024-01-01T00:00:00Z&endTime=2024-01-01T23:59:59Z"
```

Get aggregations by device type:
```bash
curl -H "Authorization: Bearer supermetrics-api-token-2024" \
  "http://localhost:8080/api/events/type/THERMOSTAT?startTime=2024-01-01T00:00:00Z&endTime=2024-01-01T23:59:59Z"
```

Get aggregations by zone and type:
```bash
curl -H "Authorization: Bearer supermetrics-api-token-2024" \
  "http://localhost:8080/api/events/zone/zone-1/type/THERMOSTAT?startTime=2024-01-01T00:00:00Z&endTime=2024-01-01T23:59:59Z"
```

Device types: `THERMOSTAT`, `HEART_RATE_METER`, `CAR_FUEL`

## Project Structure

- `relay-common` - Shared models
- `relay-simulator` - Device simulator
- `relay-processor` - Kafka Streams processor
- `relay-api` - REST API

See [ARCHITECTURE.md](ARCHITECTURE.md) for more details.

## Future Works

Top 7 things needed for production:

1. **Monitoring & Observability** - Add Prometheus metrics, distributed tracing (Jaeger/Zipkin), and structured logging. Set up alerts for error rates, latency, and Kafka lag.

2. **Error Handling & Resilience** - Implement dead letter queues for failed messages, retry policies with exponential backoff, circuit breakers for database calls, and graceful degradation when services are down.

3. **Security Hardening** - Replace static JWT token with proper OAuth2/OIDC flow, add refresh tokens, implement rate limiting, input validation/sanitization, and API key rotation.

4. **Scalability** - Deploy distributed Kafka cluster (3+ brokers), add database read replicas, implement connection pooling, and add horizontal scaling for API and processor services.

5. **Testing & Quality** - Add integration tests with Testcontainers, load testing to find bottlenecks, chaos engineering tests, and end-to-end tests for critical paths.

6. **CI/CD Pipeline** - Set up automated builds, tests, and deployments. Add staging environment, automated rollback capabilities, and blue-green deployments.

7. **Documentation & Operations** - Generate OpenAPI/Swagger docs, create runbooks for common issues, add deployment guides, and document disaster recovery procedures.
