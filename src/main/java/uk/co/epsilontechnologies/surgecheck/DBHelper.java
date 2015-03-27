package uk.co.epsilontechnologies.surgecheck;

import uk.co.epsilontechnologies.surgecheck.model.Coordinates;
import uk.co.epsilontechnologies.surgecheck.model.Grid;

import java.math.BigDecimal;
import java.util.List;

public class DBHelper {

    private static final BigDecimal SW_LATITUDE = new BigDecimal("51.44");
    private static final BigDecimal SW_LONGITUDE = new BigDecimal("-0.33");
    private static final BigDecimal NE_LATITUDE = new BigDecimal("51.63");
    private static final BigDecimal NE_LONGITUDE = new BigDecimal("-0.09");

    public static void main(String... args) {

        final Coordinates southWest = new Coordinates(SW_LATITUDE, SW_LONGITUDE);
        final Coordinates northEast = new Coordinates(NE_LATITUDE, NE_LONGITUDE);

        final Grid grid = new Grid(southWest, northEast);

        final List<Coordinates> all = grid.getAllCoordinates();

        for (final Coordinates coordinates : all) {
            System.out.println("CREATE TABLE epsilon.surge_"+stringifyCoordinate(coordinates.getLatitude())+"_"+stringifyCoordinate(coordinates.getLongitude())+" (" +
                    "timestamp bigint PRIMARY KEY, " +
                    "latitude decimal," +
                    "longitude decimal," +
                    "surge_multiplier decimal" +
                ");");
            System.out.println();
        }
    }

    private static String stringifyCoordinate(final BigDecimal coordinate) {
        String stringValue = String.valueOf(coordinate.movePointRight(2).abs());
        while (stringValue.length() < 4) {
            stringValue = "0" + stringValue;
        }
        return coordinate.signum() == -1 ? "n" + stringValue : stringValue;
    }

}