package ai.shreds.shared.utils;

import ai.shreds.shared.exceptions.SharedValidationException;

public class SharedMessageValidator {
    public static final int MAX_LENGTH = 255;

    public static void validateMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new SharedValidationException("Message cannot be null or empty");
        }
        if (message.length() > MAX_LENGTH) {
            throw new SharedValidationException("Message length cannot exceed " + MAX_LENGTH + " characters");
        }
    }
}
