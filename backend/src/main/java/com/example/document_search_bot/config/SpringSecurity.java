package com.example.document_search_bot.config;


import com.example.document_search_bot.filter.JwtFilter;
import com.example.document_search_bot.service.CustomUserDetailsServiceImpl;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.swing.*;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SpringSecurity {


    @Autowired
     @Lazy
    JwtFilter jwtFilter;
    @Autowired
    private CustomUserDetailsServiceImpl userDetailsService;

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http.authorizeHttpRequests(request -> request
                                .requestMatchers("/public/**").permitAll()
                                .requestMatchers( "/api/qna/ask").hasAnyRole("USER","ADMIN")
                                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()// Public access
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // 🛡️ Allow preflight!
                                .requestMatchers("/api/document/**","/api/user").hasRole("ADMIN")
                                .requestMatchers("/error").permitAll()// Admin-only
                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/v3/api-docs/**",
                                        "/v3/api-docs/swagger-config",
                                        "/favicon.ico",
                                        "/error"
                                ).permitAll()

                                .anyRequest().authenticated()
                        // Everything else: auth required
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * HttpSecurity http
     *      │
     *      ▼
     * authorizeHttpRequests(...)  ← Configures authorization rules
     *      │
     *      ▼
     *    authz (lambda)
     *      ├── requestMatchers("/hello").permitAll()
     *      └── anyRequest().authenticated()
     *      │
     *      ▼
     *   Returns modified HttpSecurity
     *      │
     *      ▼
     *    formLogin(...)  ← Configures login form
     *      │
     *      ▼
     *    Returns modified HttpSecurity
     *      │
     *      ▼
     *    build()         ← Builds and returns SecurityFilterChain
     *    session cookie jsessionid is used to manage the session automatically by SS.
     *
     */


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000")); // Frontend origin
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // If you're using cookies or Authorization headers

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        // Defines BCrypt as the password encoder for securing and verifying passwords
        return new BCryptPasswordEncoder();
    }

}
/**
 *    | Component                      | Purpose                                                               |
 *   | ------------------------------ | --------------------------------------------------------------------- |
 *   | `AuthenticationManager`        | Handles login authentication — verifies username/password credentials |
 *   | `AuthenticationManagerBuilder` | Used to build the `AuthenticationManager` with your configuration     |
 *   | `UserDetailsService`           | A service that loads user data (e.g., from a DB) by username          |
 *   | `PasswordEncoder`              | Securely hashes passwords (BCrypt is a strong industry standard)      |
 *
 *
 *   | Line                                  | Action                                                                   |
 *   | ------------------------------------- | ------------------------------------------------------------------------ |
 *   | `http.getSharedObject(...)`           | Accesses internal `AuthenticationManagerBuilder` used by Spring Security |
 *   | `authBuilder.userDetailsService(...)` | Tells Spring where to load user credentials from                         |
 *   | `authBuilder.passwordEncoder(...)`    | Tells Spring how to hash and check passwords securely                    |
 *   | `return authBuilder.build();`         | Returns a fully built `AuthenticationManager` with all settings applied  |
 *   | `BCryptPasswordEncoder()`             | Uses bcrypt, a secure hashing algorithm, to encode user passwords        |
 *
 *             ┌─────────────────────────────┐
 *             │     HttpSecurity (injected) │
 *             └────────────┬────────────────┘
 *                          │
 *                          ▼
 *          ┌────────────────────────────────────┐
 *          │ getSharedObject(AuthenticationManagerBuilder.class) │
 *          └────────────────────────────────────┘
 *                          │
 *                          ▼
 *      ┌──────────────────────────────────────┐
 *      │   AuthenticationManagerBuilder       │
 *      └──────────────────────────────────────┘
 *                          │
 *                          ├── set UserDetailsService → customUserDetailsService
 *                          │
 *                          └── set PasswordEncoder → BCryptPasswordEncoder
 *                          │
 *                          ▼
 *         ┌─────────────────────────────────┐
 *         │     build()                     │
 *         │  (creates AuthenticationManager)│
 *         └─────────────────────────────────┘
 *                          │
 *                          ▼
 *           ┌─────────────────────────────┐
 *           │ AuthenticationManager (Bean)│
 *           └─────────────────────────────┘
 */
//spring security hierarchy
