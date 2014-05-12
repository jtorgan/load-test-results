package load_test_service.api.statistic.results;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sample {
    private final String threadGroup;
    private final String name;

    private Map<String, List<SampleBuildValue>> metricValues;

    public Sample(String threadGroup, String name) {
        this.threadGroup = threadGroup;
        this.name = name;
        this.metricValues = new HashMap<>();
    }

    public String getThreadGroup() {
        return threadGroup;
    }

    public String getName() {
        return name;
    }

    public void addMetricValue(String metric, long buildID, long value) {
        List<SampleBuildValue> values = metricValues.get(metric);
        if (values == null) {
            values = new ArrayList<>();
            metricValues.put(metric, values);
        }
        values.add(new SampleBuildValue(buildID, value));
    }

    public Map<String, List<SampleBuildValue>> getMetricValues() {
        return metricValues;
    }

    public final class SampleBuildValue {
        private final long buildID;
        private final long value;

        public SampleBuildValue(long buildID, long value) {
            this.buildID = buildID;
            this.value = value;
        }

        public long getBuildID() {
            return buildID;
        }

        public long getValue() {
            return value;
        }
    }
}
