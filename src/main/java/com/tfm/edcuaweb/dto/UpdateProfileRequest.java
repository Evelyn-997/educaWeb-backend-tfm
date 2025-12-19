package com.tfm.edcuaweb.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateProfileRequest {
	private String email;
	private String password;
    private String name;
    private String lastName;
    private String username;

}
