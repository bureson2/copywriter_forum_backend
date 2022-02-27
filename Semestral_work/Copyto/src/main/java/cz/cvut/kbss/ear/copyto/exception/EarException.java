package cz.cvut.kbss.ear.copyto.exception;

/**
 * Base for all application-specific exceptions.
 */
public class EarException extends RuntimeException {

    public EarException() {
    }

    public EarException(String message) {
        super(message);
    }

    public EarException(String message, Throwable cause) {
        super(message, cause);
    }

    public EarException(Throwable cause) {
        super(cause);
    }
}
