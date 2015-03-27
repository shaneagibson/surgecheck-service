package uk.co.epsilontechnologies.surgecheck.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.co.epsilontechnologies.surgecheck.model.Coordinates;
import uk.co.epsilontechnologies.surgecheck.model.SurgeCheckResponse;
import uk.co.epsilontechnologies.surgecheck.service.SurgeCheckService;

import java.math.BigDecimal;

@RestController
@RequestMapping(value = "/surgecheck")
public class SurgeCheckController {

    private final SurgeCheckService surgeCheckService;

    @Autowired
    public SurgeCheckController(final SurgeCheckService surgeCheckService) {
        this.surgeCheckService = surgeCheckService;
    }

    @RequestMapping(
            value = "/status",
            method = RequestMethod.GET,
            consumes = "application/json",
            params = { "latitude", "longitude" })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public SurgeCheckResponse checkStatus(@RequestParam final BigDecimal latitude, @RequestParam final BigDecimal longitude) {
        return surgeCheckService.check(new Coordinates(latitude, longitude));
    }

}