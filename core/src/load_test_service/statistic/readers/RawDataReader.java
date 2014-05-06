package load_test_service.statistic.readers;

import load_test_service.api.exeptions.FileFormatException;
import load_test_service.api.statistic.TestBuildStatistic;
import load_test_service.api.statistic.TestID;
import load_test_service.statistic.TestBuildStatisticImpl;
import load_test_service.statistic.TestIDImpl;
import load_test_service.utils.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Yuliya.Torhan on 4/22/14.
 */
public class RawDataReader extends FileReader {
    private final Pattern delimiter = StringUtils.LOG_DELIMITER_PATTERN;
    private final Map<TestID, TestBuildStatistic> tests = new HashMap<>();

    @Override
    protected void processLine(String line) throws FileFormatException {
        String[] items = delimiter.split(line.trim());
        if (checkItem(items)) {
            long startTime = Long.parseLong(items[0]);
            long elapsedTime = Long.parseLong(items[1]);

            TestBuildStatisticImpl statistic = getTest(items[2].trim());
            statistic.addValue(startTime, elapsedTime);
        }
    }

    private TestBuildStatisticImpl getTest(String label) {
        String[] labelParts = label.split(":");
        TestIDImpl id = (labelParts.length == 2) ? new TestIDImpl(labelParts[0], labelParts[1]) : new TestIDImpl(StringUtils.EMPTY, labelParts[0]);
        TestBuildStatistic statistic = tests.get(id);
        if (statistic == null)
            statistic = new TestBuildStatisticImpl();
        return (TestBuildStatisticImpl) statistic;
    }

    private boolean checkItem(String[] values) throws FileFormatException {
        if (values.length < 3) {
            throw new FileFormatException("Item: timestamp\tresultValue\tlabel \n Found: " + Arrays.toString(values));
        }
        return (values[0].matches("\\d+") && values[1].matches("[0-9]*\\.?[0-9]*([Ee][+-]?[0-9]+)?"));
    }

    /**
     * Return test objects with raw statistic
     * @return
     */
    public Map<TestID, TestBuildStatistic> getTests() {
        return tests;
    }
}
