package com.tfm.edcuaweb.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor @Getter @Setter
public class AuthRequest {
	private String usernameOrEmail;
	private String password;

	public String getUsername() {
		return usernameOrEmail;
	}

	public void setUsername(String username) {
		this.usernameOrEmail = username;
	}


}
