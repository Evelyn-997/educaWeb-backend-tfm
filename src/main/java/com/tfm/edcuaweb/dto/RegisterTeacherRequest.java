package com.tfm.edcuaweb.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterTeacherRequest {
	 @NotBlank @Size(min = 3, max = 60)
	  private String username;
	  @NotBlank @Email @Size(max = 150)
	  private String email;
	  @NotBlank
	  private String password;
	  @NotBlank @Size(max = 60)
	  private String name;
	  @NotBlank @Size(max = 80)
     // @Column(name = "last_name")
	  private String lastName;
      private String role;
      private Boolean status = true;
      private LocalDate regisDate = LocalDate.now();

}
