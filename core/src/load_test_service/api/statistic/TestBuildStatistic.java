package load_test_service.api.statistic;

import java.util.List;

/**
 * Created by Yuliya.Torhan on 4/22/14.
 */
public interface TestBuildStatistic {
    List<List<Long>> getSRTValues();
    List<List<Long>> getRPSValues();
}
