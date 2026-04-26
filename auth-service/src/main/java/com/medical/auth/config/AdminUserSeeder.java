package com.medical.auth.config;

import com.medical.auth.entity.AuthUser;
import com.medical.auth.enums.Role;
import com.medical.auth.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminUserSeeder {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Bean
    CommandLineRunner seedAdminUser() {
        return args -> {
            authUserRepository.findByRole(Role.ADMIN)
                    .stream()
                    .filter(user -> !adminEmail.equalsIgnoreCase(user.getEmail()))
                    .forEach(authUserRepository::delete);

            AuthUser admin = authUserRepository.findByEmail(adminEmail)
                    .orElseGet(() -> AuthUser.builder()
                            .fullName("System Admin")
                            .email(adminEmail)
                            .password(passwordEncoder.encode(adminPassword))
                            .role(Role.ADMIN)
                            .enabled(true)
                            .build());

            admin.setFullName("System Admin");
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);
            authUserRepository.save(admin);
        };
    }
}
