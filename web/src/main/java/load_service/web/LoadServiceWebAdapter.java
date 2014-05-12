package load_service.web;

import load_test_service.LoadServiceFactory;
import load_test_service.ProjectTree;
import load_test_service.api.LoadService;
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

public class LoadServiceWebAdapter implements LoadService {
    private LoadService myService;

    /**
     * Initialization base service: open entity store, run threads to monitor build types
     * @throws Exception
     */
    public void initIt() throws Exception {
        myService = LoadServiceFactory.getDefault("c:\\_DB_\\");
        System.out.println("Load Service: init entity store");
    }

    /**
     * Stop base service: close entity store; stop monitoring
     * @throws Exception
     */
    public void cleanUp() throws Exception {
        myService.stop();
        System.out.println("Load Service: close entity store");
    }


//  Forward all queries to actual service
    @Override
    public void stop() {
        myService.stop();
    }

    @Nullable
    @Override
    public List<ProjectTree> getTCProjectTree() {
        return myService.getTCProjectTree();
    }

    @Override
    public void addBuildType(@NotNull final BuildType buildType) {
        myService.addBuildType(buildType);
    }

    @Override
    public BuildType getBuildType(@NotNull String btID) {
        return myService.getBuildType(btID);
    }

    @Override
    public void removeBuildType(@NotNull String btID) {
        myService.removeBuildType(btID);
    }

    @Override
    public void startMonitorBuildType(@NotNull String btID) {
        myService.startMonitorBuildType(btID);
    }

    @Override
    public void stopMonitorBuildType(@NotNull String btID) {
        myService.stopMonitorBuildType(btID);
    }

    @NotNull
    @Override
    public List<BuildType> getAllBuildTypes() {
        return myService.getAllBuildTypes();
    }

    @NotNull
    @Override
    public List<TestBuild> getAllBuilds(@NotNull String btID) {
        return myService.getAllBuilds(btID);
    }

    @Override
    public void setArtifactPatterns(@NotNull String btID, @NotNull List<String> patterns) {
        myService.setArtifactPatterns(btID, patterns);
    }

    @Override
    public TestBuild getBuild(@NotNull BuildID buildID) {
        return myService.getBuild(buildID);
    }

    @Override
    public void removeBuild(@NotNull BuildID buildID) {
        myService.removeBuild(buildID);
    }

    @Override
    public InputStream loadArtifact(@NotNull BuildID buildID, @NotNull String artifactName) {
        return myService.loadArtifact(buildID, artifactName);
    }

    @NotNull
    @Override
    public Metric[] getBaseMetrics() {
        return myService.getBaseMetrics();
    }

    @Override
    public boolean countStatistic(@NotNull BuildID buildID, @NotNull String artifactName, @NotNull StatisticProperties properties) {
        return myService.countStatistic(buildID, artifactName, properties);
    }

    @Override
    public Map<TestID, TestBuildStatistic> getRawStatistic(@NotNull BuildID buildID, @NotNull String artifactName) throws FileFormatException {
        return myService.getRawStatistic(buildID, artifactName);
    }

    @Override
    public boolean isStatisticCalculated(@NotNull BuildID buildID, @NotNull String artifactName) {
        return myService.isStatisticCalculated(buildID, artifactName);
    }

    @NotNull
    @Override
    public Collection<String> getArtifactsWithStat(@NotNull BuildID buildID) {
        return myService.getArtifactsWithStat(buildID);
    }

    @NotNull
    @Override
    public Collection<String> getArtifactsWithoutStat(@NotNull BuildID buildID) {
        return myService.getArtifactsWithoutStat(buildID);
    }
}
