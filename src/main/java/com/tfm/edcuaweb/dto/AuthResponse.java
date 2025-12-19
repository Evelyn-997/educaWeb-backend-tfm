package com.tfm.edcuaweb.dto;

import com.tfm.edcuaweb.model.Role;

import lombok.*;

import javax.xml.transform.sax.SAXResult;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class AuthResponse {
	private String accessToken;
	private String refreshToken;
	private Role role;
	private Long userId;
	private String username;
    private String name;
	private String tokenType = "Bearer";
    private String enrollmentNumber;

}
