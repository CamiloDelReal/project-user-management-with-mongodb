package com.example.usermanagementwithmongodb.utils;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class CustomMongoDbContainer extends GenericContainer<CustomMongoDbContainer> {
    public CustomMongoDbContainer(String image, String username, String password, String initialDb) {
        super(DockerImageName.parse(image));
        withEnv("MONGO_INITDB_ROOT_USERNAME", username);
        withEnv("MONGO_INITDB_ROOT_PASSWORD", password);
        withEnv("MONGO_INITDB_DATABASE", initialDb);
        withExposedPorts(27017);
        withReuse(true);
    }
}
