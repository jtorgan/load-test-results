package load_test_service.statistic.readers;

import load_test_service.api.exeptions.FileFormatException;
import load_test_service.api.statistic.TestID;
import load_test_service.api.statistic.results.SampleRawResults;
import load_test_service.utils.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * Reader to calculated raw values from log (RPS and SRT)
 */
public class RawDataReader extends FileReader {
    private final Pattern delimiter = StringUtils.LOG_DELIMITER_PATTERN;

    private final Map<TestID, SampleRawResults> samples = new TreeMap<>();

    @Override
    protected void processLine(String line) throws FileFormatException {
        String[] items = delimiter.split(line.trim());
        if (checkItem(items)) {
            long startTime = Long.parseLong(items[0]);
            long elapsedTime = Long.parseLong(items[1]);

            SampleRawResults statistic = getTest(items[2].trim());
            statistic.addValue(startTime, elapsedTime);
        }
    }

    private SampleRawResults getTest(String label) {
        String[] labelParts = label.split(":");
        TestID id = (labelParts.length == 2) ? new TestID(labelParts[0], labelParts[1]) : new TestID(StringUtils.EMPTY, labelParts[0]);
        SampleRawResults statistic = samples.get(id);
        if (statistic == null) {
            statistic = new SampleRawResults(id);
            samples.put(id, statistic);
        }
        return statistic;
    }

    private boolean checkItem(String[] values) throws FileFormatException {
        if (values.length < 3) {
            throw new FileFormatException("Item: timestamp\tresultValue\tlabel \n Found: " + Arrays.toString(values));
        }
        return (values[0].matches("\\d+") && values[1].matches("[0-9]*\\.?[0-9]*([Ee][+-]?[0-9]+)?"));
    }

    public Map<TestID, SampleRawResults> getSamples() {
        return samples;
    }
}
