package ai.shreds.shared.exceptions;

import java.io.Serial;

public class SharedValidationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public SharedValidationException(String message) {
        super(message);
    }

    public SharedValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SharedValidationException(String messageTemplate, Object... args) {
        super(String.format(messageTemplate, args));
    }

    public static SharedValidationException createMessageTooLong(int currentLength, int maxLength) {
        return new SharedValidationException("Message length %d exceeds maximum allowed length of %d", currentLength, maxLength);
    }

    public static SharedValidationException createMessageEmpty() {
        return new SharedValidationException("Message cannot be empty or null");
    }
}
