package ai.shreds.domain.exceptions;

/**
 * Domain-specific exception for persistence-related errors.
 * This exception is thrown when database operations fail or when persistence rules are violated.
 */
public class DomainExceptionPersistence extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Database type enumeration for specifying the source of persistence errors.
     */
    public enum DatabaseType {
        MYSQL("MySQL"),
        MONGODB("MongoDB");

        private final String name;

        DatabaseType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * Constructs a new persistence exception with the specified detail message.
     *
     * @param message the detail message explaining the persistence error
     */
    public DomainExceptionPersistence(String message) {
        super(message);
    }

    /**
     * Constructs a new persistence exception with the specified detail message and cause.
     *
     * @param message the detail message explaining the persistence error
     * @param cause the cause of the persistence error
     */
    public DomainExceptionPersistence(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a persistence exception with a formatted message.
     *
     * @param messageFormat the message format string
     * @param args the arguments to be formatted into the message
     * @return a new DomainExceptionPersistence with the formatted message
     */
    public static DomainExceptionPersistence withFormat(String messageFormat, Object... args) {
        return new DomainExceptionPersistence(String.format(messageFormat, args));
    }

    /**
     * Creates a persistence exception for when an entity cannot be saved.
     *
     * @param dbType the type of database where the save operation failed
     * @param entityType the type of entity that failed to save
     * @param cause the underlying cause of the failure
     * @return a new DomainExceptionPersistence with a formatted message
     */
    public static DomainExceptionPersistence saveFailed(DatabaseType dbType, String entityType, Throwable cause) {
        return new DomainExceptionPersistence(
            String.format("Failed to save %s to %s database", entityType, dbType),
            cause
        );
    }

    /**
     * Creates a persistence exception for when an entity cannot be deleted.
     *
     * @param dbType the type of database where the delete operation failed
     * @param entityType the type of entity that failed to delete
     * @param id the ID of the entity that failed to delete
     * @return a new DomainExceptionPersistence with a formatted message
     */
    public static DomainExceptionPersistence deleteFailed(DatabaseType dbType, String entityType, Object id) {
        return withFormat("Failed to delete %s with ID %s from %s database", 
                         entityType, id.toString(), dbType);
    }

    /**
     * Creates a persistence exception for when a dual-database operation fails and requires compensation.
     *
     * @param primaryDb the database where the operation succeeded and needs compensation
     * @param failedDb the database where the operation failed
     * @param entityType the type of entity involved
     * @return a new DomainExceptionPersistence with a formatted message
     */
    public static DomainExceptionPersistence dualPersistenceFailure(
            DatabaseType primaryDb, DatabaseType failedDb, String entityType) {
        return withFormat(
            "Dual persistence failure: %s operation succeeded but %s operation failed for %s. Compensation required.",
            primaryDb, failedDb, entityType
        );
    }
}
