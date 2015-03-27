package uk.co.epsilontechnologies.surgecheck.repository;

import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;
import uk.co.epsilontechnologies.surgecheck.model.Coordinates;
import uk.co.epsilontechnologies.surgecheck.model.SurgeStatus;

import java.math.BigDecimal;
import java.util.List;

import static com.datastax.driver.core.querybuilder.QueryBuilder.*;

@Repository
public class SurgeCheckDaoImpl implements SurgeCheckDao {

    private final CassandraOperations cassandraOperations;
    private final SurgeStatusRowMapper surgeStatusRowMapper;
    private final int timeToLive;

    @Autowired
    public SurgeCheckDaoImpl(
            final CassandraOperations cassandraOperations,
            final SurgeStatusRowMapper surgeStatusRowMapper,
            @Value("${surgecheck.ttl}") final int timeToLive) {
        this.cassandraOperations = cassandraOperations;
        this.surgeStatusRowMapper = surgeStatusRowMapper;
        this.timeToLive = timeToLive;
    }

    @Override
    public void persistSurgeStatus(final SurgeStatus surgeStatus) {
        final String tableName = resolveTableName(surgeStatus.getCoordinates());
        insertSurgeStatus(tableName, surgeStatus);
    }

    @Override
    public List<SurgeStatus> fetchSurgeStatus(final Coordinates coordinates) {
        final String tableName = resolveTableName(coordinates);
        final Select select = select("timestamp", "latitude", "longitude", "surge_multiplier").from(tableName);
        select.setConsistencyLevel(ConsistencyLevel.ONE);
        return cassandraOperations.query(select, surgeStatusRowMapper);
    }

    private String resolveTableName(final Coordinates coordinates) {
        return "surge_" + stringifyCoordinate(coordinates.getLatitude())+"_"+stringifyCoordinate(coordinates.getLongitude());
    }

    private void insertSurgeStatus(final String tableName, final SurgeStatus surgeStatus) {
        final Insert insert = insertInto(tableName);
        insert.setConsistencyLevel(ConsistencyLevel.ONE);
        insert.value("latitude", surgeStatus.getCoordinates().getLatitude());
        insert.value("longitude", surgeStatus.getCoordinates().getLongitude());
        insert.value("surge_multiplier", surgeStatus.getSurgeMultiplier());
        insert.value("timestamp", surgeStatus.getTimestamp().getTime());
        insert.using(QueryBuilder.ttl(timeToLive));
        cassandraOperations.execute(insert);
    }

    private String stringifyCoordinate(final BigDecimal coordinate) {
        String stringValue = String.valueOf(coordinate.movePointRight(2).abs());
        while (stringValue.length() < 4) {
            stringValue = "0" + stringValue;
        }
        return coordinate.signum() == -1 ? "n" + stringValue : stringValue;
    }

}