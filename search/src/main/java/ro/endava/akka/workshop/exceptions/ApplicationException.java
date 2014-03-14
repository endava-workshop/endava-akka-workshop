package ro.endava.akka.workshop.exceptions;

/**
 * Created by cosmin on 3/10/14.
 */
public class ApplicationException extends Exception {

    private ErrorCode errorCode;

    private ApplicationException() {
    }

    public ApplicationException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public ApplicationException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
