package uk.co.epsilontechnologies.surgecheck.calculator;

import uk.co.epsilontechnologies.surgecheck.model.Coordinates;
import uk.co.epsilontechnologies.surgecheck.model.Metrics;

import java.util.List;

public interface SurgeHistoryCalculator {

    List<Metrics> calculate(Coordinates coordinates);

}
