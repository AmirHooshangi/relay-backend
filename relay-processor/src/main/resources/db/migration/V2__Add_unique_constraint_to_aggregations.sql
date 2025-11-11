-- Add unique constraint to prevent duplicate aggregations for the same window
-- This ensures data integrity and allows efficient upsert operations
CREATE UNIQUE INDEX IF NOT EXISTS idx_agg_unique_window ON device_event_aggregations 
    (device_id, zone, device_type, time_window_start, time_window_end, window_type);

