package com.supermetrics.relay.processor.stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AggregationValueTest {

    private AggregationValue aggregationValue;

    @BeforeEach
    void setUp() {
        aggregationValue = new AggregationValue();
    }

    @Test
    void addValues() {
        aggregationValue.add(10.0);
        aggregationValue.add(20.0);
        aggregationValue.add(30.0);

        assertEquals(60.0, aggregationValue.getSum());
        assertEquals(10.0, aggregationValue.getMin());
        assertEquals(30.0, aggregationValue.getMax());
        assertEquals(3L, aggregationValue.getCount());
    }

    @Test
    void calculateAverage() {
        aggregationValue.add(10.0);
        aggregationValue.add(20.0);
        aggregationValue.add(30.0);

        assertEquals(20.0, aggregationValue.getAverage());
    }

    @Test
    void calculateMedian() {
        aggregationValue.add(10.0);
        aggregationValue.add(20.0);
        aggregationValue.add(30.0);

        assertEquals(20.0, aggregationValue.getMedian());
    }

    @Test
    void mergeAggregations() {
        aggregationValue.add(10.0);
        aggregationValue.add(20.0);

        AggregationValue other = new AggregationValue();
        other.add(30.0);
        other.add(40.0);

        aggregationValue.merge(other);

        assertEquals(100.0, aggregationValue.getSum());
        assertEquals(10.0, aggregationValue.getMin());
        assertEquals(40.0, aggregationValue.getMax());
        assertEquals(4L, aggregationValue.getCount());
    }
}
