package load_test_service.api.statistic.metrics;

import load_test_service.api.statistic.TestID;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Yuliya.Torhan on 4/22/14.
 */
public interface Metric {
    MetricCounter getCounter(@NotNull final TestID testID);
    String getKey();
    String getName();
}
