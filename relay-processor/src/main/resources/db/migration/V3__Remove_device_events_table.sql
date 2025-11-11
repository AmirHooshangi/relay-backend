

-- Drop indexes first
DROP INDEX IF EXISTS idx_device_events_time_zone_type;
DROP INDEX IF EXISTS idx_device_events_zone_type;
DROP INDEX IF EXISTS idx_device_events_zone;
DROP INDEX IF EXISTS idx_device_events_device_type;
DROP INDEX IF EXISTS idx_device_events_device_id;


DROP TABLE IF EXISTS device_events CASCADE;

