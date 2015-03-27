package uk.co.epsilontechnologies.surgecheck.repository;

import uk.co.epsilontechnologies.surgecheck.model.Coordinates;
import uk.co.epsilontechnologies.surgecheck.model.SurgeStatus;

import java.util.List;

public interface SurgeCheckDao {

    void persistSurgeStatus(SurgeStatus surgeStatus);

    List<SurgeStatus> fetchSurgeStatus(Coordinates coordinates);

}
