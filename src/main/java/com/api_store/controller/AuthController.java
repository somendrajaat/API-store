package com.api_store.controller;

import com.api_store.dto.request.LoginRequest;
import com.api_store.dto.request.RegisterRequest;
import com.api_store.dto.response.AuthResponse;
import com.api_store.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest dto) {
        // Registration logic goes here
        authService.register(dto);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest dto) {
        // Login logic goes here
        AuthResponse authResponse = authService.login(dto);
        if (authResponse == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        return ResponseEntity.ok().body(authResponse);
    }

    @GetMapping("/me")
    public String check(){
        return "Auth Service is running";
    }


}
