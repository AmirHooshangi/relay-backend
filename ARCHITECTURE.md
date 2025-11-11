# Architecture

## Overview

Event-driven stream processing. Devices send events to Kafka, a processor aggregates them hourly, and a REST API serves queries. Simple and scalable.

## Components

**Simulator** - Generates IoT device events and publishes to Kafka every second.

**Processor** - Kafka Streams app that:
- Consumes events from Kafka
- Groups by device/zone/type
- Aggregates into 1-hour windows
- Stores results in PostgreSQL

**API** - Spring Boot REST API that queries PostgreSQL aggregations.

## Tech Choices

**Kafka** - Industry standard for event streaming. Handles high throughput and keeps messages around for replay.

**Kafka Streams** - Runs in-process, no separate cluster. Built-in state stores and exactly-once processing.

**PostgreSQL + TimescaleDB** - SQL is familiar, Spring Data JPA works well, and TimescaleDB gives us time-series optimizations. Could migrate to InfluxDB later if needed, but PostgreSQL is fine for now.

**Spring Boot** - Standard Java framework. Good ecosystem and documentation.

**JWT** - Stateless auth for the REST API. Spring Security handles it.

## Data Flow

```
Simulator → Kafka → Processor → PostgreSQL
                              ↑
                            API
```

1. Simulator publishes device events to Kafka topic `iot-events`
2. Processor consumes events, groups by device/zone/type, aggregates hourly
3. Aggregations stored in PostgreSQL with window start/end times
4. API queries PostgreSQL and returns aggregated results

## Trade-offs

**Kafka complexity** - More setup than RabbitMQ, but built for streaming. Worth it.

**PostgreSQL vs specialized time-series DB** - InfluxDB might be faster, but PostgreSQL is simpler and works fine. Can migrate later if needed.

**Spring Boot overhead** - Slightly slower startup, but the ecosystem makes it worth it.

## Limitations

This is a prototype:
- Single-node Kafka (not distributed)
- Basic JWT (no refresh tokens)
- No monitoring/observability
- Basic error handling

For production, you'd want distributed Kafka, proper monitoring, and better error handling.
