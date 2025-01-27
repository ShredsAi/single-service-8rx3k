package ai.shreds.application.services;

import ai.shreds.application.exceptions.ApplicationMessageException;
import ai.shreds.application.ports.ApplicationMessageInputPort;
import ai.shreds.domain.exceptions.DomainExceptionPersistence;
import ai.shreds.domain.exceptions.DomainExceptionValidation;
import ai.shreds.domain.services.DomainServiceMessage;
import ai.shreds.domain.value_objects.DomainValueSavedIDs;
import ai.shreds.shared.dtos.SharedMessageRequestDTO;
import ai.shreds.shared.dtos.SharedMessageResponseDTO;
import ai.shreds.shared.exceptions.SharedValidationException;
import ai.shreds.shared.utils.SharedMessageValidator;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service responsible for handling message persistence operations.
 * This service implements the ApplicationMessageInputPort interface and acts as a facade
 * between the adapter layer and the domain layer.
 */
@Service
public class ApplicationMessageService implements ApplicationMessageInputPort {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationMessageService.class);

    private final DomainServiceMessage domainServiceMessage;
    private final ApplicationMessageException applicationMessageException;

    /**
     * Constructs a new ApplicationMessageService.
     *
     * @param domainServiceMessage The domain service responsible for message persistence operations
     * @param applicationMessageException The exception handler for application-level exceptions
     * @throws IllegalArgumentException if any of the required dependencies is null
     */
    public ApplicationMessageService(@NonNull DomainServiceMessage domainServiceMessage,
                                   @NonNull ApplicationMessageException applicationMessageException) {
        this.domainServiceMessage = domainServiceMessage;
        this.applicationMessageException = applicationMessageException;
        logger.debug("ApplicationMessageService initialized with required dependencies");
    }

    /**
     * Saves a message to both MySQL and MongoDB databases.
     * This method is transactional and will roll back if any part of the operation fails.
     *
     * @param request The DTO containing the message to be saved
     * @return A DTO containing the generated IDs from both databases
     * @throws SharedValidationException if the message fails validation
     * @throws DomainExceptionValidation if the message fails domain-level validation
     * @throws DomainExceptionPersistence if there's an error during persistence
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public SharedMessageResponseDTO saveMessage(@NonNull SharedMessageRequestDTO request) {
        logger.debug("Processing message save request: {}", request);
        
        validateRequest(request);
        
        try {
            logger.debug("Validating message content");
            SharedMessageValidator.validateMessage(request.getMessage());
            logger.debug("Message validation successful");

            logger.debug("Delegating to domain service for message persistence");
            DomainValueSavedIDs savedIDs = domainServiceMessage.saveMessage(request.getMessage());
            
            logger.info("Message successfully persisted with MySQL ID: {} and MongoDB ID: {}", 
                       savedIDs.getMysqlId(), savedIDs.getMongoId());

            return mapToResponseDTO(savedIDs);
            
        } catch (SharedValidationException | DomainExceptionValidation ex) {
            logger.error("Validation error occurred while processing message", ex);
            applicationMessageException.handleMessageValidationError(ex);
            throw ex;
            
        } catch (DomainExceptionPersistence ex) {
            logger.error("Persistence error occurred while saving message", ex);
            applicationMessageException.handleMessagePersistenceError(ex);
            throw ex;
            
        } catch (Exception ex) {
            logger.error("Unexpected error occurred while processing message", ex);
            DomainExceptionPersistence unexpectedException = new DomainExceptionPersistence(
                "Unexpected error occurred while processing message: " + ex.getMessage());
            applicationMessageException.handleMessagePersistenceError(unexpectedException);
            throw unexpectedException;
        }
    }

    /**
     * Validates the incoming request DTO.
     *
     * @param request The request DTO to validate
     * @throws SharedValidationException if the request is invalid
     */
    private void validateRequest(SharedMessageRequestDTO request) {
        if (request == null) {
            throw new SharedValidationException("Request cannot be null");
        }
        if (request.getMessage() == null) {
            throw new SharedValidationException("Message content cannot be null");
        }
    }

    /**
     * Maps the domain value object to a response DTO.
     *
     * @param savedIDs The domain value object containing the saved IDs
     * @return A response DTO containing the saved IDs
     */
    private SharedMessageResponseDTO mapToResponseDTO(DomainValueSavedIDs savedIDs) {
        SharedMessageResponseDTO response = new SharedMessageResponseDTO();
        response.setMysqlId(savedIDs.getMysqlId());
        response.setMongoId(savedIDs.getMongoId());
        return response;
    }
}
