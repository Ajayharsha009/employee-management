package com.ajay.employee_management.controller;

import com.ajay.employee_management.dto.LoginRequest;
import com.ajay.employee_management.dto.LoginResponse;
import com.ajay.employee_management.entity.User;
import com.ajay.employee_management.service.JwtService;
import com.ajay.employee_management.service.UserService;

import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;


    public AuthController(
            UserService userService,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.userService = userService;

        this.authenticationManager = authenticationManager;

        this.jwtService = jwtService;
    }


    // ==============================
    // REGISTER
    // ==============================

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {

        User savedUser = userService.registerUser(user);

        return ResponseEntity.ok(savedUser);
    }


    // ==============================
    // LOGIN
    // ==============================

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest loginRequest) {


        // Create authentication request

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getUsername(),
                                loginRequest.getPassword()
                        )
                );


        // Get username

        String username = authentication.getName();


        // Get role

        String role = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("USER");


        // Remove ROLE_ prefix

        if (role.startsWith("ROLE_")) {

            role = role.substring(5);

        }


        // Generate JWT

        String token = jwtService.generateToken(
                username,
                role
        );


        // Return JWT

        return ResponseEntity.ok(
                new LoginResponse(token)
        );
    }
}