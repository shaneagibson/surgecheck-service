package uk.co.epsilontechnologies.surgecheck.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Grid {

    private final Coordinates southWest;
    private final Coordinates northEast;

    public Grid(
            final Coordinates southWest,
            final Coordinates northEast) {
        this.southWest = southWest;
        this.northEast = northEast;
    }

    public List<Coordinates> getAllCoordinates() {
        final List<Coordinates> result = new ArrayList<>();
        BigDecimal latitude = southWest.getLatitude();
        while (latitude.compareTo(northEast.getLatitude()) == -1) {
            latitude = latitude.add(new BigDecimal("0.01"));
            BigDecimal longitude = southWest.getLongitude();
            while (longitude.compareTo(northEast.getLongitude()) == -1) {
                longitude = longitude.add(new BigDecimal("0.01"));
                result.add(new Coordinates(latitude, longitude));
            }
        }
        return result;
    }

    public boolean contains(final Coordinates coordinates) {
        return (coordinates.getLatitude().compareTo(southWest.getLatitude()) == -1 &&
            coordinates.getLatitude().compareTo(northEast.getLatitude()) == 1 &&
            coordinates.getLongitude().compareTo(southWest.getLongitude()) == -1 &&
            coordinates.getLongitude().compareTo(northEast.getLongitude()) == 1);
    }

    @Override
    public boolean equals(final Object o) {
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