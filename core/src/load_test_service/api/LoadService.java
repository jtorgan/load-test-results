package load_test_service.api;

import load_test_service.ProjectTree;
import load_test_service.api.exeptions.FileFormatException;
import load_test_service.api.model.BuildID;
import load_test_service.api.model.BuildType;
import load_test_service.api.model.TestBuild;
import load_test_service.api.statistic.StatisticProperties;
import load_test_service.api.statistic.TestBuildStatistic;
import load_test_service.api.statistic.TestID;
import load_test_service.api.statistic.metrics.Metric;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface LoadService {

    /**
     * Stop service: close entity store; stop monitoring threads
     */
    void stop();

    /**
     * Helper methods to extract list of project from TC in tree format (project with nested sub projects)
     * @return
     */
    @Nullable
    List<ProjectTree> getTCProjectTree();



/*  BUILD TYPE Query Module */

    /**
     * Add build configuration to entity store
     * @param buildType
     */
    void addBuildType(@NotNull final BuildType buildType);

    /**
     * Get build configuration by buildTypeID
     * @param btID = TC buildType BUILD_ID
     */
    @Nullable
    BuildType getBuildType(@NotNull final String btID);

    /**
     * Remove build configuration from entity store, including all builds, downloaded artifacts, calculated statistic values
     * @param btID = TC buildType BUILD_ID
     */
    void removeBuildType(@NotNull final String btID);


    /**
     * Start monitor build configuration: start polling TC for new builds; download new artifacts
     * @param btID
     */
    void startMonitorBuildType(@NotNull final String btID);

    /**
     * Stop monitor build configuration (not remove; ONLY stop)
     * @param btID
     */
    void stopMonitorBuildType(@NotNull final String btID);

    /**
     * Get all saved build configurations
     * @return
     */
    @NotNull
    List<BuildType> getAllBuildTypes();

    /**
     * Returns all builds of build configuration
     * @param btID
     * @return
     */
    @NotNull
    List<TestBuild> getAllBuilds(@NotNull final String btID);

    /**
     * Set artifact patterns to download and save to entity store
     * @param btID
     * @param patterns
     */
    void setArtifactPatterns(@NotNull String btID, @NotNull List<String> patterns);




/*  BUILD Query Module */

    /**
     * Get build by id
     * @param buildID  include TC buildTypeID and buildID
     * @return saved test build
     */
    @Nullable
    TestBuild getBuild(@NotNull final BuildID buildID);

    /**
     * Remove build by id: remove it artifacts, calculated statistic
     * @param buildID  include TC buildTypeID and buildID
     */
    void removeBuild(@NotNull final BuildID buildID);

    /**
     * Get stream of artifact
     * @param buildID
     * @param artifactName
     * @return
     */
    InputStream loadArtifact(@NotNull final BuildID buildID, @NotNull final String artifactName);




/*  STATISTIC Query Module */

    /**
     * Based metrics: average, min, max, 90% line
     * @return
     */
    @NotNull
    Metric[] getBaseMetrics();

    /**
     * Calculate performance statistic based on properties: whether to count total, response codes, which of metrics, etc.
     * @param buildID
     * @param artifactName
     * @param properties
     */
    boolean countStatistic(@NotNull final BuildID buildID, @NotNull final String artifactName, @NotNull final StatisticProperties properties);

    /**
     * Return RPS and SRT chart values
     * @param buildID
     * @param artifactName
     * @return
     * @throws FileFormatException
     */
    Map<TestID, TestBuildStatistic> getRawStatistic(@NotNull final BuildID buildID, @NotNull final String artifactName) throws FileFormatException;


    boolean isStatisticCalculated(@NotNull final BuildID buildID, @NotNull final String artifactName);

    @NotNull
    Collection<String> getArtifactsWithStat(@NotNull final BuildID buildID);

    @NotNull
    Collection<String> getArtifactsWithoutStat(@NotNull final BuildID buildID);

//    @Nullable
//    List<TestID> getAllTests(@NotNull final BuildID buildID);
//    @Nullable
//    List<String> getAllThreadGroups(@NotNull final BuildID buildID);

//    @Nullable
//    List<String> getTestsInThreadGroup(@NotNull final BuildID buildID, String testGroup);
//    Map<TestID, TestBuildTypeStatistic> getAggregatedStatistic(@NotNull BuildID buildID, @NotNull final String artifactName, @NotNull StatisticProperties properties) throws FileFormatException;

//    Map<String, List<Long>> getAggregatedStatistic(@NotNull final String buildTypeID, StatisticMetrics[] metrics);
}
