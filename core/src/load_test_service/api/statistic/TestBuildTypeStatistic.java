package load_test_service.api.statistic;

import load_test_service.api.model.BuildID;
import load_test_service.api.statistic.metrics.Metric;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created by Yuliya.Torhan on 4/22/14.
 */
public interface TestBuildTypeStatistic {
    @Nullable
    Map<BuildID, Long> getTestValues(@NotNull final TestID testID, @NotNull final Metric metric);

    @Nullable
    Map<BuildID, Long> getTotalValues(@NotNull final Metric metric);

}
