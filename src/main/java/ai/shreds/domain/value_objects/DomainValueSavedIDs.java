package ai.shreds.domain.value_objects;

import ai.shreds.domain.exceptions.DomainExceptionValidation;

import java.util.Objects;

public final class DomainValueSavedIDs {
    private final Long mysqlId;
    private final String mongoId;

    public DomainValueSavedIDs(Long mysqlId, String mongoId) {
        validateIds(mysqlId, mongoId);
        this.mysqlId = mysqlId;
        this.mongoId = mongoId;
    }

    private void validateIds(Long mysqlId, String mongoId) {
        if (mysqlId == null) {
            throw new DomainExceptionValidation("MySQL ID cannot be null");
        }
        if (mysqlId <= 0) {
            throw new DomainExceptionValidation("MySQL ID must be positive");
        }
        if (mongoId == null || mongoId.trim().isEmpty()) {
            throw new DomainExceptionValidation("MongoDB ID cannot be null or empty");
        }
    }

    public Long getMysqlId() {
        return mysqlId;
    }

    public String getMongoId() {
        return mongoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainValueSavedIDs that = (DomainValueSavedIDs) o;
        return Objects.equals(mysqlId, that.mysqlId) && 
               Objects.equals(mongoId, that.mongoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mysqlId, mongoId);
    }

    @Override
    public String toString() {
        return "DomainValueSavedIDs{" +
                "mysqlId=" + mysqlId +
                ", mongoId='" + mongoId + '\'' +
                '}';
    }
}
