package com.example.usermanagementwithmongodb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories("com.example.usermanagementwithmongodb.repositories")
public class UserManagementWithMongodbApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserManagementWithMongodbApplication.class, args);
    }

}
