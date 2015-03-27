package uk.co.epsilontechnologies.surgecheck.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.co.epsilontechnologies.surgecheck.service.SurgeCheckService;

@Component
public class PopulateMetricsJob {

    private final SurgeCheckService surgeCheckService;

    @Autowired
    public PopulateMetricsJob(final SurgeCheckService surgeCheckService) {
        this.surgeCheckService = surgeCheckService;
    }

    @Scheduled(cron = "0 0,10,20,30,40,50 * * * *")
    public void run() {
        System.out.println("----- Populating Metrics from Uber");
        surgeCheckService.populateMetrics();
    }

}