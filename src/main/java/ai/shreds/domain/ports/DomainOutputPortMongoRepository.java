package ai.shreds.domain.ports;

/**
 * Output port for MongoDB persistence operations in the domain layer.
 * This interface defines the contract for storing and managing messages in MongoDB.
 */
public interface DomainOutputPortMongoRepository {

    /**
     * Persists a message to MongoDB database.
     *
     * @param message The message to be stored, must not be null or empty and must not exceed 255 characters
     * @return The generated MongoDB ObjectId as String for the persisted message
     * @throws ai.shreds.domain.exceptions.DomainExceptionPersistence if persistence operation fails
     */
    String saveToMongo(String message);

    /**
     * Deletes a message by its ID from MongoDB database.
     * This method is used for compensation in case of dual-persistence failure.
     *
     * @param id The ObjectId as String of the message to delete
     * @throws ai.shreds.domain.exceptions.DomainExceptionPersistence if deletion operation fails
     */
    void deleteById(String id);

    /**
     * Checks if a message exists in MongoDB database.
     *
     * @param id The ObjectId as String of the message to check
     * @return true if the message exists, false otherwise
     * @throws ai.shreds.domain.exceptions.DomainExceptionPersistence if the check operation fails
     */
    boolean existsById(String id);

    /**
     * Validates if the provided string is a valid MongoDB ObjectId.
     *
     * @param id The string to validate as MongoDB ObjectId
     * @return true if the string is a valid MongoDB ObjectId, false otherwise
     */
    boolean isValidObjectId(String id);
}
