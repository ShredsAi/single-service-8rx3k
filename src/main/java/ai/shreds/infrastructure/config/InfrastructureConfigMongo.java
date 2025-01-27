package ai.shreds.infrastructure.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.StringUtils;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableMongoRepositories(basePackages = "ai.shreds.infrastructure.repositories")
public class InfrastructureConfigMongo {

    private MongoClient mongoClient;

    @Bean
    public MongoClient mongoClient(@Value("${spring.data.mongodb.uri}") String uri) {
        validateMongoConfiguration(uri);
        
        ConnectionString connectionString = new ConnectionString(uri);
        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .applyToConnectionPoolSettings(builder ->
                builder.maxSize(20)
                       .minSize(5)
                       .maxWaitTime(2000, TimeUnit.MILLISECONDS)
                       .maxConnectionLifeTime(30, TimeUnit.MINUTES)
            )
            .applyToSocketSettings(builder ->
                builder.connectTimeout(2000, TimeUnit.MILLISECONDS)
                       .readTimeout(5000, TimeUnit.MILLISECONDS)
            )
            .build();

        this.mongoClient = MongoClients.create(settings);
        return this.mongoClient;
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient, 
            @Value("${spring.data.mongodb.database}") String database) {
        validateDatabaseName(database);
        return new MongoTemplate(mongoClient, database);
    }

    private void validateMongoConfiguration(String uri) {
        if (!StringUtils.hasText(uri)) {
            throw new IllegalStateException("MongoDB URI must be configured");
        }
        if (!uri.startsWith("mongodb://") && !uri.startsWith("mongodb+srv://")) {
            throw new IllegalStateException("Invalid MongoDB URI format");
        }
    }

    private void validateDatabaseName(String database) {
        if (!StringUtils.hasText(database)) {
            throw new IllegalStateException("MongoDB database name must be configured");
        }
        if (database.contains(" ") || database.contains("/")) {
            throw new IllegalStateException("Invalid MongoDB database name");
        }
    }

    @PreDestroy
    public void cleanup() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
