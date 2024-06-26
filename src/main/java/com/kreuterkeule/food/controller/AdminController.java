package com.kreuterkeule.food.controller;

import com.kreuterkeule.food.dto.CreateAdminDto;
import com.kreuterkeule.food.entity.UserEntity;
import com.kreuterkeule.food.repository.UserRepository;
import com.kreuterkeule.food.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserDetailsManager users;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public ResponseEntity<String> root() {
        return new ResponseEntity<String>("200 OK", HttpStatus.OK);
    }

    @PutMapping("/admin")
    public ResponseEntity<UserDetails> createAdmin(@RequestBody CreateAdminDto createAdminDto) {
        if (users.userExists(createAdminDto.username)) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        UserDetails user = userService.createAdmin(createAdminDto.username, createAdminDto.password);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/user")
    public ResponseEntity<UserDetails> createUser(@RequestBody CreateAdminDto createAdminDto) {
        if (users.userExists(createAdminDto.username)) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        UserDetails user = userService.createUser(createAdminDto.username, createAdminDto.password);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping(value = {"/user", "/admin"}) // works for admins too :)
    public ResponseEntity<UserDetails> deleteUser(@RequestParam("username") String username) {
        if (!users.userExists(username)) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        UserDetails user = userService.deleteUser(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/users/all")
    public ResponseEntity<List<UserEntity>> getUsersAll() {
        List<UserEntity> userEntities = userRepository.findAll();
        return new ResponseEntity<>(userEntities, HttpStatus.OK);
    }
}
