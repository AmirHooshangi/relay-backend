-- Create device_events table
-- Note: For TimescaleDB hypertables, the partitioning column must be part of the primary key
CREATE TABLE IF NOT EXISTS device_events (
    id BIGSERIAL,
    device_id VARCHAR(255) NOT NULL,
    device_type VARCHAR(50) NOT NULL,
    zone VARCHAR(100) NOT NULL,
    value DOUBLE PRECISION NOT NULL,
    timestamp TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (id, timestamp)
);

-- Convert to TimescaleDB hypertable for time-series optimization
-- Must be done before creating indexes to avoid conflicts
SELECT create_hypertable('device_events', 'timestamp', if_not_exists => TRUE);

-- Create indexes for efficient querying
CREATE INDEX IF NOT EXISTS idx_device_events_device_id ON device_events (device_id);
CREATE INDEX IF NOT EXISTS idx_device_events_device_type ON device_events (device_type);
CREATE INDEX IF NOT EXISTS idx_device_events_zone ON device_events (zone);
CREATE INDEX IF NOT EXISTS idx_device_events_zone_type ON device_events (zone, device_type);
CREATE INDEX IF NOT EXISTS idx_device_events_time_zone_type ON device_events (timestamp DESC, zone, device_type);

-- Create aggregations table
CREATE TABLE IF NOT EXISTS device_event_aggregations (
    id BIGSERIAL PRIMARY KEY,
    device_id VARCHAR(255),
    device_type VARCHAR(50),
    zone VARCHAR(100),
    time_window_start TIMESTAMPTZ NOT NULL,
    time_window_end TIMESTAMPTZ NOT NULL,
    window_type VARCHAR(20) NOT NULL,
    avg_value DOUBLE PRECISION NOT NULL,
    min_value DOUBLE PRECISION NOT NULL,
    max_value DOUBLE PRECISION NOT NULL,
    count BIGINT NOT NULL
);

-- Create indexes for aggregations
CREATE INDEX IF NOT EXISTS idx_agg_time_window ON device_event_aggregations (time_window_start, time_window_end);
CREATE INDEX IF NOT EXISTS idx_agg_zone_type ON device_event_aggregations (zone, device_type);
CREATE INDEX IF NOT EXISTS idx_agg_device_id ON device_event_aggregations (device_id);

