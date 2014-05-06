package load_test_service.statistic;

import load_test_service.api.statistic.TestID;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Yuliya.Torhan on 4/22/14.
 */
public class TestIDImpl implements TestID {
    private final String testGroup;
    private final String testName;

    public TestIDImpl(@NotNull final String testGroup, @NotNull final String testName) {
        this.testGroup = testGroup;
        this.testName = testName;
    }

    @NotNull
    public String getThreadGroup() {
        return testGroup;
    }

    @NotNull
    public String getTestName() {
        return testName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof TestIDImpl)) return false;

        TestIDImpl test = ((TestIDImpl) obj);
        return test.testGroup.equals(testGroup) && test.testName.equals(testName);
    }

    @Override
    public int hashCode() {
        int hash = testGroup.hashCode() * 17 % 1000;
        hash = hash + testName.hashCode() * 17 % 1000;
        return hash;
    }
}
