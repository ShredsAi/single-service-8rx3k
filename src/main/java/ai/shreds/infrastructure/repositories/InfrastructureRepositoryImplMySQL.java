package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainEntityMessage;
import ai.shreds.domain.exceptions.DomainExceptionPersistence;
import ai.shreds.domain.ports.DomainOutputPortMySQLRepository;
import ai.shreds.infrastructure.exceptions.InfrastructureExceptionPersistence;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.time.LocalDateTime;

@Repository
public class InfrastructureRepositoryImplMySQL implements DomainOutputPortMySQLRepository {

    private static final Logger logger = LoggerFactory.getLogger(InfrastructureRepositoryImplMySQL.class);
    private static final int MAX_RETRY_ATTEMPTS = 3;

    private final InfrastructureRepositoryMySQLJPA jpaRepository;
    private final InfrastructureExceptionPersistence exceptionPersistence;

    @Autowired
    public InfrastructureRepositoryImplMySQL(InfrastructureRepositoryMySQLJPA jpaRepository) {
        this.jpaRepository = jpaRepository;
        this.exceptionPersistence = new InfrastructureExceptionPersistence("Infrastructure MySQL Exception");
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable(value = {DeadlockLoserDataAccessException.class},
               maxAttempts = MAX_RETRY_ATTEMPTS,
               backoff = @Backoff(delay = 500, multiplier = 2))
    public Long saveToMySql(String message) {
        logger.debug("Attempting to save message to MySQL: {}", message);
        validateMessage(message);

        try {
            checkDuplicateMessage(message);
            
            InfrastructureEntityMessage entity = createEntity(message);
            InfrastructureEntityMessage saved = jpaRepository.save(entity);
            
            logger.info("Successfully saved message to MySQL with ID: {}", saved.getId());
            return saved.getId();

        } catch (DataIntegrityViolationException dive) {
            logger.error("Data integrity violation while saving message: {}", dive.getMessage());
            throw new DomainExceptionPersistence("Data integrity violation while saving message: " + dive.getMessage());
        } catch (DeadlockLoserDataAccessException dle) {
            logger.warn("Deadlock detected while saving message, will retry: {}", dle.getMessage());
            throw dle; // Will be retried by @Retryable
        } catch (PersistenceException pe) {
            logger.error("Persistence error while saving message: {}", pe.getMessage());
            if (pe.getCause() instanceof SQLException) {
                throw exceptionPersistence.handleMySQLError((SQLException) pe.getCause());
            }
            throw new DomainExceptionPersistence("Persistence error while saving message: " + pe.getMessage());
        } catch (Exception ex) {
            logger.error("Unexpected error while saving to MySQL: {}", ex.getMessage(), ex);
            throw new DomainExceptionPersistence("Unexpected error while saving to MySQL: " + ex.getMessage());
        }
    }

    private InfrastructureEntityMessage createEntity(String message) {
        InfrastructureEntityMessage entity = new InfrastructureEntityMessage();
        entity.setMessageText(message);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }

    private void validateMessage(String message) {
        logger.debug("Validating message: {}", message);
        if (!StringUtils.hasText(message)) {
            logger.error("Message validation failed: empty message");
            throw new DomainExceptionPersistence("Message cannot be empty");
        }
        if (message.length() > 255) {
            logger.error("Message validation failed: message too long ({})", message.length());
            throw new DomainExceptionPersistence("Message length cannot exceed 255 characters");
        }
    }

    private void checkDuplicateMessage(String message) {
        logger.debug("Checking for duplicate message: {}", message);
        if (jpaRepository.existsByMessageText(message)) {
            logger.warn("Duplicate message detected: {}", message);
            throw new DomainExceptionPersistence("Duplicate message detected");
        }
    }

    protected DomainEntityMessage mapToDomainEntity(InfrastructureEntityMessage entity) {
        if (entity == null) {
            return null;
        }
        logger.debug("Mapping infrastructure entity to domain entity: {}", entity.getId());
        return new DomainEntityMessage(
            entity.getId(),
            entity.getMessageText(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
