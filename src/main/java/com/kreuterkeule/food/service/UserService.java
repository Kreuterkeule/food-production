package com.kreuterkeule.food.service;

import com.kreuterkeule.food.entity.UserEntity;
import com.kreuterkeule.food.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    UserDetailsManager users;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;

    public UserDetails getUser() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return users.loadUserByUsername(name);
    }

    public UserEntity getUserEntity() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(name).orElse(null);
    }

    public List<String> getRoles() {
        UserDetails user = this.getUser();
        return user.getAuthorities().stream().map(e -> e.getAuthority()).collect(Collectors.toList());
    }

    public UserDetails createUser(String username, String password) {
        UserDetails user = User.builder().password(passwordEncoder.encode(password)).username(username).roles("USER").build();
        users.createUser(user);
        UserEntity ue = new UserEntity(username);
        userRepository.save(ue);
        return user;
    }

    public UserDetails createUser(String username, String password, String email) {
        if (users.userExists(username)) return null;
        UserDetails user = User.builder().password(passwordEncoder.encode(password)).username(username).roles("USER").build();
        users.createUser(user);
        UserEntity ue = new UserEntity(username);
        ue.setEmail(email);
        userRepository.save(ue);
        return user;
    }

    public UserDetails createAdmin(String username, String password) {
        if (users.userExists(username)) return null;
        UserDetails user = User.builder().password(passwordEncoder.encode(password)).username(username).roles("ADMIN", "USER").build();
        users.createUser(user);
        return user;
    }

    public UserDetails deleteUser(String username) {
        UserDetails user = users.loadUserByUsername(username);
        users.deleteUser(username);
        UserEntity ue = userRepository.findByUsername(username).orElse(null);
        if (ue != null) {
            userRepository.delete(ue);
        }
        return user;
    }
}
