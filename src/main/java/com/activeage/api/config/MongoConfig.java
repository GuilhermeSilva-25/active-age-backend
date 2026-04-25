package com.activeage.api.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

/**
 * Configuração manual do MongoDB.
 * Sobrescreve a autoconfiguração do Spring Boot para garantir que a conexão
 * seja feita com o Atlas na nuvem e nunca no localhost padrão.
 */
@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {
    @Value("${MONGO_URI}")
    private String mongoUri;

    @Override
    protected String getDatabaseName() {
        return "active_age_db";
    }

    @Override
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(mongoUri);

        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        return MongoClients.create(mongoClientSettings);
    }
}