package ai.shreds.application.exceptions;

import ai.shreds.domain.exceptions.DomainExceptionValidation;
import ai.shreds.domain.exceptions.DomainExceptionPersistence;
import ai.shreds.shared.exceptions.SharedValidationException;
import org.springframework.stereotype.Component;

@Component
public class ApplicationMessageException {
    public void handleMessageValidationError(Exception ex) {
        if (ex instanceof DomainExceptionValidation) {
            throw new SharedValidationException(ex.getMessage());
        }
        throw new SharedValidationException("Invalid message format");
    }

    public void handleMessagePersistenceError(Exception ex) {
        if (ex instanceof DomainExceptionPersistence) {
            throw new SharedValidationException("Failed to persist message: " + ex.getMessage());
        }
        throw new SharedValidationException("System error while persisting message");
    }
}
