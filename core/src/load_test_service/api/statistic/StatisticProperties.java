package load_test_service.api.statistic;

import load_test_service.api.statistic.metrics.Metric;

public class StatisticProperties {
    private boolean responseCodes;
    private boolean assertions;
    private boolean total;
    private boolean usedTestGroups;

    private Metric[] metrics;

    public boolean isCalculateResponseCodes() {
        return responseCodes;
    }

    public void calculateResponseCodes(boolean responseCodes) {
        this.responseCodes = responseCodes;
    }

    public boolean isCheckAssertions() {
        return assertions;
    }

    public void checkAssertions(boolean assertions) {
        this.assertions = assertions;
    }

    public boolean isCalculateTotal() {
        return total;
    }

    public void calculateTotal(boolean total) {
        this.total = total;
    }

    public boolean isUsedTestGroups() {
        return usedTestGroups;
    }

    public void useTestGroups(boolean usedTestGroups) {
        this.usedTestGroups = usedTestGroups;
    }

    public Metric[] getMetrics() {
        return metrics;
    }

    public void setMetrics(Metric[] metrics) {
        this.metrics = metrics;
    }
}
