package load_test_service.teamcity.exceptions;

/**
 * Created by Yuliya.Torhan on 5/12/14.
 */
public class TCException extends Exception {
    public TCException(String message) {
        super(message);
    }

    public TCException(String message, Throwable cause) {
        super(message, cause);
    }

    public TCException(Throwable cause) {
        super(cause);
    }
}
