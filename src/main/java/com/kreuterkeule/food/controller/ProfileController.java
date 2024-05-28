package com.kreuterkeule.food.controller;

import com.kreuterkeule.food.config.JwtTokenUtil;
import com.kreuterkeule.food.dto.RegisterUserDto;
import com.kreuterkeule.food.entity.UserEntity;
import com.kreuterkeule.food.repository.UserRepository;
import com.kreuterkeule.food.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile/")
public class ProfileController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenUtil tokenUtil;

    @GetMapping("/getInfo")
    public ResponseEntity<UserEntity> getInfo() {
        UserDetails ud = userService.getUser();
        UserEntity ue = userRepository.findByUsername(ud.getUsername()).orElse(null);
        if (ue == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ue, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<UserEntity> register(@RequestBody RegisterUserDto registerUserDto) {
        UserDetails ud = userService.createUser(registerUserDto.username, registerUserDto.password, registerUserDto.email);
        UserEntity ue = userRepository.findByUsername(ud.getUsername()).orElse(null);
        if (ue == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ue, HttpStatus.OK);
    }

    @GetMapping("/token")
    public ResponseEntity<String> token() {
        UserDetails ud = userService.getUser();
        String token = tokenUtil.generateToken(ud.getUsername());
        System.out.println(token);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}
