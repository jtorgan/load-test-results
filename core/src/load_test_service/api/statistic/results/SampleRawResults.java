package load_test_service.api.statistic.results;

import load_test_service.api.statistic.TestID;

import java.util.*;

/**
 * Container for srt and _rps results
 */
public class SampleRawResults {
    private final TestID testID;
    private final List<Value> srt;
    private List<Value> rps;

    private Map<Long, Long> _rps;

    public SampleRawResults(TestID testID) {
        this.testID = testID;
        this.srt = new ArrayList<>();
    }

    public String getThreadGroup() {
        return testID.getThreadGroup();
    }

    public String getName() {
        return testID.getTestName();
    }

    public synchronized void addValue(long startTime, long responseTime) {
        if (_rps == null)
            _rps = new HashMap<>();

        srt.add(new Value(startTime, responseTime));

        startTime = startTime - startTime % 1000;
        Long count = _rps.get(startTime);
        _rps.put(startTime, count == null ? 1 : ++count);
    }

    public synchronized List<Value> getSRTValues() {
        return srt;
    }

    public synchronized List<Value> getRPSValues() {
        if (rps == null) {
            rps = new ArrayList<>(_rps.size());
            for (Long key : _rps.keySet()) {
                rps.add(new Value(key, _rps.get(key)));
            }
            Collections.sort(rps);
        }
        return rps;
    }

}
