package ai.shreds.domain.exceptions;

/**
 * Domain-specific exception for validation errors.
 * This exception is thrown when domain validation rules are violated.
 */
public class DomainExceptionValidation extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new validation exception with the specified detail message.
     *
     * @param message the detail message explaining the validation error
     */
    public DomainExceptionValidation(String message) {
        super(message);
    }

    /**
     * Constructs a new validation exception with the specified detail message and cause.
     *
     * @param message the detail message explaining the validation error
     * @param cause the cause of the validation error
     */
    public DomainExceptionValidation(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a validation exception with a formatted message.
     *
     * @param messageFormat the message format string
     * @param args the arguments to be formatted into the message
     * @return a new DomainExceptionValidation with the formatted message
     */
    public static DomainExceptionValidation withFormat(String messageFormat, Object... args) {
        return new DomainExceptionValidation(String.format(messageFormat, args));
    }

    /**
     * Creates a validation exception for when a value exceeds its maximum length.
     *
     * @param fieldName the name of the field that exceeded its length
     * @param maxLength the maximum allowed length
     * @param actualLength the actual length of the value
     * @return a new DomainExceptionValidation with a formatted message
     */
    public static DomainExceptionValidation lengthExceeded(String fieldName, int maxLength, int actualLength) {
        return withFormat("%s length exceeded. Maximum allowed: %d, Actual: %d", 
                         fieldName, maxLength, actualLength);
    }

    /**
     * Creates a validation exception for when a required value is null or empty.
     *
     * @param fieldName the name of the required field
     * @return a new DomainExceptionValidation with a formatted message
     */
    public static DomainExceptionValidation requiredValue(String fieldName) {
        return withFormat("%s is required and cannot be null or empty", fieldName);
    }
}
