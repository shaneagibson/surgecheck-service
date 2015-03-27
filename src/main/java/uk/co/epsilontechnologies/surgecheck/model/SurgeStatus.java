package uk.co.epsilontechnologies.surgecheck.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.Date;

public class SurgeStatus {

    private final Date timestamp;
    private final Coordinates coordinates;
    private final BigDecimal surgeMultiplier;

    public SurgeStatus(final Date timestamp, final Coordinates coordinates, final BigDecimal surgeMultiplier) {
        this.timestamp = timestamp;
        this.coordinates = coordinates;
        this.surgeMultiplier = surgeMultiplier;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public BigDecimal getSurgeMultiplier() {
        return surgeMultiplier;
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
