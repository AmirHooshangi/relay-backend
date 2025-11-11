-- Fix device_type values to use enum names instead of getName() values
-- This aligns with the @Enumerated(EnumType.STRING) annotation which expects enum names

UPDATE device_event_aggregations 
SET device_type = 'THERMOSTAT' 
WHERE device_type = 'thermostat';

UPDATE device_event_aggregations 
SET device_type = 'HEART_RATE_METER' 
WHERE device_type = 'heart-rate-meter';

UPDATE device_event_aggregations 
SET device_type = 'CAR_FUEL' 
WHERE device_type = 'car-fuel';

