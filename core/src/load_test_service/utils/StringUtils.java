package load_test_service.utils;

import java.util.regex.Pattern;

public class StringUtils {
    public static final String EMPTY = "";

    public static final String LOG_DELIMITER = "\t";
    public static final Pattern LOG_DELIMITER_PATTERN = Pattern.compile(LOG_DELIMITER);

    private static final Pattern non_word_pattern = Pattern.compile("\\W");

    public static String getCharKey(String str) {
        return non_word_pattern.matcher(str).replaceAll("");
    }
}
