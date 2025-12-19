package com.tfm.edcuaweb.controller;

import com.tfm.edcuaweb.service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.tfm.edcuaweb.dto.*;
import com.tfm.edcuaweb.model.User;
import com.tfm.edcuaweb.service.AuthService;
import com.tfm.edcuaweb.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin (origins = "http://localhost:4200") //Aniado un endpoint para Auth
public class AuthController {

	private final RefreshTokenService refreshTokenService;
	private final AuthService authService;

	@PostMapping("/register-teacher") /*Regsitro de PROFESORES*/
	public ResponseEntity<AuthResponse> registerTeacher(@RequestBody RegisterTeacherRequest request){
		return ResponseEntity.ok(authService.registerTeacher(request));
	}
	
	@PostMapping("/register-student") /*Regsitro de ESTUDIANTES*/
	public ResponseEntity<AuthResponse> registerStudent(@RequestBody RegisterStudentRequest request){
		return ResponseEntity.ok(authService.registerStudent(request));
	}
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req){
		return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody Map<String, String> body){
        String token = body.get("refreshToken");
        System.out.println("Logout token recibido: "+token);
        refreshTokenService.revokeToken(token);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest req){
		return ResponseEntity.ok(authService.refreshToken(req));
	}

	@PostMapping("/password/change")
	public ResponseEntity<Void> changePassword(@AuthenticationPrincipal User user, @RequestBody ChangePasswordRequest req){
		authService.changePassword(user, req);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/password/forgot")
	public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest req){
		authService.forgotPassword(req);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/password/reset")
	public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest req){
		authService.resetPassword(req);
		return ResponseEntity.noContent().build();
	}

}
