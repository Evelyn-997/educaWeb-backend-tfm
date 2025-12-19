package com.tfm.edcuaweb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileResponse {
    private Long id;
    private String name;
    private String lastName;
    private String username;
    private String email;
    private String role;
    private String password;
}
