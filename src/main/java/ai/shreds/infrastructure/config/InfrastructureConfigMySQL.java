package ai.shreds.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import jakarta.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "ai.shreds.infrastructure.repositories")
@EntityScan(basePackages = "ai.shreds.infrastructure.entities")
public class InfrastructureConfigMySQL {

    @Value("${spring.datasource.url}")
    private String dataSourceUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    @Primary
    public DataSource dataSource() {
        validateDataSourceConfiguration();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dataSourceUrl);
        config.setUsername(username);
        config.setPassword(password);
        
        // Connection pool settings
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000); // 5 minutes
        config.setConnectionTimeout(20000); // 20 seconds
        config.setMaxLifetime(1200000); // 20 minutes
        
        // Connection test query
        config.setConnectionTestQuery("SELECT 1");
        
        return new HikariDataSource(config);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setPackagesToScan("ai.shreds.infrastructure.entities");
        
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(true);
        vendorAdapter.setGenerateDdl(true);
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.connection.characterEncoding", "utf8");
        properties.put("hibernate.connection.useUnicode", "true");
        properties.put("hibernate.connection.CharSet", "utf8");
        properties.put("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
        
        factoryBean.setJpaPropertyMap(properties);
        
        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    private void validateDataSourceConfiguration() {
        if (dataSourceUrl == null || dataSourceUrl.trim().isEmpty()) {
            throw new IllegalStateException("Database URL must be configured");
        }
        if (!dataSourceUrl.startsWith("jdbc:mysql://")) {
            throw new IllegalStateException("Invalid MySQL JDBC URL format");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalStateException("Database username must be configured");
        }
        if (password == null) {
            throw new IllegalStateException("Database password must be configured");
        }
    }
}
