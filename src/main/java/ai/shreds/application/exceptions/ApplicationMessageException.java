package ai.shreds.application.exceptions;

import ai.shreds.domain.exceptions.DomainExceptionPersistence;
import ai.shreds.domain.exceptions.DomainExceptionValidation;
import ai.shreds.shared.exceptions.SharedValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ApplicationMessageException {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationMessageException.class);
    private static final String VALIDATION_ERROR_PREFIX = "Message Validation Error";
    private static final String PERSISTENCE_ERROR_PREFIX = "Message Persistence Error";

    public void handleMessageValidationError(Exception ex) {
        String errorMessage = buildValidationErrorMessage(ex);
        logger.error(errorMessage, ex);

        if (ex instanceof SharedValidationException) {
            throw new SharedValidationException(errorMessage);
        } else if (ex instanceof DomainExceptionValidation) {
            throw new DomainExceptionValidation(errorMessage);
        }
    }

    public void handleMessagePersistenceError(Exception ex) {
        String errorMessage = buildPersistenceErrorMessage(ex);
        logger.error(errorMessage, ex);

        if (ex instanceof DomainExceptionPersistence) {
            throw new DomainExceptionPersistence(errorMessage);
        }
    }

    private String buildValidationErrorMessage(Exception ex) {
        StringBuilder errorBuilder = new StringBuilder(VALIDATION_ERROR_PREFIX);
        errorBuilder.append(": ").append(ex.getMessage());
        
        if (ex instanceof SharedValidationException) {
            errorBuilder.append(" (Shared Layer Validation)");
        } else if (ex instanceof DomainExceptionValidation) {
            errorBuilder.append(" (Domain Layer Validation)");
        }
        
        return errorBuilder.toString();
    }

    private String buildPersistenceErrorMessage(Exception ex) {
        StringBuilder errorBuilder = new StringBuilder(PERSISTENCE_ERROR_PREFIX);
        errorBuilder.append(": ").append(ex.getMessage());

        if (ex instanceof DomainExceptionPersistence) {
            errorBuilder.append(" (Domain Layer Persistence)");
        }

        if (ex.getCause() != null) {
            errorBuilder.append(" - Root cause: ").append(ex.getCause().getMessage());
        }

        return errorBuilder.toString();
    }
}
