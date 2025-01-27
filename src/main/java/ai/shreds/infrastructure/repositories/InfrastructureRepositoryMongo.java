package ai.shreds.infrastructure.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InfrastructureRepositoryMongo extends MongoRepository<InfrastructureEntityDocument, String> {

    /**
     * Find a document by its exact message text
     */
    Optional<InfrastructureEntityDocument> findByMessageText(String messageText);

    /**
     * Check if a document exists with the given message text
     */
    boolean existsByMessageText(String messageText);

    /**
     * Find documents created between two dates
     */
    @Query("{'created_at': {$gte: ?0, $lte: ?1}}")
    List<InfrastructureEntityDocument> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Find documents containing specific text (case-insensitive)
     */
    @Query("{'message_text': {$regex: ?0, $options: 'i'}}")
    List<InfrastructureEntityDocument> findByMessageTextContainingIgnoreCase(String text);

    /**
     * Delete documents older than a specific date
     */
    @Query(value = "{'created_at': {$lt: ?0}}", delete = true)
    void deleteDocumentsOlderThan(LocalDateTime date);

    /**
     * Find the most recent documents, limited by count
     */
    @Query(value = "{}", sort = "{created_at: -1}")
    List<InfrastructureEntityDocument> findMostRecent(int count);
}
