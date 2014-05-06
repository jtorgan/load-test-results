package load_test_service.storage.queries;

import load_test_service.api.exeptions.FileFormatException;
import load_test_service.api.model.BuildID;
import load_test_service.api.statistic.TestBuildStatistic;
import load_test_service.api.statistic.TestBuildTypeStatistic;
import load_test_service.api.statistic.TestID;
import load_test_service.api.statistic.metrics.Metric;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Map;


public interface StatisticQuery {
    Map<TestID, TestBuildStatistic> getRawStatistic(@NotNull final BuildID buildID, @NotNull final InputStream artifactName) throws FileFormatException;

    Map<TestID, TestBuildTypeStatistic> getAggregatedStatistic(@NotNull BuildID buildID, @NotNull final InputStream artifactName, @NotNull Metric[] metrics);

//    Map<String, List<Long>> getAggregatedStatistic(@NotNull final String buildTypeID, StatisticMetrics[] metrics);
}
