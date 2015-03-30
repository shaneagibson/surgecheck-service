package uk.co.epsilontechnologies.surgecheck.repository;

import org.springframework.stereotype.Component;
import uk.co.epsilontechnologies.surgecheck.model.Coordinates;

import java.math.BigDecimal;

@Component
public class SurgeStatusTableNameFormatter {

    public String format(final Coordinates coordinates) {
        return "surge_" + stringifyCoordinate(coordinates.getLatitude())+"_"+stringifyCoordinate(coordinates.getLongitude());
    }

    private String stringifyCoordinate(final BigDecimal coordinate) {
        String stringValue = String.valueOf(coordinate.movePointRight(2).abs());
        while (stringValue.length() < 4) {
            stringValue = "0" + stringValue;
        }
        return coordinate.signum() == -1 ? "n" + stringValue : stringValue;
    }

}
