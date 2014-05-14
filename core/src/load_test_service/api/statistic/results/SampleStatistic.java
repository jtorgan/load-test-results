package load_test_service.api.statistic.results;

import load_test_service.api.statistic.TestID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SampleStatistic {
    private final TestID testID;

    private Map<String, List<Value>> metricValues;

    public SampleStatistic(TestID testID) {
        this.testID = testID;
        this.metricValues = new HashMap<>();
    }

    public String getThreadGroup() {
        return testID.getThreadGroup();
    }

    public String getName() {
        return testID.getTestName();
    }

    public void addMetricValue(String metric, long buildID, long value) {
        List<Value> values = metricValues.get(metric);
        if (values == null) {
            values = new ArrayList<>();
            metricValues.put(metric, values);
        }
        values.add(new Value(buildID, value));
    }

    public Map<String, List<Value>> getMetricValues() {
        return metricValues;
    }

}
