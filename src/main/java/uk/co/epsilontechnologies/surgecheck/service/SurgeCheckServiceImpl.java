package uk.co.epsilontechnologies.surgecheck.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.co.epsilontechnologies.surgecheck.cache.SurgeHistoryCache;
import uk.co.epsilontechnologies.surgecheck.calculator.SurgeHistoryCalculator;
import uk.co.epsilontechnologies.surgecheck.gateway.uber.UberGateway;
import uk.co.epsilontechnologies.surgecheck.model.*;
import uk.co.epsilontechnologies.surgecheck.repository.SurgeCheckDao;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SurgeCheckServiceImpl implements SurgeCheckService {


    private final UberGateway uberGateway;
    private final SurgeCheckDao surgeCheckDao;
    private final Grid grid;
    private final SurgeHistoryCache surgeHistoryCache;
    private final SurgeHistoryCalculator surgeHistoryCalculator;

    @Autowired
    public SurgeCheckServiceImpl(
            final UberGateway uberGateway,
            final SurgeCheckDao surgeCheckDao,
            final Grid grid,
            final SurgeHistoryCache surgeHistoryCache,
            final SurgeHistoryCalculator surgeHistoryCalculator) {
        this.uberGateway = uberGateway;
        this.surgeCheckDao = surgeCheckDao;
        this.grid = grid;
        this.surgeHistoryCache = surgeHistoryCache;
        this.surgeHistoryCalculator = surgeHistoryCalculator;
    }

    @Override
    public void populateMetrics() {
        final ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (final Coordinates coordinates : grid.getAllCoordinates()) {
            executorService.execute(() -> {
                final SurgeStatus surgeStatus = uberGateway.getSurgeStatus(coordinates);
                surgeCheckDao.persistSurgeStatus(surgeStatus);
            });
        }
        executorService.shutdown();
    }

    @Override
    public SurgeCheckResponse check(final Coordinates coordinates) {
        final SurgeStatus surgeStatus = uberGateway.getSurgeStatus(coordinates);
        surgeCheckDao.persistSurgeStatus(surgeStatus);
        final BigDecimal current = surgeStatus.getSurgeMultiplier();
        final List<Metrics> historic = surgeHistoryCache.lookup(coordinates);
        return new SurgeCheckResponse(current, historic);
    }

    @Override
    public void updateMetricsCache() {
        final ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (final Coordinates coordinates : grid.getAllCoordinates()) {
            executorService.execute(() -> {
                final List<Metrics> historic = surgeHistoryCalculator.calculate(coordinates);
                surgeHistoryCache.populate(coordinates, historic);
            });
        }
        executorService.shutdown();
    }

}
