package ai.shreds.shared.utils;

import ai.shreds.shared.exceptions.SharedValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SharedMessageValidator {

    private static final Logger logger = LoggerFactory.getLogger(SharedMessageValidator.class);
    public static final int MAX_LENGTH = 255;
    private static final String SPECIAL_CHARS_REGEX = "[!@#$%^&*(),.?\":{}|<>]";

    private SharedMessageValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static void validateMessage(String message) {
        logger.debug("Validating message: {}", message);

        if (message == null || message.trim().isEmpty()) {
            logger.error("Message validation failed: empty or null message");
            throw SharedValidationException.createMessageEmpty();
        }

        String trimmedMessage = message.trim();
        if (trimmedMessage.length() > MAX_LENGTH) {
            logger.error("Message validation failed: message too long ({} characters)", trimmedMessage.length());
            throw SharedValidationException.createMessageTooLong(trimmedMessage.length(), MAX_LENGTH);
        }

        validateSpecialCharacters(trimmedMessage);
        logger.debug("Message validation passed successfully");
    }

    private static void validateSpecialCharacters(String message) {
        if (message.matches(".*" + SPECIAL_CHARS_REGEX + ".*")) {
            logger.warn("Message contains special characters that might need escaping");
        }
    }
}
