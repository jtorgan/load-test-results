package load_test_service.statistic;

import load_test_service.api.statistic.Item;
import load_test_service.api.statistic.TestID;
import load_test_service.api.statistic.metrics.Metric;
import load_test_service.api.statistic.metrics.MetricCounter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public enum BaseMetrics implements Metric {
    AVERAGE("Average time") {
        public MetricCounter getCounter(@NotNull final TestID testID) {
            return new MetricCounter.SingleValueMetric() {
                private long sum = 0;
                private long count = 0;
                private final boolean total = TOTAL_NAME.equals(testID.getTestName());

                @Override
                public long getBuildValue() {
                    return Math.round(sum / count);
                }

                @Override
                public void processItem(@NotNull final Item item) {
                    if (item.getThreadGroup().equals(testID.getThreadGroup()) &&  (total || item.getTestName().equals(testID.getTestName()))) {
                        sum += item.getResponseTime();
                        count++;
                    }
                }

                @Override
                public String getKey() {
                    return BaseMetrics.AVERAGE.getKey();
                }
            };
        }
    },
    MAX("Max time") {
        @Override
        public MetricCounter getCounter(@NotNull final TestID testID) {
            return new MetricCounter.SingleValueMetric() {
                private final boolean total = TOTAL_NAME.equals(testID.getTestName());

                private long value = Integer.MIN_VALUE;

                @Override
                public long getBuildValue() {
                    return value;
                }

                @Override
                public void processItem(@NotNull final Item item) {
                    if (item.getThreadGroup().equals(testID.getThreadGroup()) &&  (total || item.getTestName().equals(testID.getTestName()))) {
                        value = Math.max(value, item.getResponseTime());
                    }
                }

                @Override
                public String getKey() {
                    return BaseMetrics.MAX.getKey();
                }
            };
        }
    },
    MIN("Min time") {
        @Override
        public MetricCounter getCounter(@NotNull final TestID testID) {
            return new MetricCounter.SingleValueMetric() {
                private final boolean total = TOTAL_NAME.equals(testID.getTestName());

                @Override
                public long getBuildValue() {
                    return value;
                }

                @Override
                public void processItem(@NotNull final Item item) {
                    if (item.getThreadGroup().equals(testID.getThreadGroup()) &&  (total || item.getTestName().equals(testID.getTestName()))) {
                        value = Math.min(value, item.getResponseTime());
                    }
                }

                @Override
                public String getKey() {
                    return BaseMetrics.MIN.getKey();
                }
            };
        }
    },
    LINE90("90% line") {
        @Override
        public MetricCounter getCounter(@NotNull final TestID testID) {
            return new MetricCounter.SingleValueMetric() {
                private final boolean total = TOTAL_NAME.equals(testID.getTestName());
                private List<Long> values = new ArrayList<>();
                @Override
                public long getBuildValue() {
                    Collections.sort(values);
                    int ind90 = (int) Math.round(values.size() * 0.9d);
                    return values.get(ind90 - 1);
                }

                @Override
                public void processItem(@NotNull Item item) {
                    if (item.getThreadGroup().equals(testID.getThreadGroup()) && (total || item.getTestName().equals(testID.getTestName())))
                        values.add(item.getResponseTime());
                }

                @Override
                public String getKey() {
                    return BaseMetrics.LINE90.getKey();
                }
            };
        }
    },
    RESPONSE_CODE("Response codes") {
        @Override
        public MetricCounter getCounter(@NotNull final TestID testID) {
            return new MetricCounter.MultipleValueMetric() {
                private final boolean total = TOTAL_NAME.equals(testID.getTestName());
                private Map<String, Long> values = new HashMap<>();

                @Override
                public void processItem(@NotNull Item item) {
                    if (item.getThreadGroup().equals(testID.getThreadGroup()) && (total || item.getTestName().equals(testID.getTestName()))) {
                        Long val = values.get(item.getResponseCode());
                        values.put(item.getResponseCode(), val == null ? 1 : ++val);
                    }
                }

                @Override
                public Map<String, Long> getBuildValues() {
                    return values;
                }

                @Override
                public String getKey() {
                    return BaseMetrics.RESPONSE_CODE.getKey();
                }
            };
        }
    },
    ;
    public static final String TOTAL_NAME = "Total";

    private final String key;
    protected long value;

    BaseMetrics(String key) {
        this.key = key;
    }

    @NotNull
    @Override
    public String getKey() {
        return key;
    }

    public String getName() {
        return name();
    }
}
