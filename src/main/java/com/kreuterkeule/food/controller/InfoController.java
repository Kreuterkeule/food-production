package com.kreuterkeule.food.controller;

import com.kreuterkeule.food.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/info")
public class InfoController {

    @Autowired
    private UserService userService;

    @GetMapping("/access")
    public String root() {
        return "success";
    }

    @GetMapping("/roles")
    public String getRoles() {
        UserDetails user = userService.getAuthenticatedUser();
        return user.getAuthorities().toString();
    }

}
