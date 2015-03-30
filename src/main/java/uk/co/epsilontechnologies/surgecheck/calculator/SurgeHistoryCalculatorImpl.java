package uk.co.epsilontechnologies.surgecheck.calculator;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.epsilontechnologies.surgecheck.model.Coordinates;
import uk.co.epsilontechnologies.surgecheck.model.Metrics;
import uk.co.epsilontechnologies.surgecheck.model.SurgeStatus;
import uk.co.epsilontechnologies.surgecheck.repository.SurgeCheckDao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class SurgeHistoryCalculatorImpl implements SurgeHistoryCalculator {

    private static final SimpleDateFormat DAY_HOUR_FORMATTER = new SimpleDateFormat("EEE_hh00");

    private final SurgeCheckDao surgeCheckDao;

    @Autowired
    public SurgeHistoryCalculatorImpl(final SurgeCheckDao surgeCheckDao) {
        this.surgeCheckDao = surgeCheckDao;
    }

    @Override
    public List<Metrics> calculate(final Coordinates coordinates) {

        final Date now = new Date();

        final List<SurgeStatus> surgeStatusRecords = getSurgeStatusRecords(coordinates, now);

        final Map<Date,List<BigDecimal>> surgeMultiplierBuckets = initializeSurgeMultiplierBuckets(now);

        for (final SurgeStatus surgeStatus : surgeStatusRecords) {
            surgeMultiplierBuckets.get(roundToMinutes(surgeStatus.getTimestamp(), 10)).add(surgeStatus.getSurgeMultiplier());
        }

        return convertToMetricsList(surgeMultiplierBuckets);
    }

    private List<Metrics> convertToMetricsList(final Map<Date, List<BigDecimal>> surgeMultiplierBuckets) {
        final List<Metrics> metricsList = new ArrayList<>();
        for (final Date timestamp : surgeMultiplierBuckets.keySet()) {
            final List<BigDecimal> surgeMultipliers = surgeMultiplierBuckets.get(timestamp);
            if (!surgeMultipliers.isEmpty()) {
                final DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
                surgeMultipliers.forEach(bigDecimal -> descriptiveStatistics.addValue(bigDecimal.floatValue()));
                metricsList.add(
                        new Metrics(
                                timestamp.getTime(),
                                new BigDecimal(descriptiveStatistics.getPercentile(75)),
                                new BigDecimal(descriptiveStatistics.getMean()),
                                new BigDecimal(descriptiveStatistics.getPercentile(25))));
            }
        }
        return metricsList;
    }

    private Map<Date, List<BigDecimal>> initializeSurgeMultiplierBuckets(final Date now) {

        final Date start = roundToMinutes(addMinutes(now, -180), 10);
        final Date end = roundToMinutes(addMinutes(now, 180), 10);

        final Map<Date,List<BigDecimal>> surgeMultiplierBuckets = new HashMap<>();

        Date timestamp = new Date(start.getTime());

        while (timestamp.getTime() < end.getTime()) {
            surgeMultiplierBuckets.put(timestamp, new ArrayList<>());
            timestamp = addMinutes(timestamp, 5);
        }
        return surgeMultiplierBuckets;
    }

    private List<SurgeStatus> getSurgeStatusRecords(final Coordinates coordinates, final Date now) {
        final List<SurgeStatus> surgeStatusList = surgeCheckDao.fetchSurgeStatus(coordinates);
        final List<String> classificationsToReport = Arrays.asList(
                classifyTimestamp(addMinutes(now, 180)),
                classifyTimestamp(addMinutes(now, 120)),
                classifyTimestamp(addMinutes(now, 60)),
                classifyTimestamp(now),
                classifyTimestamp(addMinutes(now, -60)),
                classifyTimestamp(addMinutes(now, -120)),
                classifyTimestamp(addMinutes(now, -180)));
        return surgeStatusList
                .stream()
                .filter(surgeStatus -> classificationsToReport.contains(classifyTimestamp(surgeStatus.getTimestamp())))
                .collect(Collectors.toList());
    }

    private Date addMinutes(final Date date, final int minsToAdd) {
        return new Date(date.getTime() + (minsToAdd * 1000 * 60));
    }

    private String classifyTimestamp(final Date timestamp) {
        return DAY_HOUR_FORMATTER.format(timestamp).toUpperCase();
    }

    private Date roundToMinutes(final Date timestamp, final int minutes) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        int unroundedMinutes = calendar.get(Calendar.MINUTE);
        int mod = unroundedMinutes % minutes;
        calendar.add(Calendar.MINUTE, mod == 0 ? minutes : minutes - mod);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

}