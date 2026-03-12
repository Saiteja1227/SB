package com.example.firstproject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.firstproject.filter.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configure(http))
                .sessionManagement(session -> 
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no authentication required
                        .requestMatchers(HttpMethod.POST, "/api/orders", "/orders").permitAll()  // Allow order creation
                        .requestMatchers(HttpMethod.GET, "/api/orders/user/**", "/orders/user/**").permitAll()  // Allow fetching user orders
                        .requestMatchers(
                                "/",
                                "/api/auth/**",
                                "/api/register",
                                "/api/login",
                                "/api/products/**",
                                "/api/debug/**",
                                "/debug/**",
                                "/register",
                                "/login",
                                "/view",
                                "/viewAll"
                        ).permitAll()
                        // Protected endpoints - authentication required  
                        .requestMatchers(
                                "/api/cart/**",
                                "/api/update",
                                "/api/delete"
                        ).authenticated()
                        // All other endpoints permit all
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
