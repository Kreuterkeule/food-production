package com.kreuterkeule.food.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;

@Configuration
@EnableWebMvc
public class SecurityConfig {

    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    public void setJwtTokenFilter(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    public SecurityConfig() {}

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic(Customizer.withDefaults())
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests((request) -> {
                    try {
                            request
                                    .requestMatchers("/profile/register", "/profile/login", "/app/get_daily").permitAll()
                                    .requestMatchers("/admin/**").hasRole("ADMIN")
                                .anyRequest().authenticated()
                                    .and()
                                    .addFilterBefore(
                                            jwtTokenFilter,
                                            UsernamePasswordAuthenticationFilter.class
                                    );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        return http.build();
    }

}