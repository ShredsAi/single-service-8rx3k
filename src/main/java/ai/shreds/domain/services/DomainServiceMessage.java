package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainEntityDocument;
import ai.shreds.domain.entities.DomainEntityMessage;
import ai.shreds.domain.exceptions.DomainExceptionPersistence;
import ai.shreds.domain.exceptions.DomainExceptionPersistence.DatabaseType;
import ai.shreds.domain.exceptions.DomainExceptionValidation;
import ai.shreds.domain.ports.DomainInputPortSaveMessage;
import ai.shreds.domain.ports.DomainOutputPortMongoRepository;
import ai.shreds.domain.ports.DomainOutputPortMySQLRepository;
import ai.shreds.domain.value_objects.DomainValueSavedIDs;

/**
 * Domain service responsible for orchestrating message persistence across MySQL and MongoDB databases.
 * Implements the dual-write pattern with compensation handling for partial failures.
 */
public class DomainServiceMessage implements DomainInputPortSaveMessage {

    private static final String ENTITY_TYPE = "Message";
    private final DomainOutputPortMySQLRepository mySQLRepo;
    private final DomainOutputPortMongoRepository mongoRepo;

    /**
     * Constructs a new DomainServiceMessage with required repositories.
     *
     * @param mySQLRepo MySQL repository for message persistence
     * @param mongoRepo MongoDB repository for message persistence
     * @throws IllegalArgumentException if any repository is null
     */
    public DomainServiceMessage(
            DomainOutputPortMySQLRepository mySQLRepo,
            DomainOutputPortMongoRepository mongoRepo) {
        if (mySQLRepo == null) {
            throw DomainExceptionValidation.requiredValue("MySQL Repository");
        }
        if (mongoRepo == null) {
            throw DomainExceptionValidation.requiredValue("MongoDB Repository");
        }
        this.mySQLRepo = mySQLRepo;
        this.mongoRepo = mongoRepo;
    }

    /**
     * Saves a message to both MySQL and MongoDB databases.
     * Implements a dual-write pattern with compensation handling.
     *
     * @param message the message to be saved
     * @return DomainValueSavedIDs containing both MySQL and MongoDB generated IDs
     * @throws DomainExceptionValidation if the message is invalid
     * @throws DomainExceptionPersistence if persistence operations fail
     */
    @Override
    public DomainValueSavedIDs saveMessage(String message) {
        // Create and validate domain entities
        DomainEntityMessage mysqlEntity = new DomainEntityMessage(message);
        DomainEntityDocument mongoEntity = new DomainEntityDocument(message);

        // Save to MySQL first
        Long mysqlId = saveToMySql(message);
        mysqlEntity.setId(mysqlId);

        // Save to MongoDB
        String mongoId;
        try {
            mongoId = saveToMongo(message);
            mongoEntity.setId(mongoId);
        } catch (Exception ex) {
            // Compensation: attempt to delete the MySQL record if MongoDB save fails
            handleMongoFailureWithCompensation(mysqlId, ex);
            throw DomainExceptionPersistence.dualPersistenceFailure(
                DatabaseType.MYSQL,
                DatabaseType.MONGODB,
                ENTITY_TYPE
            );
        }

        // Verify both IDs exist
        if (mysqlId == null || mongoId == null) {
            throw DomainExceptionPersistence.withFormat(
                "Failed to obtain valid IDs from both databases. MySQL ID: %s, MongoDB ID: %s",
                mysqlId, mongoId
            );
        }

        return mongoEntity.toValueObject(mysqlId);
    }

    /**
     * Saves the message to MySQL database.
     *
     * @param message the message to save
     * @return the generated MySQL ID
     * @throws DomainExceptionPersistence if the save operation fails
     */
    private Long saveToMySql(String message) {
        try {
            return mySQLRepo.saveToMySql(message);
        } catch (Exception ex) {
            throw DomainExceptionPersistence.saveFailed(
                DatabaseType.MYSQL,
                ENTITY_TYPE,
                ex
            );
        }
    }

    /**
     * Saves the message to MongoDB database.
     *
     * @param message the message to save
     * @return the generated MongoDB ID
     * @throws DomainExceptionPersistence if the save operation fails
     */
    private String saveToMongo(String message) {
        try {
            return mongoRepo.saveToMongo(message);
        } catch (Exception ex) {
            throw DomainExceptionPersistence.saveFailed(
                DatabaseType.MONGODB,
                ENTITY_TYPE,
                ex
            );
        }
    }

    /**
     * Handles MongoDB save failure by attempting to compensate the MySQL save.
     *
     * @param mysqlId the MySQL ID to delete during compensation
     * @param originalError the original MongoDB error
     * @throws DomainExceptionPersistence if compensation fails
     */
    private void handleMongoFailureWithCompensation(Long mysqlId, Exception originalError) {
        try {
            mySQLRepo.deleteById(mysqlId);
        } catch (Exception ex) {
            throw DomainExceptionPersistence.withFormat(
                "Compensation failed: Could not delete MySQL record (ID: %d) after MongoDB failure. Original error: %s. Compensation error: %s",
                mysqlId,
                originalError.getMessage(),
                ex.getMessage()
            );
        }
    }
}
