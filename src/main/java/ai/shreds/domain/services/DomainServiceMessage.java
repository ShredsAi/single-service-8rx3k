package ai.shreds.domain.services;

import ai.shreds.domain.ports.DomainInputPortSaveMessage;
import ai.shreds.domain.ports.DomainOutputPortMongoRepository;
import ai.shreds.domain.ports.DomainOutputPortMySQLRepository;
import ai.shreds.domain.value_objects.DomainValueSavedIDs;
import ai.shreds.domain.exceptions.DomainExceptionPersistence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DomainServiceMessage implements DomainInputPortSaveMessage {
    private final DomainOutputPortMySQLRepository mySQLRepo;
    private final DomainOutputPortMongoRepository mongoRepo;

    @Override
    public DomainValueSavedIDs saveMessage(String message) {
        Long mysqlId = mySQLRepo.saveToMySql(message);
        try {
            String mongoId = mongoRepo.saveToMongo(message);
            return new DomainValueSavedIDs(mysqlId, mongoId);
        } catch (Exception e) {
            throw new DomainExceptionPersistence("Failed to save message to MongoDB", e);
        }
    }
}
