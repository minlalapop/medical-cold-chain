package com.medical.auth.service;

import com.medical.auth.client.AuditClient;
import com.medical.auth.client.UserClient;
import com.medical.auth.dto.AuthResponse;
import com.medical.auth.dto.CreateUserRequest;
import com.medical.auth.dto.LoginRequest;
import com.medical.auth.dto.RegisterRequest;
import com.medical.auth.entity.AuthUser;
import com.medical.auth.enums.Role;
import com.medical.auth.exception.BadRequestException;
import com.medical.auth.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserClient userClient;
    private final AuditClient auditClient;

    public AuthResponse register(RegisterRequest request) {
        if (request.getRole() == Role.ADMIN) {
            throw new BadRequestException("Admin account cannot be created from registration");
        }

        if (authUserRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists: " + request.getEmail());
        }

        userClient.createUser(new CreateUserRequest(
                request.getFullName(),
                request.getEmail(),
                null,
                request.getRole()
        ));

        AuthUser user = AuthUser.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(true)
                .build();

        AuthUser savedUser = authUserRepository.save(user);
        String token = jwtService.generateToken(savedUser);
        auditClient.log(savedUser.getEmail(), "REGISTER", savedUser.getId(), "User registered with role " + savedUser.getRole());

        return toAuthResponse(savedUser, token);
    }

    public void deleteUserByEmail(String email) {
        AuthUser user = authUserRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Auth user not found with email: " + email));

        if (user.getRole() == Role.ADMIN) {
            throw new BadRequestException("The system admin account cannot be deleted");
        }

        authUserRepository.delete(user);
        auditClient.log(email, "DELETE", user.getId(), "Auth user deleted");
    }

    public AuthResponse login(LoginRequest request) {
        AuthUser user = authUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!user.isEnabled()) {
            throw new BadRequestException("User account is disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);
        auditClient.log(user.getEmail(), "LOGIN", user.getId(), "User logged in");

        return toAuthResponse(user, token);
    }

    private AuthResponse toAuthResponse(AuthUser user, String token) {
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
