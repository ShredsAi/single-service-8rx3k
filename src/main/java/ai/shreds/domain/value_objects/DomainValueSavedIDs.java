package ai.shreds.domain.value_objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DomainValueSavedIDs {
    private final Long mysqlId;
    private final String mongoId;
}
