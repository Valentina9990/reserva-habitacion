package com.microservices.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;

public class MongoDatabaseConnection {
    private static MongoDatabaseConnection instance;
    private final MongoClient mongoClient;
    private final MongoDatabase database;

    private MongoDatabaseConnection() {
        String connectionString = System.getenv("MONGO_CONNECTION_STRING");
        String databaseName = System.getenv("MONGO_DATABASE_NAME");

        if (connectionString == null) {
            connectionString = "mongodb://localhost:27017";
        }
        if (databaseName == null) {
            databaseName = "reservas";
        }

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();

        this.mongoClient = MongoClients.create(settings);
        this.database = mongoClient.getDatabase(databaseName);
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public static MongoDatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (MongoDatabaseConnection.class) {
                if (instance == null) {
                    instance = new MongoDatabaseConnection();
                }
            }
        }
        return instance;
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    public boolean isConnected() {
        try {
            mongoClient.listDatabaseNames().first();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}