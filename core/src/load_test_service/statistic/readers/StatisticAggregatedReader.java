package load_test_service.statistic.readers;

import load_test_service.api.exeptions.FileFormatException;
import load_test_service.api.statistic.Item;
import load_test_service.api.statistic.StatisticProperties;
import load_test_service.api.statistic.TestID;
import load_test_service.api.statistic.metrics.Metric;
import load_test_service.api.statistic.metrics.MetricCounter;
import load_test_service.statistic.BaseMetrics;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Reader to calculate aggregated statistic values
 */
public class StatisticAggregatedReader extends FileReader {
    private final StatisticProperties properties;
    private Metric[] metrics;

    private List<String> failedItems;

    private Map<TestID, List<MetricCounter>> samplers;
    private Map<TestID, List<MetricCounter>> totals;

    public StatisticAggregatedReader(StatisticProperties properties) {
        super();
        this.samplers = new HashMap<>();
        if (properties.isCalculateTotal()) {
            this.totals = new HashMap<>();
        }

        this.metrics = properties.getMetrics();
        this.properties = properties;

        if (properties.isCheckAssertions()) {
            failedItems = new LinkedList<>();
        }
    }

    @Override
    protected void processLine(String line) throws FileFormatException {
        Item item = new Item(line, properties);

        TestID testID = new TestID(item.getThreadGroup(), item.getTestName());
        runCounters(testID, item, samplers);

        if (properties.isCalculateTotal()) {
            testID = new TestID(item.getThreadGroup(), BaseMetrics.TOTAL_NAME);
            runCounters(testID, item, totals);
        }

        if (properties.isCheckAssertions() && !item.isSuccessful()) {
            if (failedItems == null) {
                failedItems = new LinkedList<>();
            }
            failedItems.add(line);
        }
    }



    public Collection<TestID> getAllTestIDs() {
        return samplers.keySet();
    }

    public Collection<MetricCounter> getSamplerValues(@NotNull TestID testID) {
        return samplers.get(testID);
    }


    public Map<TestID, List<MetricCounter>> getValuesBySamplers() {
        return samplers;
    }

    public Map<TestID, List<MetricCounter>> getTotalValuesByThreadGroups() {
        return totals;
    }


    public Collection<String> getFailedItems() {
        return failedItems;
    }

    private void runCounters(TestID testID, Item item, Map<TestID, List<MetricCounter>> counters) {
        List<MetricCounter> testCounters = counters.get(testID);
        if (testCounters == null) {
            testCounters = new ArrayList<>(metrics.length);
            for (Metric metricDescriptor : metrics) {
                testCounters.add(metricDescriptor.getCounter(testID));
            }
            counters.put(testID, testCounters);
        }
        for (MetricCounter counter : testCounters) {
            counter.processItem(item);
        }
    }
}
