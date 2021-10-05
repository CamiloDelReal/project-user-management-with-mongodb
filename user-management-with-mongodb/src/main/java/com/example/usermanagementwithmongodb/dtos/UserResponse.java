package com.example.usermanagementwithmongodb.dtos;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Set<RoleResponse> roles;
}
