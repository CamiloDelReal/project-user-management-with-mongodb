package com.example.usermanagementwithmongodb.services;

import com.example.usermanagementwithmongodb.dtos.LoginRequest;
import com.example.usermanagementwithmongodb.dtos.LoginResponse;
import com.example.usermanagementwithmongodb.dtos.UserRequest;
import com.example.usermanagementwithmongodb.dtos.UserResponse;
import com.example.usermanagementwithmongodb.entities.Role;
import com.example.usermanagementwithmongodb.entities.User;
import com.example.usermanagementwithmongodb.repositories.RoleRepository;
import com.example.usermanagementwithmongodb.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final AuthenticationManager authenticationManager;
    private final ModelMapper mapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${security.token-key}")
    private String tokenKey;
    @Value("${security.token-type}")
    private String tokenType;
    @Value("${security.separator}")
    private String separator;
    @Value("${security.validity}")
    private Long validity;
    @Value("${security.authorities-key}")
    private String authoritiesKey;

    @Autowired
    public UserService(@Lazy AuthenticationManager authenticationManager, ModelMapper mapper, UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElse(null);
        UserDetails userDetails = null;
        if(user != null) {
            List<GrantedAuthority> authorities = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
            userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getProtectedPassword(), true, true, true, true, authorities);
        }
        return userDetails;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        LoginResponse response = null;
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            User user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);
            if(user != null) {
                String rolesClaim = user.getRoles().stream().map(r -> r.getName()).collect(Collectors.joining());
                String subject = String.join(separator, String.valueOf(user.getId()), user.getEmail());
                Claims claims = Jwts.claims();
                claims.put(authoritiesKey, rolesClaim);
                long currentTime = System.currentTimeMillis();
                String token = Jwts.builder()
                        .setClaims(claims)
                        .setSubject(subject)
                        .setIssuedAt(new Date(currentTime))
                        .setExpiration(new Date(currentTime + validity))
                        .signWith(SignatureAlgorithm.HS256, tokenKey)
                        .compact();
                response = new LoginResponse(user.getEmail(), tokenType, token);
            }
        } catch (Exception ex) {
            logger.error("Exception captured", ex);
        }
        return response;
    }

    public List<UserResponse> getAllUsers() {
        List<UserResponse> response = null;
        List<User> users = userRepository.findAll();
        if(!users.isEmpty()) {
            response = users.stream().map(u -> mapper.map(u, UserResponse.class)).collect(Collectors.toList());
        } else {
            response = new ArrayList<>();
        }
        return response;
    }

    public UserResponse getUserById(String id) {
        UserResponse response = null;
        User user = userRepository.findById(new ObjectId(id)).orElse(null);
        System.out.println("User by id " + user);
        if(user != null) {
            response = mapper.map(user, UserResponse.class);
        }
        return response;
    }

    public boolean isEmailAvailable(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        return user == null;
    }

    public UserResponse createUser(UserRequest userRequest) {
        User user = mapper.map(userRequest, User.class);
        user.setProtectedPassword(passwordEncoder.encode(userRequest.getPassword()));
        if(userRequest.getRoles() == null || userRequest.getRoles().isEmpty()) {
            Role guestRole = roleRepository.findByName("Guest").orElse(null);
            user.setRoles(Set.of(guestRole));
        } else {
            Set<Role> roles = userRequest.getRoles().stream().map(it -> roleRepository.findById(it.getId()).orElse(null)).filter(Objects::nonNull).collect(Collectors.toSet());
            user.setRoles(roles);
        }
        userRepository.save(user);
        UserResponse response = mapper.map(user, UserResponse.class);
        return response;
    }

    public boolean createUserRequestHasAdminRole(UserRequest userRequest) {
        Role adminRole = roleRepository.findByName("Administrator").orElse(null);
        return userRequest.getRoles() != null && userRequest.getRoles().stream().anyMatch(it -> Objects.equals(it.getId(), adminRole.getId()));
    }

    public boolean createUserRequestHasAdminRole(User user) {
        Role adminRole = roleRepository.findByName("Administrator").orElse(null);
        return user.getRoles() != null && user.getRoles().stream().anyMatch(it -> Objects.equals(it.getId(), adminRole.getId()));
    }

    public UserResponse editUser(String id, UserRequest userRequest) {
        User user = userRepository.findById(new ObjectId(id)).orElse(null);
        UserResponse response = null;
        if(user != null) {
            user.setFirstName(userRequest.getFirstName());
            user.setLastName(userRequest.getLastName());
            user.setEmail(userRequest.getEmail());
            user.setProtectedPassword(passwordEncoder.encode(userRequest.getPassword()));
            if(userRequest.getRoles() != null && !userRequest.getRoles().isEmpty()) {
                Set<Role> roles = userRequest.getRoles().stream().map(it -> roleRepository.findById(it.getId()).orElse(null)).filter(Objects::nonNull).collect(Collectors.toSet());
                user.setRoles(roles);
            }
            userRepository.save(user);
            response = mapper.map(user, UserResponse.class);
        }
        return response;
    }

    public boolean deleteUser(String id) {
        boolean success = false;
        User user = userRepository.findById(new ObjectId(id)).orElse(null);
        if(user != null) {
            userRepository.delete(user);
            success = true;
        }
        return success;
    }

}
