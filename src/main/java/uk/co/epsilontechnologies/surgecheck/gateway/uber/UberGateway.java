package uk.co.epsilontechnologies.surgecheck.gateway.uber;

import uk.co.epsilontechnologies.surgecheck.model.Coordinates;
import uk.co.epsilontechnologies.surgecheck.model.SurgeStatus;

public interface UberGateway {

    SurgeStatus getSurgeStatus(Coordinates coordinates);

}
