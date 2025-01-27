package ai.shreds.domain.exceptions;

public class DomainExceptionValidation extends RuntimeException {
    public DomainExceptionValidation(String message) {
        super(message);
    }

    public DomainExceptionValidation(String message, Throwable cause) {
        super(message, cause);
    }
}
