package load_test_service.storage.queries;

import jetbrains.exodus.database.Entity;
import jetbrains.exodus.database.StoreTransaction;
import load_test_service.api.exeptions.FileFormatException;
import load_test_service.api.exeptions.LinkNotFound;
import load_test_service.api.statistic.StatisticProperties;
import load_test_service.api.statistic.TestBuildStatistic;
import load_test_service.api.statistic.TestID;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Map;


public interface StatisticQuery {
    Map<TestID, TestBuildStatistic> getRawStatistic(@NotNull InputStream artifact) throws FileFormatException;


    void countStatistic(@NotNull StoreTransaction txn, @NotNull final Entity buildType, @NotNull final InputStream artifact,
                        @NotNull final StatisticProperties properties) throws FileFormatException, LinkNotFound;

    void deleteBuildStatistic(@NotNull final Entity build);

//    Map<TestID, TestBuildTypeStatistic> getAggregatedStatistic(@NotNull BuildID buildID, @NotNull final InputStream artifactName, @NotNull Metric[] metrics);

//    Map<String, List<Long>> getAggregatedStatistic(@NotNull final String buildTypeID, StatisticMetrics[] metrics);
}
