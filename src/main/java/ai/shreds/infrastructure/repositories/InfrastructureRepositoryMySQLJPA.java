package ai.shreds.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InfrastructureRepositoryMySQLJPA extends JpaRepository<InfrastructureEntityMessage, Long> {

    /**
     * Check if a message with the given text already exists
     */
    boolean existsByMessageText(String messageText);

    /**
     * Find a message by its exact text
     */
    Optional<InfrastructureEntityMessage> findByMessageText(String messageText);

    /**
     * Find messages created between two dates
     */
    @Query("SELECT m FROM InfrastructureEntityMessage m WHERE m.createdAt BETWEEN :startDate AND :endDate")
    List<InfrastructureEntityMessage> findByCreatedAtBetween(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find messages containing specific text (case-insensitive)
     */
    @Query("SELECT m FROM InfrastructureEntityMessage m WHERE LOWER(m.messageText) LIKE LOWER(CONCAT('%', :text, '%'))")
    List<InfrastructureEntityMessage> findByMessageTextContainingIgnoreCase(@Param("text") String text);

    /**
     * Delete messages older than a specific date
     */
    @Query("DELETE FROM InfrastructureEntityMessage m WHERE m.createdAt < :date")
    void deleteMessagesOlderThan(@Param("date") LocalDateTime date);

    /**
     * Find the most recent messages, limited by count
     */
    @Query(value = "SELECT m FROM InfrastructureEntityMessage m ORDER BY m.createdAt DESC")
    List<InfrastructureEntityMessage> findMostRecent(@Param("limit") int limit);
}
