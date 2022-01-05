package walkbook.server.advice.exception;

public class CSignupFailedException extends RuntimeException{
    public CSignupFailedException() {
        super();
    }

    public CSignupFailedException(String message) {
        super(message);
    }

    public CSignupFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
