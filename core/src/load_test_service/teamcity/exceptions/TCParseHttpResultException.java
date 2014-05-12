package load_test_service.teamcity.exceptions;

/**
 * Created by Yuliya.Torhan on 5/12/14.
 */
public class TCParseHttpResultException extends TCException {
    public TCParseHttpResultException(String message) {
        super(message);
    }

    public TCParseHttpResultException(Throwable cause) {
        super(cause);
    }
}
