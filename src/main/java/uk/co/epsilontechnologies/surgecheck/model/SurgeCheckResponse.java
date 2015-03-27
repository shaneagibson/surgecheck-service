package uk.co.epsilontechnologies.surgecheck.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.List;

public class SurgeCheckResponse {

    private final BigDecimal current;
    private final List<Metrics> historic;

    public SurgeCheckResponse(final BigDecimal current, final List<Metrics> historic) {
        this.current = current;
        this.historic = historic;
    }

    public BigDecimal getCurrent() {
        return current;
    }

    public List<Metrics> getHistoric() {
        return historic;
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
