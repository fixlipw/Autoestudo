package com.ufc.blog.controller;

import com.ufc.blog.entity.User;
import com.ufc.blog.entity.UserRole;
import com.ufc.blog.entity.UserStatus;
import com.ufc.blog.exception.BadRequestException;
import com.ufc.blog.exception.TokenRefreshException;
import com.ufc.blog.repository.UserRepository;
import com.ufc.blog.security.JwtService;
import com.ufc.blog.security.RefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Nome de usuário já está em uso");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email já está em uso");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.PENDING);
        user = userRepository.save(user);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody User loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new BadRequestException("Usuário não encontrado."));
        if (!user.getStatus().canLogin()) {
            throw new BadRequestException("Usuário não está ativo. " + user.getStatus().getDescription());
        }
        Map<String, Object> tokensAndUser = jwtService.generateToken(authentication);
        return ResponseEntity.ok(tokensAndUser);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody String refreshToken) {
        Map<String, String> newTokens = jwtService.findByToken(refreshToken)
                .map(jwtService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = jwtService.generateTokenFromUsername(user.getUsername());
                    return Map.of(
                            "accessToken", newAccessToken,
                            "refreshToken", refreshToken);
                })
                .orElseThrow(() -> new TokenRefreshException(refreshToken,
                        "Refresh token não encontrado no banco de dados."));
        return ResponseEntity.ok(newTokens);
    }

}