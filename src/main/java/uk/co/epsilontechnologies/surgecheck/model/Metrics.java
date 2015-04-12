package uk.co.epsilontechnologies.surgecheck.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;

public class Metrics {

    private final long timestamp;
    private final BigDecimal high;
    private final BigDecimal mid;
    private final BigDecimal low;
    private final int count;

    public Metrics(final long timestamp, final BigDecimal high, final BigDecimal mid, final BigDecimal low, final int count) {
        this.timestamp = timestamp;
        this.high = high;
        this.mid = mid;
        this.low = low;
        this.count = count;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public BigDecimal getMid() {
        return mid;
    }

    public BigDecimal getLow() {
        return low;
    }

    public int getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}