package ai.shreds.domain.ports;

/**
 * Output port for MySQL persistence operations in the domain layer.
 * This interface defines the contract for storing and managing messages in MySQL.
 */
public interface DomainOutputPortMySQLRepository {

    /**
     * Persists a message to MySQL database.
     *
     * @param message The message to be stored, must not be null or empty and must not exceed 255 characters
     * @return The generated MySQL ID for the persisted message
     * @throws ai.shreds.domain.exceptions.DomainExceptionPersistence if persistence operation fails
     */
    Long saveToMySql(String message);

    /**
     * Deletes a message by its ID from MySQL database.
     * This method is used for compensation in case of dual-persistence failure.
     *
     * @param id The ID of the message to delete
     * @throws ai.shreds.domain.exceptions.DomainExceptionPersistence if deletion operation fails
     */
    void deleteById(Long id);

    /**
     * Checks if a message exists in MySQL database.
     *
     * @param id The ID of the message to check
     * @return true if the message exists, false otherwise
     * @throws ai.shreds.domain.exceptions.DomainExceptionPersistence if the check operation fails
     */
    boolean existsById(Long id);
}
