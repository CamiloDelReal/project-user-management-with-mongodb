package com.example.usermanagementwithmongodb.seeders;

import com.example.usermanagementwithmongodb.entities.Role;
import com.example.usermanagementwithmongodb.entities.User;
import com.example.usermanagementwithmongodb.repositories.RoleRepository;
import com.example.usermanagementwithmongodb.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DatabaseSeeder {

    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Autowired
    public DatabaseSeeder(BCryptPasswordEncoder passwordEncoder, RoleRepository roleRepository, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @EventListener
    public void seedDatabase(ContextRefreshedEvent event) {
        seedRoles();
        seedUsers();
    }

    private void seedRoles() {
        if(roleRepository.count() == 0) {
            Role role = new Role(Role.ADMINISTRATOR);
            roleRepository.save(role);

            role = new Role(Role.GUEST);
            roleRepository.save(role);
        }
    }

    private void seedUsers() {
        if(userRepository.count() == 0) {
            Role admin = roleRepository.findByName(Role.ADMINISTRATOR).orElse(null);
            User user = new User("Root", "Admin", "root@gmail.com", passwordEncoder.encode("root"), Set.of(admin));
            userRepository.save(user);
        }
    }

}
