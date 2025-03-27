package com.studbuds.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // We disable CSRF for simplicity
        http.csrf().disable()
                // Stateless session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                  // Allow public (unauthenticated) access to signup & login
                  .antMatchers("/api/auth/signup", "/api/auth/login").permitAll()
                  // All other endpoints require auth
                  .anyRequest().authenticated()
                .and()
                // Register our FirebaseAuthenticationFilter
                .addFilterBefore(new FirebaseAuthenticationFilter(), 
                                 UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}