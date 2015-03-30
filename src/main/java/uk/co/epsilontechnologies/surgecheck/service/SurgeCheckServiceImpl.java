package uk.co.epsilontechnologies.surgecheck.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.co.epsilontechnologies.surgecheck.cache.SurgeHistoryCache;
import uk.co.epsilontechnologies.surgecheck.calculator.SurgeHistoryCalculator;
import uk.co.epsilontechnologies.surgecheck.error.CoordinatesOutOfBoundsException;
import uk.co.epsilontechnologies.surgecheck.gateway.uber.UberGateway;
import uk.co.epsilontechnologies.surgecheck.model.*;
import uk.co.epsilontechnologies.surgecheck.repository.SurgeCheckDao;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final int threadPoolSize;

    @Autowired
    public SurgeCheckServiceImpl(
            final UberGateway uberGateway,
            final SurgeCheckDao surgeCheckDao,
            final Grid grid,
            final SurgeHistoryCache surgeHistoryCache,
            final SurgeHistoryCalculator surgeHistoryCalculator,
            @Value("threadpool.size") final int threadPoolSize) {
        this.uberGateway = uberGateway;
        this.surgeCheckDao = surgeCheckDao;
        this.grid = grid;
        this.surgeHistoryCache = surgeHistoryCache;
        this.surgeHistoryCalculator = surgeHistoryCalculator;
        this.threadPoolSize = threadPoolSize;
    }

    @Override
    public void populateMetrics() {
        final ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
        for (final Coordinates coordinates : grid.getAllCoordinates()) {
            executorService.execute(() -> {
                final SurgeStatus surgeStatus = uberGateway.getSurgeStatus(coordinates);
                surgeCheckDao.persistSurgeStatus(surgeStatus);
            });
        }
        executorService.shutdown();
    }

    @Override
    public SurgeCheckResponse check(final Coordinates coordinates) throws CoordinatesOutOfBoundsException{
        if (!grid.contains(coordinates)) {
            throw new CoordinatesOutOfBoundsException();
        }
        final BigDecimal current = lookupRealTimeSurgeMultiplier(coordinates);
        final List<Metrics> historic = surgeHistoryCache.lookup(coordinates.scale());
        return new SurgeCheckResponse(current, historic);
    }

    @Override
    public void updateMetricsCache() {
        final ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
        for (final Coordinates coordinates : grid.getAllCoordinates()) {
            executorService.execute(() -> {
                final List<Metrics> historic = surgeHistoryCalculator.calculate(coordinates);
                surgeHistoryCache.populate(coordinates, historic);
            });
        }
        executorService.shutdown();
    }

    private BigDecimal lookupRealTimeSurgeMultiplier(final Coordinates coordinates) {
        final SurgeStatus surgeStatus = uberGateway.getSurgeStatus(coordinates);
        surgeCheckDao.persistSurgeStatus(surgeStatus);
        return surgeStatus.getSurgeMultiplier();
    }

}
