package com.medical.auth.dto;

import com.medical.auth.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    private String fullName;
    private String email;
    private String department;
    private Role role;
}
