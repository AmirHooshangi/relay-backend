# Relay Simulator

IoT Device Simulator that generates and publishes device events to Kafka.

## Features

- Simulates at least 3 distinct IoT devices (Thermostat, Heart Rate Meter, Car Fuel)
- Each device sends a new event every second
- Publishes events to Kafka topic `iot-events` as JSON messages
- Configurable via environment variables or system properties

## Running the Simulator

### Prerequisites

- Java 25
- Maven 3.8+
- Kafka running (via Docker Compose or standalone)

### Using Docker Compose

The simulator is included in the main `docker-compose.yml`. Start it with:

```bash
docker-compose up simulator
```

Or start all services:

```bash
docker-compose up -d
```

### Running Manually

1. Build the project:
```bash
mvn clean install
```

2. Run the simulator:
```bash
java -jar relay-simulator/target/relay-simulator-1.0-SNAPSHOT.jar
```

Or with custom Kafka bootstrap servers:
```bash
java -DKAFKA_BOOTSTRAP_SERVERS=localhost:9092 -jar relay-simulator/target/relay-simulator-1.0-SNAPSHOT.jar
```

Or using environment variable:
```bash
KAFKA_BOOTSTRAP_SERVERS=localhost:9092 java -jar relay-simulator/target/relay-simulator-1.0-SNAPSHOT.jar
```

## Verifying with Kafka Console Consumer

### Start Kafka Console Consumer

In a separate terminal, start the Kafka console consumer to see the messages:

```bash
# If using Docker Compose
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic iot-events \
  --from-beginning

# Or if Kafka is running locally
kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic iot-events \
  --from-beginning
```

### Expected Output

You should see JSON messages like:

```json
{"deviceId":"thermostat-001","deviceType":"THERMOSTAT","value":22.5,"timestamp":"2024-01-01T12:00:00Z"}
{"deviceId":"heart-rate-001","deviceType":"HEART_RATE_METER","value":75.2,"timestamp":"2024-01-01T12:00:01Z"}
{"deviceId":"car-fuel-001","deviceType":"CAR_FUEL","value":45.8,"timestamp":"2024-01-01T12:00:02Z"}
```

Messages will appear every second from each device.

## Configuration

The simulator can be configured via:

1. **Environment variable**: `KAFKA_BOOTSTRAP_SERVERS` (default: `localhost:9092`)
2. **System property**: `kafka.bootstrap.servers` (default: `localhost:9092`)

## Testing

### Run Unit Tests

```bash
mvn test
```

### Run Integration Tests

Integration tests use Testcontainers to spin up a real Kafka instance:

```bash
mvn test
```

Note: Integration tests require Docker to be running.

## Architecture

- **DeviceSimulator**: Manages multiple devices and schedules events
- **KafkaEventProducer**: Publishes events to Kafka as JSON
- **Device**: Represents an IoT device with type and zone
- **DeviceType**: Enum defining device types with value ranges

## Message Format

Messages are published to the `iot-events` topic with:
- **Key**: Device ID (for partitioning)
- **Value**: JSON string containing:
  - `deviceId`: String
  - `deviceType`: Device type enum value
  - `value`: Double
  - `timestamp`: ISO-8601 timestamp

