package com.medical.user.service;

import com.medical.user.client.AuditClient;
import com.medical.user.client.AuthClient;
import com.medical.user.dto.CreateUserRequest;
import com.medical.user.dto.UpdateUserRequest;
import com.medical.user.dto.UserResponse;
import com.medical.user.entity.AppUser;
import com.medical.user.enums.Role;
import com.medical.user.enums.UserStatus;
import com.medical.user.exception.BadRequestException;
import com.medical.user.exception.ResourceNotFoundException;
import com.medical.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthClient authClient;
    private final AuditClient auditClient;

    public UserResponse createUser(CreateUserRequest request) {
        if (request.getRole() == Role.ADMIN) {
            throw new BadRequestException("Admin account is managed by the system and cannot be created");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists: " + request.getEmail());
        }

        AppUser user = AppUser.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .department(request.getDepartment())
                .role(request.getRole())
                .status(UserStatus.ACTIVE)
                .build();

        AppUser savedUser = userRepository.save(user);
        auditClient.log(savedUser.getEmail(), "REGISTER", savedUser.getId(), "User registered with role " + savedUser.getRole());
        return toResponse(savedUser);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        return toResponse(findUserEntityById(id));
    }

    public UserResponse getUserByEmail(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return toResponse(user);
    }

    public List<UserResponse> getUsersByRole(Role role) {
        return userRepository.findByRole(role)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<UserResponse> getUsersByStatus(UserStatus status) {
        return userRepository.findByStatus(status)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        AppUser user = findUserEntityById(id);

        userRepository.findByEmail(request.getEmail()).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(id)) {
                throw new BadRequestException("Email already exists: " + request.getEmail());
            }
        });

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setDepartment(request.getDepartment());
        user.setRole(request.getRole());

        return toResponse(userRepository.save(user));
    }

    public UserResponse deactivateUser(Long id) {
        AppUser user = findUserEntityById(id);
        user.setStatus(UserStatus.INACTIVE);

        return toResponse(userRepository.save(user));
    }

    public UserResponse activateUser(Long id) {
        AppUser user = findUserEntityById(id);
        user.setStatus(UserStatus.ACTIVE);

        return toResponse(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        AppUser user = findUserEntityById(id);
        if (user.getRole() == Role.ADMIN) {
            throw new BadRequestException("The system admin account cannot be deleted");
        }

        authClient.deleteAuthUser(user.getEmail());
        userRepository.delete(user);
        auditClient.log("admin", "DELETE", id, "User deleted: " + user.getEmail());
    }

    private AppUser findUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private UserResponse toResponse(AppUser user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .department(user.getDepartment())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }
}
