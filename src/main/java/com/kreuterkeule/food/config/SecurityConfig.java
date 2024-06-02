package com.kreuterkeule.food.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebMvc
@EnableWebSecurity
public class SecurityConfig {

    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    public void setJwtTokenFilter(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    public SecurityConfig() {}

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.
                csrf().disable()
                .exceptionHandling()
                .and()
                .httpBasic()
                .and()
                .cors(Customizer.withDefaults())
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests((request) -> {
                    try {
                            request
                                    .requestMatchers(
                                            "/profile/register",
                                            "/app/daily"
                                    ).permitAll()
                                    .requestMatchers(
                                            antMatcher(HttpMethod.GET, "/app/tag"),
                                            antMatcher(HttpMethod.GET, "/app/ingredient"),
                                            antMatcher(HttpMethod.GET, "/app/image/**"),
                                            antMatcher(HttpMethod.GET, "/app/recipe/**"),
                                            antMatcher(HttpMethod.POST, "/app/recipe/**"),
                                            antMatcher(HttpMethod.GET, "/app/user"),
                                            antMatcher(HttpMethod.OPTIONS, "/**"),
                                            antMatcher(HttpMethod.GET, "/app/user/**")
                                    ).permitAll()
                                    .requestMatchers(
                                        antMatcher(HttpMethod.DELETE, "/app/recipe/**")
                                    ).authenticated()
                                    .requestMatchers(
                                            "/v2/api-docs",
                                            "/v3/api-docs",
                                            "/v3/api-docs/**",
                                            "/swagger-resources",
                                            "/swagger-resources/**",
                                            "/configuration/ui",
                                            "/configuration/security",
                                            "/swagger-ui/**",
                                            "/webjars/**",
                                            "/swagger-ui.html"
                                    ).permitAll()
                                    .requestMatchers("/admin/**").hasRole("ADMIN")
                                    .requestMatchers(HttpMethod.DELETE).hasRole("ADMIN")
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
