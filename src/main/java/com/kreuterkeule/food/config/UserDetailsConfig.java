package com.kreuterkeule.food.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.sql.DataSource;

@Configuration
public class UserDetailsConfig {


    @Autowired
    private DataSource dataSource;

    @Value("${spring.security.user.name}")
    private String default_username;
    @Value("${spring.security.user.password}")
    private String default_password;

    @Bean
    UserDetailsManager users(DataSource dataSource) {
        System.out.println();
        UserDetails defaultUser = User.builder()
                .username(this.default_username)
                .password(this.passwordEncoder().encode(this.default_password))
                .roles("ADMIN", "USER")
                .build();
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        try {
            try {
                users.deleteUser(defaultUser.getUsername());
            } catch (Exception e) {
                System.out.println("first initialization detected default user will be created, and can't be deleted before");
                e.printStackTrace();
            }
            users.createUser(defaultUser);
        } catch (Exception e) {
            System.out.println("database not yet initialized");
            e.printStackTrace();
        }
        return users;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
