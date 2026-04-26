package com.medical.user.dto;

import com.medical.user.enums.Role;
import com.medical.user.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private String department;
    private Role role;
    private UserStatus status;
}
