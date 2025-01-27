package ai.shreds.domain.ports;

import ai.shreds.domain.value_objects.DomainValueSavedIDs;

public interface DomainInputPortSaveMessage {
    DomainValueSavedIDs saveMessage(String message);
}
