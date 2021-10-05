package com.example.usermanagementwithmongodb.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "roles")
public class Role {
    @Id
    private String id;
    private String name;

    public Role(String name) {
        this.name = name;
    }

}
