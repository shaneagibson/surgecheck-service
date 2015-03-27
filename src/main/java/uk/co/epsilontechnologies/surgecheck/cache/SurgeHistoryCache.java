package uk.co.epsilontechnologies.surgecheck.cache;

import org.springframework.stereotype.Component;
import uk.co.epsilontechnologies.surgecheck.model.CoordinateMetrics;
import uk.co.epsilontechnologies.surgecheck.model.Coordinates;
import uk.co.epsilontechnologies.surgecheck.model.Metrics;

import java.util.List;

@Component
public class SurgeHistoryCache {

    private final CoordinateMetrics coordinateMetrics = new CoordinateMetrics();

    public List<Metrics> lookup(final Coordinates coordinates) {
        return coordinateMetrics.get(coordinates);
    }


    public void populate(final Coordinates coordinates, final List<Metrics> historic) {
        coordinateMetrics.put(coordinates, historic);
    }

}
