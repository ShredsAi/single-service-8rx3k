package ai.shreds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication(
    exclude = {
        // Excluding database auto-configuration to allow application to start without database connections
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class
    }
)
@EnableRetry
public class MessagePersisterApplication {
    public static void main(String[] args) {
        SpringApplication.run(MessagePersisterApplication.class, args);
    }
}
