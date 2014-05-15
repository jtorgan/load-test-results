package load_test_service.api.statistic.results;

import org.jetbrains.annotations.NotNull;

public class Value implements Comparable<Value> {
    private final long x;
    private final long y;

    public Value(long x, long y) {
        this.x = x;
        this.y = y;
    }

    public long getX() {
        return x;
    }

    public long getY() {
        return y;
    }

    @Override
    public int compareTo(@NotNull Value o) {
        if (x == o.x) return 0;
        return x < o.x ? -1 : 1;
    }
}
