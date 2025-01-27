package ai.shreds.domain.ports;

public interface DomainOutputPortMySQLRepository {
    Long saveToMySql(String message);
    void deleteById(Long id);
    boolean existsById(Long id);
}
