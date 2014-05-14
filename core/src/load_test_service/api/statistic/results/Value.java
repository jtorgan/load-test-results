package load_test_service.api.statistic.results;

/**
 * Created by Yuliya.Torhan on 5/14/14.
 */
public class Value {
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
}
