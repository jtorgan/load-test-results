package load_test_service.api.statistic;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Yuliya.Torhan on 4/22/14.
 */
public interface TestID {
    @NotNull
    String getThreadGroup();

    @NotNull
    String getTestName();
}
