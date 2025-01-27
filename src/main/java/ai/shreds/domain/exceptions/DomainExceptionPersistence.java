package ai.shreds.domain.exceptions;

public class DomainExceptionPersistence extends RuntimeException {
    public DomainExceptionPersistence(String message) {
        super(message);
    }

    public DomainExceptionPersistence(String message, Throwable cause) {
        super(message, cause);
    }
}
