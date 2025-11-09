# IoT Data Processing System - Relay Backend

This is a backend system for processing continuous IoT device data streams. The system simulates IoT devices, processes their readings through Kafka Streams, stores data in PostgreSQL, and provides a secure REST API for querying aggregated readings.

## Architecture

For detailed architectural decisions, technology stack choices, and trade-offs, please see [ARCHITECTURE.md](ARCHITECTURE.md).

**Note**: The architecture document contains Mermaid diagrams. To view these diagrams properly, you'll need a Mermaid viewer:
- **VS Code**: Install the "Markdown Preview Mermaid Support" extension
- **GitHub/GitLab**: Diagrams render automatically in markdown files
- **Online**: Use [Mermaid Live Editor](https://mermaid.live/)
- **IntelliJ IDEA**: Install the "Mermaid" plugin

## Project Structure

This is a multi-module Maven project. For details on module structure and package naming, see [MODULE_STRUCTURE.md](MODULE_STRUCTURE.md).

### Modules

- **relay-common**: Shared domain models and DTOs
- **relay-simulator**: IoT device simulator that generates and publishes readings
- **relay-processor**: Kafka Streams processor for data aggregation
- **relay-api**: Spring Boot REST API for querying readings

## Prerequisites

- Java 25
- Maven 3.8+
- Docker and Docker Compose

## Quick Start

### Using Docker Compose

The easiest way to run the entire system:

```bash
docker-compose up -d
```

This starts:
- Zookeeper (port 2181)
- Kafka (port 9092)
- PostgreSQL with TimescaleDB (port 5432)
- **simulator**: IoT device simulator (generates and publishes readings to Kafka)
- **processor**: Kafka Streams processor (consumes from Kafka, processes and stores data)
- **api**: Spring Boot REST API (port 8080) for querying readings

To stop everything:

```bash
docker-compose down
```

### Manual Setup

1. Start Kafka and PostgreSQL (or use Docker Compose for these services)
2. Build all modules:
   ```bash
   mvn clean install
   ```
3. Run each service independently:
   
   **Simulator** (in a separate terminal):
   ```bash
   cd relay-simulator
   java -jar target/relay-simulator-1.0-SNAPSHOT.jar
   ```
   
   **Processor** (in a separate terminal):
   ```bash
   cd relay-processor
   mvn spring-boot:run
   ```
   
   **API** (in a separate terminal):
   ```bash
   cd relay-api
   mvn spring-boot:run
   ```

## Configuration

Each service has its own configuration:

- **Simulator**: Configure Kafka bootstrap servers via environment variable `KAFKA_BOOTSTRAP_SERVERS`
- **Processor**: Configuration in `relay-processor/src/main/resources/application.yml`
- **API**: Configuration in `relay-api/src/main/resources/application.yml`

Update the following for your environment:
- Kafka bootstrap servers
- PostgreSQL connection details
- Server port (API only)

## Testing

Run tests for all modules:

```bash
mvn test
```

Run tests for a specific module:

```bash
cd relay-api
mvn test
```

## Building

Build the executable JAR:

```bash
mvn clean package
```

The executable JAR will be in `relay-api/target/relay-api-1.0-SNAPSHOT.jar`

Run it with:

```bash
java -jar relay-api/target/relay-api-1.0-SNAPSHOT.jar
```

