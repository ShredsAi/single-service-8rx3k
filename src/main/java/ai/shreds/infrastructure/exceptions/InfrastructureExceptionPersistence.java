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
                    ex
                );
            }
            return new DomainExceptionPersistence(
                "Data integrity violation occurred",
                ex
            );
        }

        if (ex instanceof SQLTimeoutException) {
            return new DomainExceptionPersistence(
                "Database operation timed out",
                ex
            );
        }

        // Handle specific MySQL error codes
        switch (ex.getErrorCode()) {
            case 1040: // Too many connections
                return new DomainExceptionPersistence(
                    "Database connection pool exhausted",
                    ex
                );
            case 1064: // SQL syntax error
                return new DomainExceptionPersistence(
                    "Invalid database query",
                    ex
                );
            case 1205: // Lock wait timeout
                return new DomainExceptionPersistence(
                    "Database lock timeout occurred",
                    ex
                );
            default:
                return new DomainExceptionPersistence(
                    "Unexpected MySQL error: " + ex.getMessage(),
                    ex
                );
        }
    }

    public DomainExceptionPersistence handleMongoError(MongoException ex) {
        logger.error("MongoDB error occurred: {}", ex.getMessage(), ex);

        if (ex instanceof DuplicateKeyException) {
            return new DomainExceptionPersistence(
                "A duplicate record already exists in MongoDB",
                ex
            );
        }

        if (ex instanceof MongoTimeoutException) {
            return new DomainExceptionPersistence(
                "MongoDB operation timed out",
                ex
            );
        }

        if (ex instanceof MongoWriteException) {
            MongoWriteException writeEx = (MongoWriteException) ex;
            switch (writeEx.getError().getCode()) {
                case 11000: // Duplicate key
                    return new DomainExceptionPersistence(
                        "Duplicate key violation in MongoDB",
                        writeEx
                    );
                case 13: // Authentication failed
                    return new DomainExceptionPersistence(
                        "MongoDB authentication failed",
                        writeEx
                    );
                default:
                    return new DomainExceptionPersistence(
                        "MongoDB write operation failed: " + writeEx.getMessage(),
                        writeEx
                    );
            }
        }

        return new DomainExceptionPersistence(
            "Unexpected MongoDB error: " + ex.getMessage(),
            ex
        );
    }
}
