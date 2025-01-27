package ai.shreds.domain.entities;

import ai.shreds.domain.value_objects.DomainValueSavedIDs;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DomainEntityDocument {
    private final String id;
    private final String messageText;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public DomainValueSavedIDs toValueObject() {
        return new DomainValueSavedIDs(null, id);
    }
}
