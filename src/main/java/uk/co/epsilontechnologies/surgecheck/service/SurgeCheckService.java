package uk.co.epsilontechnologies.surgecheck.service;

import uk.co.epsilontechnologies.surgecheck.error.CoordinatesOutOfBoundsException;
import uk.co.epsilontechnologies.surgecheck.model.Coordinates;
import uk.co.epsilontechnologies.surgecheck.model.SurgeCheckResponse;

import java.util.Date;

public interface SurgeCheckService {

    void populateMetrics();

    SurgeCheckResponse check(Coordinates coordinates) throws CoordinatesOutOfBoundsException;

    void updateMetricsCache();

}
