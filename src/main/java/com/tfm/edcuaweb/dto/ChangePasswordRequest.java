package com.tfm.edcuaweb.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChangePasswordRequest {
	//CAMBIO O RECUPERACION DE CONTRASEÑA
	@NotBlank private String currentPassword;
	@NotBlank private String newPassword;

}
