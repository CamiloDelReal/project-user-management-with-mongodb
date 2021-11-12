package com.example.usermanagementwithmongodb.services;

import com.example.usermanagementwithmongodb.dtos.RoleResponse;
import com.example.usermanagementwithmongodb.entities.Role;
import com.example.usermanagementwithmongodb.repositories.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(ModelMapper modelMapper, RoleRepository roleRepository) {
        this.modelMapper = modelMapper;
        this.roleRepository = roleRepository;
    }

    public List<RoleResponse> getAll() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(r -> modelMapper.map(r, RoleResponse.class)).collect(Collectors.toList());
    }

}
