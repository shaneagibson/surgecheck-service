package uk.co.epsilontechnologies.surgecheck.repository;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.DriverException;
import org.springframework.cassandra.core.RowMapper;
import org.springframework.stereotype.Component;
import uk.co.epsilontechnologies.surgecheck.model.Coordinates;
import uk.co.epsilontechnologies.surgecheck.model.SurgeStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Component
public class SurgeStatusRowMapper implements RowMapper<SurgeStatus> {

    @Override
    public SurgeStatus mapRow(final Row row, final int i) throws DriverException {
        return new SurgeStatus(
                new Date(row.getLong("timestamp")),
                new Coordinates(
                        row.getDecimal("latitude"),
                        row.getDecimal("longitude")),
                new BigDecimal(row.getFloat("surge_multiplier")).setScale(1, RoundingMode.HALF_UP));
    }

}