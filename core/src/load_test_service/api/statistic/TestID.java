package load_test_service.api.statistic;

import org.jetbrains.annotations.NotNull;

public class TestID {
    private final String testGroup;
    private final String testName;

    public TestID(@NotNull final String testGroup, @NotNull final String testName) {
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
        if (!(obj instanceof TestID)) return false;

        TestID test = ((TestID) obj);
        return test.testGroup.equals(testGroup) && test.testName.equals(testName);
    }

    @Override
    public int hashCode() {
        int hash = 1 + testGroup.length() * 17;
        hash = hash + testName.length() * 17;
        return hash;
    }
}
