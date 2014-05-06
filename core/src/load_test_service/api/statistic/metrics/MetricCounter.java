package load_test_service.api.statistic.metrics;

import load_test_service.api.statistic.Item;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Created by Yuliya.Torhan on 4/23/14.
 */
public interface MetricCounter {
    void processItem(@NotNull Item item);
    String getKey();

    static interface SingleValueMetric extends MetricCounter {
        long getBuildValue();
    }

    static interface MultipleValueMetric extends MetricCounter {
        Map<String, Long> getBuildValues();
        //        Map<Metric, Long> getBuildValues();

    }
}
