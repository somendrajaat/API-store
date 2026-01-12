package com.api_store.service;


import com.api_store.domain.user.UserEntity;
import com.api_store.dto.request.LoginRequest;
import com.api_store.dto.request.RegisterRequest;
import com.api_store.dto.response.AuthResponse;
import com.api_store.repository.UserRepository;
import com.api_store.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

import static com.api_store.util.Role.USER;

@Service
public class AuthService {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private final AuthenticationManager authManager;
    @Autowired
    UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authManager, PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest dto) {

        String hash=passwordEncoder.encode(dto.getPassword());
        UserEntity user=new UserEntity(dto.getEmail(),hash,USER);
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest dto) {
        Authentication authentication =
                authManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                dto.getEmail(),
                                dto.getPassword()
                        )
                );

        UserDetails user =
                (UserDetails) authentication.getPrincipal();

        return new AuthResponse(jwtUtil.generateToken(user));
    }
}
