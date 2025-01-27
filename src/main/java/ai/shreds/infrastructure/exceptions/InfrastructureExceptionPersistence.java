package ai.shreds.infrastructure.exceptions;

import ai.shreds.domain.exceptions.DomainExceptionPersistence;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLTimeoutException;

public class InfrastructureExceptionPersistence extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(InfrastructureExceptionPersistence.class);

    private final String message;
    private final String errorCode;

    public InfrastructureExceptionPersistence(String message) {
        this(message, "INFRA_ERR_001");
    }

    public InfrastructureExceptionPersistence(String message, String errorCode) {
        super(message);
        this.message = message;
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public DomainExceptionPersistence handleMySQLError(SQLException ex) {
        logger.error("MySQL error occurred: {}", ex.getMessage(), ex);

        if (ex instanceof SQLIntegrityConstraintViolationException) {
            if (ex.getMessage().contains("Duplicate entry")) {
                return new DomainExceptionPersistence(
                    "A duplicate record already exists",
                    "MYSQL_DUPLICATE_001"
                );
            }
            return new DomainExceptionPersistence(
                "Data integrity violation occurred",
                "MYSQL_CONSTRAINT_001"
            );
        }

        if (ex instanceof SQLTimeoutException) {
            return new DomainExceptionPersistence(
                "Database operation timed out",
                "MYSQL_TIMEOUT_001"
            );
        }

        // Handle specific MySQL error codes
        switch (ex.getErrorCode()) {
            case 1040: // Too many connections
                return new DomainExceptionPersistence(
                    "Database connection pool exhausted",
                    "MYSQL_CONN_001"
                );
            case 1064: // SQL syntax error
                return new DomainExceptionPersistence(
                    "Invalid database query",
                    "MYSQL_SYNTAX_001"
                );
            case 1205: // Lock wait timeout
                return new DomainExceptionPersistence(
                    "Database lock timeout occurred",
                    "MYSQL_LOCK_001"
                );
            default:
                return new DomainExceptionPersistence(
                    "Unexpected MySQL error: " + ex.getMessage(),
                    "MYSQL_GENERIC_001"
                );
        }
    }

    public DomainExceptionPersistence handleMongoError(MongoException ex) {
        logger.error("MongoDB error occurred: {}", ex.getMessage(), ex);

        if (ex instanceof DuplicateKeyException) {
            return new DomainExceptionPersistence(
                "A duplicate record already exists in MongoDB",
                "MONGO_DUPLICATE_001"
            );
        }

        if (ex instanceof MongoTimeoutException) {
            return new DomainExceptionPersistence(
                "MongoDB operation timed out",
                "MONGO_TIMEOUT_001"
            );
        }

        if (ex instanceof MongoWriteException) {
            MongoWriteException writeEx = (MongoWriteException) ex;
            switch (writeEx.getError().getCode()) {
                case 11000: // Duplicate key
                    return new DomainExceptionPersistence(
                        "Duplicate key violation in MongoDB",
                        "MONGO_DUPLICATE_002"
                    );
                case 13: // Authentication failed
                    return new DomainExceptionPersistence(
                        "MongoDB authentication failed",
                        "MONGO_AUTH_001"
                    );
                default:
                    return new DomainExceptionPersistence(
                        "MongoDB write operation failed: " + writeEx.getMessage(),
                        "MONGO_WRITE_001"
                    );
            }
        }

        return new DomainExceptionPersistence(
            "Unexpected MongoDB error: " + ex.getMessage(),
            "MONGO_GENERIC_001"
        );
    }
}
