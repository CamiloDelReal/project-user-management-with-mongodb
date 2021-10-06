package com.example.usermanagementwithmongodb.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "users")
public class User {
    @Id
    @Indexed
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String protectedPassword;
    private Set<Role> roles;

    public User(String firstName, String lastName, String email, String protectedPassword, Set<Role> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.protectedPassword = protectedPassword;
        this.roles = roles;
    }

}
