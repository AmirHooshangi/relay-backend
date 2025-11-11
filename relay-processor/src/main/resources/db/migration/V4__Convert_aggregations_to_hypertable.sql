-- Convert device_event_aggregations table to TimescaleDB hypertable
-- This enables automatic time-based partitioning for better query performance
-- Partitioning by time_window_start since all queries filter by time ranges
-- 
-- TimescaleDB requires that unique constraints (including primary keys) must include
-- the partitioning column. We need to drop the existing primary key and create
-- a composite primary key that includes time_window_start.

-- Drop the existing primary key constraint
ALTER TABLE device_event_aggregations DROP CONSTRAINT IF EXISTS device_event_aggregations_pkey;

-- Create composite primary key that includes the partitioning column
ALTER TABLE device_event_aggregations 
ADD PRIMARY KEY (id, time_window_start);

-- Now create the hypertable
SELECT create_hypertable(
    'device_event_aggregations', 
    'time_window_start',
    if_not_exists => TRUE,
    chunk_time_interval => INTERVAL '7 days'
);

