package load_test_service.storage.entities;

import load_test_service.api.model.BuildID;
import load_test_service.api.statistic.TestID;
import load_test_service.api.statistic.results.SampleRawResults;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RawCache {
    private final Map<BuildID, Map<String, Integer>> builds;
    private final List<Map<TestID, SampleRawResults>> cache;

    @Nullable
    Map<TestID, SampleRawResults> getRawResults(BuildID buildID, String artifactPath) {
        Map<String, Integer> artifacts = builds.get(buildID);
        if (artifacts == null) return null;
        Integer id = artifacts.get(artifactPath);
        if (id == null) return null;
        return cache.get(id);
    }

    void addRawStatistic(BuildID buildID, String artifactPath, Map<TestID, SampleRawResults> results) {
        if (Runtime.getRuntime().freeMemory() < 30000) {// < 30Mb
            cache.clear();
            System.gc();
        }

        Map<String, Integer> artifacts = builds.get(buildID);
        if (artifacts == null) {
            artifacts = new HashMap<>();
            builds.put(buildID, artifacts);
        }
        artifacts.put(artifactPath, cache.size());
        cache.add(results);
    }

    RawCache() {
        builds = new HashMap<>();
        cache = new ArrayList<>();
    }

}
