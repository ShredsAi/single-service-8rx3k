package ai.shreds.infrastructure.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class InfrastructureConfigMongo {

    @Bean
    public MongoClient mongoClient() {
        // Using empty connection string as we're not actually connecting to MongoDB
        return MongoClients.create();
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoClient(), "dummy");
    }
}
