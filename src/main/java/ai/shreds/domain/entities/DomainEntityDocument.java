package ai.shreds.domain.entities;

import ai.shreds.domain.exceptions.DomainExceptionValidation;
import ai.shreds.domain.value_objects.DomainValueSavedIDs;

public class DomainEntityDocument {
    private static final int MAX_MESSAGE_LENGTH = 255;
    private static final int MONGODB_OBJECTID_LENGTH = 24;

    private String id;
    private String messageText;

    public DomainEntityDocument(String messageText) {
        validateMessage(messageText);
        this.messageText = messageText;
    }

    public DomainEntityDocument(String id, String messageText) {
        validateMessage(messageText);
        validateMongoId(id);
        this.id = id;
        this.messageText = messageText;
    }

    private void validateMessage(String messageText) {
        if (messageText == null || messageText.trim().isEmpty()) {
            throw new DomainExceptionValidation("Message text cannot be null or empty");
        }
        if (messageText.length() > MAX_MESSAGE_LENGTH) {
            throw new DomainExceptionValidation("Message text cannot exceed " + MAX_MESSAGE_LENGTH + " characters");
        }
    }

    private void validateMongoId(String id) {
        if (id != null) {
            if (id.trim().isEmpty()) {
                throw new DomainExceptionValidation("MongoDB ID cannot be empty when provided");
            }
            if (id.length() != MONGODB_OBJECTID_LENGTH) {
                throw new DomainExceptionValidation("MongoDB ID must be " + MONGODB_OBJECTID_LENGTH + " characters long");
            }
            if (!id.matches("^[0-9a-fA-F]{24}$")) {
                throw new DomainExceptionValidation("MongoDB ID must be a valid hexadecimal string");
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        validateMongoId(id);
        this.id = id;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        validateMessage(messageText);
        this.messageText = messageText;
    }

    public DomainValueSavedIDs toValueObject(Long mysqlId) {
        if (this.id == null) {
            throw new DomainExceptionValidation("Cannot create value object: MongoDB ID is not set");
        }
        return new DomainValueSavedIDs(mysqlId, this.id);
    }

    @Override
    public String toString() {
        return "DomainEntityDocument{" +
                "id='" + id + '\'' +
                ", messageText='" + messageText + '\'' +
                '}';
    }
}
