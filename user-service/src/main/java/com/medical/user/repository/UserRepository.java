package com.medical.user.repository;

import com.medical.user.entity.AppUser;
import com.medical.user.enums.Role;
import com.medical.user.enums.UserStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByEmail(String email);

    boolean existsByEmail(String email);

    List<AppUser> findByRole(Role role);

    List<AppUser> findByStatus(UserStatus status);
}
