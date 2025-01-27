package ai.shreds.domain.entities;

import ai.shreds.domain.exceptions.DomainExceptionValidation;
import ai.shreds.domain.value_objects.DomainValueSavedIDs;

public class DomainEntityMessage {
    private static final int MAX_MESSAGE_LENGTH = 255;
    
    private Long id;
    private String messageText;

    public DomainEntityMessage(String messageText) {
        validateMessage(messageText);
        this.messageText = messageText;
    }

    public DomainEntityMessage(Long id, String messageText) {
        validateMessage(messageText);
        validateId(id);
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

    private void validateId(Long id) {
        if (id != null && id <= 0) {
            throw new DomainExceptionValidation("ID must be positive when provided");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        validateId(id);
        this.id = id;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        validateMessage(messageText);
        this.messageText = messageText;
    }

    public DomainValueSavedIDs toValueObject(String mongoId) {
        if (this.id == null) {
            throw new DomainExceptionValidation("Cannot create value object: MySQL ID is not set");
        }
        return new DomainValueSavedIDs(this.id, mongoId);
    }

    @Override
    public String toString() {
        return "DomainEntityMessage{" +
                "id=" + id +
                ", messageText='" + messageText + '\'' +
                '}';
    }
}
