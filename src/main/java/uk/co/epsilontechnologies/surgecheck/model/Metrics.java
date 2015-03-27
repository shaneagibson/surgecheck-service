package uk.co.epsilontechnologies.surgecheck.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;

public class Metrics {

    private final long timestamp;
    private final BigDecimal high;
    private final BigDecimal avg;
    private final BigDecimal low;

    public Metrics(final long timestamp, final BigDecimal high, final BigDecimal avg, final BigDecimal low) {
        this.timestamp = timestamp;
        this.high = high;
        this.avg = avg;
        this.low = low;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public BigDecimal getAvg() {
        return avg;
    }

    public BigDecimal getLow() {
        return low;
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
