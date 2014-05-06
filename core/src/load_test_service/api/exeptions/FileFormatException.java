package load_test_service.api.exeptions;

/**
 * Created by Yuliya.Torhan on 4/22/14.
 */
public class FileFormatException extends Exception {
    public FileFormatException(String msg) {
        super("Wrong data format at artifact file! " + msg);
    }
}
