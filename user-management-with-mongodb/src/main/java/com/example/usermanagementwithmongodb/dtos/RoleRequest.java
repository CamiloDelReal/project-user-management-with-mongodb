package com.example.usermanagementwithmongodb.dtos;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RoleRequest {
    @NotNull(message = "Role id cannot be null")
    private String id;
}
