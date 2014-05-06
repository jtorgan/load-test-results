package load_test_service.statistic;

import load_test_service.api.statistic.TestBuildStatistic;

import java.util.*;

/**
 * Created by Yuliya.Torhan on 4/22/14.
 */
public class TestBuildStatisticImpl implements TestBuildStatistic {
    private List<List<Long>> mySRTValues;
    private Map<Long, Long> myRPSValues;

    public TestBuildStatisticImpl() {
        mySRTValues = new ArrayList<>();
        myRPSValues = new HashMap<>();
    }

    public void addValue(long startTime, long responseTime) {
        if (mySRTValues == null) {
            mySRTValues = new ArrayList<>();
        }
        mySRTValues.add(Arrays.asList(startTime, responseTime));

        if (myRPSValues == null) {
            myRPSValues = new HashMap<Long, Long>();
        }
        startTime = startTime - startTime % 1000;
        Long count = myRPSValues.get(startTime);
        myRPSValues.put(startTime, count == null ? 1 : ++count);
    }

    public List<List<Long>> getSRTValues() {
        Collections.sort(mySRTValues, new Comparator<List<Long>>() {
            @Override
            public int compare(List<Long> o1, List<Long> o2) {
                return o2.get(0).equals(o1.get(0)) ? 0 : o1.get(0) < o2.get(0) ? -1 : 1;
            }
        });
        return mySRTValues;
    }

    public List<List<Long>> getRPSValues() {
        if (myRPSValues != null) {
            List<List<Long>> values = new ArrayList<>(myRPSValues.size());
            for (long time: myRPSValues.keySet()) {
                values.add(Arrays.asList(time, myRPSValues.get(time)));
            }
            Collections.sort(values, new Comparator<List<Long>>() {
                @Override
                public int compare(List<Long> o1, List<Long> o2) {
                    return o2.get(0).equals(o1.get(0)) ? 0 : o1.get(0) < o2.get(0) ? -1 : 1;
                }
            });
            return values;
        }
        return null;
    }
}
