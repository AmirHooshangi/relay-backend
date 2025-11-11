package com.supermetrics.relay.processor.stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AggregationValue {
    private double sum;
    private double min;
    private double max;
    private long count;
    private List<Double> values;
    
    public AggregationValue() {
        this.sum = 0.0;
        this.min = Double.MAX_VALUE;
        this.max = Double.MIN_VALUE;
        this.count = 0;
        this.values = new ArrayList<>();
    }
    
    public AggregationValue(double sum, double min, double max, long count) {
        this.sum = sum;
        this.min = min;
        this.max = max;
        this.count = count;
        this.values = new ArrayList<>();
    }
    
    public void add(double value) {
        this.sum += value;
        this.min = Math.min(this.min, value);
        this.max = Math.max(this.max, value);
        this.count++;
        this.values.add(value);
    }
    
    public void merge(AggregationValue other) {
        this.sum += other.sum;
        this.min = Math.min(this.min, other.min);
        this.max = Math.max(this.max, other.max);
        this.count += other.count;
        this.values.addAll(other.values);
    }
    
    public double getSum() {
        return sum;
    }
    
    public void setSum(double sum) {
        this.sum = sum;
    }
    
    public double getMin() {
        return min == Double.MAX_VALUE ? 0.0 : min;
    }
    
    public void setMin(double min) {
        this.min = min;
    }
    
    public double getMax() {
        return max == Double.MIN_VALUE ? 0.0 : max;
    }
    
    public void setMax(double max) {
        this.max = max;
    }
    
    public long getCount() {
        return count;
    }
    
    public void setCount(long count) {
        this.count = count;
    }
    
    @JsonIgnore
    public double getAverage() {
        return count > 0 ? sum / count : 0.0;
    }
    
    @JsonIgnore
    public double getMedian() {
        if (values == null || values.isEmpty()) {
            return 0.0;
        }
        List<Double> sortedValues = new ArrayList<>(values);
        Collections.sort(sortedValues);
        int size = sortedValues.size();
        if (size % 2 == 0) {
            return (sortedValues.get(size / 2 - 1) + sortedValues.get(size / 2)) / 2.0;
        }
        return sortedValues.get(size / 2);
    }
    
    public List<Double> getValues() {
        return values != null ? new ArrayList<>(values) : new ArrayList<>();
    }
    
    public void setValues(List<Double> values) {
        this.values = values != null ? new ArrayList<>(values) : new ArrayList<>();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregationValue that = (AggregationValue) o;
        return Double.compare(that.sum, sum) == 0 &&
               Double.compare(that.min, min) == 0 &&
               Double.compare(that.max, max) == 0 &&
               count == that.count &&
               Objects.equals(values, that.values);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(sum, min, max, count, values);
    }
    
    @Override
    public String toString() {
        return String.format("AggregationValue{sum=%.2f, min=%.2f, max=%.2f, count=%d, avg=%.2f, median=%.2f}",
            sum, getMin(), getMax(), count, getAverage(), getMedian());
    }
}

