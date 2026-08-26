package com.ajay.employee_management.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;

import com.ajay.employee_management.security.JwtAuthenticationFilter;
import com.ajay.employee_management.service.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    public SecurityConfig(
            CustomUserDetailsService customUserDetailsService,
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.customUserDetailsService = customUserDetailsService;

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }


    // ==============================
    // Password Encoder
    // ==============================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    // ==============================
    // Authentication Manager
    // ==============================

    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http,
            PasswordEncoder passwordEncoder)
            throws Exception {

        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder);

        return authenticationManagerBuilder.build();
    }


    // ==============================
    // CORS Configuration
    // ==============================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://127.0.0.1:5500",
                "http://localhost:5500",
                "https://employee-management-production-1a07.up.railway.app"
        ));

        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type"
        ));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }


    // ==============================
    // Security Filter Chain
    // ==============================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
            		.requestMatchers(
            		        PathRequest.toStaticResources().atCommonLocations()
            		    ).permitAll()

                // ==============================
                // FRONTEND - PUBLIC
                // ==============================

                .requestMatchers(
                        "/",
                        "/login.html",
                        "/login.js",
                        "/index.html",
                        "/script.js",
                        "/style.css",
                        "/background.png"
                ).permitAll()


                // ==============================
                // AUTH - PUBLIC
                // ==============================

                .requestMatchers("/auth/**")
                .permitAll()


                // ==============================
                // ADMIN ONLY
                // ==============================

                .requestMatchers(
                        HttpMethod.POST,
                        "/employees/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                        HttpMethod.PUT,
                        "/employees/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                        HttpMethod.DELETE,
                        "/employees/**"
                ).hasRole("ADMIN")


                // ==============================
                // ADMIN + USER
                // ==============================

                .requestMatchers(
                        HttpMethod.GET,
                        "/employees/**"
                ).hasAnyRole("ADMIN", "USER")


                // ==============================
                // EVERYTHING ELSE
                // ==============================

                .anyRequest()
                .authenticated()
            )


            // ==============================
            // JWT FILTER
            // ==============================

            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            );


        return http.build();
    }
}