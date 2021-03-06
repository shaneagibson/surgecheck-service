package uk.co.epsilontechnologies.surgecheck.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Coordinates {

    private final BigDecimal latitude;
    private final BigDecimal longitude;

    public Coordinates(final BigDecimal latitude, final BigDecimal longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Coordinates scale() {
        return new Coordinates(
                this.latitude.setScale(2, RoundingMode.HALF_UP),
                this.longitude.setScale(2, RoundingMode.HALF_UP));
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return latitude + "," + longitude;
    }

    @Override
    public boolean equals(final Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}