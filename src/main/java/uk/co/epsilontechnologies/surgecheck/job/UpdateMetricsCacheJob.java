package uk.co.epsilontechnologies.surgecheck.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.co.epsilontechnologies.surgecheck.service.SurgeCheckService;

@Component
public class UpdateMetricsCacheJob {

    private final SurgeCheckService surgeCheckService;

    @Autowired
    public UpdateMetricsCacheJob(final SurgeCheckService surgeCheckService) {
        this.surgeCheckService = surgeCheckService;
        run();
    }

    @Scheduled(cron = "0 5,15,25,35,45,55 * * * *")
    public void run() {
        surgeCheckService.updateMetricsCache();
    }

}