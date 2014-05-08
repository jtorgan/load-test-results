package load_test_service.statistic;

import load_test_service.api.model.BuildID;
import load_test_service.api.statistic.metrics.Metric;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TestSampleBuildsStat {
    private final SortedSet<BuildValues> values;

    public TestSampleBuildsStat() {
        values = new TreeSet<>();
    }

    public void addBuild(@NotNull BuildID buildID) {
        values.add(new BuildValues(buildID));
    }

    public void addBuild(@NotNull BuildID buildID, @NotNull Map<Metric, Long> values)  {
        this.values.add(new BuildValues(buildID, values));
    }

    public SortedSet<BuildValues> getValues() {
        return values;
    }

    public class BuildValues implements Comparable<BuildValues>{
        private @NotNull BuildID buildID;
        private Map<Metric, Long> values;

        public BuildValues(@NotNull BuildID buildID) {
            this.buildID = buildID;
            this.values = new HashMap<>();
        }

        public BuildValues(@NotNull BuildID buildID, @NotNull Map<Metric, Long> values) {
            this.buildID = buildID;
            this.values = values;
        }

        @NotNull
        public BuildID getBuildID() {
            return buildID;
        }

        public void addValue(Metric metric, long value) {
            values.put(metric, value);
        }

        public Map<Metric, Long> getValues() {
            return values;
        }

        @Override
        public int compareTo(@NotNull BuildValues o) {
            return buildID.compareTo(o.getBuildID());
        }
    }
}
