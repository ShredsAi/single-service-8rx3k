package ai.shreds.domain.ports;

public interface DomainOutputPortMongoRepository {
    String saveToMongo(String message);
    void deleteById(String id);
    boolean existsById(String id);
    boolean isValidObjectId(String id);
}
