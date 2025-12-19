package com.tfm.edcuaweb.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterStudentRequest {
	@NotBlank @Size(max = 80)
	private String name;
	@NotBlank @Size(max = 150)
	private String lastName;
	@NotBlank @Size(min= 3, max = 50)
	private String username;
	@NotBlank @Email @Size(max = 150)
	private String email;
	@NotBlank 
	private String password;
    private String role;
    private Boolean status = true;
    private LocalDate regisDate = LocalDate.now();
    private String enrollmentNumber;


}
