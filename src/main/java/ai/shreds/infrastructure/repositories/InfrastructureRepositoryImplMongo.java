package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainEntityDocument;
import ai.shreds.domain.exceptions.DomainExceptionPersistence;
import ai.shreds.domain.ports.DomainOutputPortMongoRepository;
import ai.shreds.infrastructure.exceptions.InfrastructureExceptionPersistence;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Repository
public class InfrastructureRepositoryImplMongo implements DomainOutputPortMongoRepository {

    private static final Logger logger = LoggerFactory.getLogger(InfrastructureRepositoryImplMongo.class);
    private static final int MAX_RETRY_ATTEMPTS = 3;

    private final InfrastructureRepositoryMongo mongoRepository;
    private final MongoTemplate mongoTemplate;
    private final InfrastructureExceptionPersistence exceptionPersistence;

    @Autowired
    public InfrastructureRepositoryImplMongo(InfrastructureRepositoryMongo mongoRepository,
                                            MongoTemplate mongoTemplate) {
        this.mongoRepository = mongoRepository;
        this.mongoTemplate = mongoTemplate;
        this.exceptionPersistence = new InfrastructureExceptionPersistence("Infrastructure Mongo Exception");
    }

    @Override
    @Retryable(value = {MongoTimeoutException.class},
               maxAttempts = MAX_RETRY_ATTEMPTS,
               backoff = @Backoff(delay = 500, multiplier = 2))
    public String saveToMongo(String message) {
        logger.debug("Attempting to save message to MongoDB: {}", message);
        validateMessage(message);

        try {
            InfrastructureEntityDocument doc = createDocument(message);
            checkDuplicateMessage(message);
            
            InfrastructureEntityDocument saved = mongoRepository.save(doc);
            logger.info("Successfully saved message to MongoDB with ID: {}", saved.getId());
            return saved.getId();

        } catch (DuplicateKeyException dke) {
            logger.error("Duplicate key violation in MongoDB: {}", dke.getMessage());
            throw new DomainExceptionPersistence("Duplicate message detected: " + message);
        } catch (MongoTimeoutException mte) {
            logger.warn("MongoDB operation timed out, will retry: {}", mte.getMessage());
            throw mte; // Will be retried by @Retryable
        } catch (MongoWriteException mwe) {
            logger.error("MongoDB write error: {}", mwe.getMessage());
            throw exceptionPersistence.handleMongoError(mwe);
        } catch (MongoException me) {
            logger.error("MongoDB error: {}", me.getMessage());
            throw exceptionPersistence.handleMongoError(me);
        } catch (DataAccessException dae) {
            logger.error("Data access error while saving to MongoDB: {}", dae.getMessage());
            throw new DomainExceptionPersistence("Data access error while saving to MongoDB: " + dae.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while saving to MongoDB: {}", e.getMessage(), e);
            throw new DomainExceptionPersistence("Unexpected error while saving to MongoDB: " + e.getMessage());
        }
    }

    private InfrastructureEntityDocument createDocument(String message) {
        logger.debug("Creating new MongoDB document for message");
        InfrastructureEntityDocument doc = new InfrastructureEntityDocument();
        doc.setMessageText(message);
        doc.setCreatedAt(LocalDateTime.now());
        doc.setUpdatedAt(LocalDateTime.now());
        return doc;
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
        logger.debug("Checking for duplicate message in MongoDB: {}", message);
        Query query = new Query(Criteria.where("messageText").is(message));
        if (mongoTemplate.exists(query, InfrastructureEntityDocument.class)) {
            logger.warn("Duplicate message detected in MongoDB: {}", message);
            throw new DomainExceptionPersistence("Duplicate message detected");
        }
    }

    protected DomainEntityDocument mapToDomainEntity(InfrastructureEntityDocument document) {
        if (document == null) {
            return null;
        }
        logger.debug("Mapping MongoDB document to domain entity: {}", document.getId());
        return new DomainEntityDocument(
            document.getId(),
            document.getMessageText(),
            document.getCreatedAt(),
            document.getUpdatedAt()
        );
    }
}
