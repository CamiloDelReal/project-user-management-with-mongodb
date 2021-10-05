package com.example.usermanagementwithmongodb.repositories;

import com.example.usermanagementwithmongodb.entities.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends MongoRepository<Role, Long> {

    Optional<Role> findByName(String name);

}
