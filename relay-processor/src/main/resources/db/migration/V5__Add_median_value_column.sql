-- Add median_value column to device_event_aggregations table
ALTER TABLE device_event_aggregations 
ADD COLUMN IF NOT EXISTS median_value DOUBLE PRECISION;

-- Update existing rows to have a default median value (can be calculated later if needed)
-- For now, set to NULL to indicate it needs to be calculated
UPDATE device_event_aggregations 
SET median_value = NULL 
WHERE median_value IS NULL;

