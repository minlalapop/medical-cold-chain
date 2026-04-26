package com.medical.user.config;

import com.medical.user.entity.AppUser;
import com.medical.user.enums.Role;
import com.medical.user.enums.UserStatus;
import com.medical.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AdminUserSeeder {

    private final UserRepository userRepository;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Bean
    CommandLineRunner seedAdminUser() {
        return args -> {
            userRepository.findByRole(Role.ADMIN)
                    .stream()
                    .filter(user -> !adminEmail.equalsIgnoreCase(user.getEmail()))
                    .forEach(userRepository::delete);

            AppUser admin = userRepository.findByEmail(adminEmail)
                    .orElseGet(() -> AppUser.builder()
                            .email(adminEmail)
                            .build());

            admin.setFullName("System Admin");
            admin.setDepartment("Administration");
            admin.setRole(Role.ADMIN);
            admin.setStatus(UserStatus.ACTIVE);
            userRepository.save(admin);
        };
    }
}
