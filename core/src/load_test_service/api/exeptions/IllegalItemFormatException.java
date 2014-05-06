package load_test_service.api.exeptions;

import java.util.Arrays;

/**
 * Created by Yuliya.Torhan on 4/22/14.
 */
public class IllegalItemFormatException extends FileFormatException {
    public IllegalItemFormatException(String msg) {
        super(msg);
    }
    public IllegalItemFormatException(String wrongFiledNames, String[] actualData) {
        super("Result item format must included asserted result. Format: startTime\tresponseTime\ttestName" + wrongFiledNames + "...\nFound" + Arrays.toString(actualData));
    }
}
