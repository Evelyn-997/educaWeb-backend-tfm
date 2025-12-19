package com.tfm.edcuaweb.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;


import com.tfm.edcuaweb.model.RefreshToken;
import com.tfm.edcuaweb.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tfm.edcuaweb.dto.AuthRequest;
import com.tfm.edcuaweb.dto.AuthResponse;
import com.tfm.edcuaweb.dto.ChangePasswordRequest;
import com.tfm.edcuaweb.dto.ForgotPasswordRequest;
import com.tfm.edcuaweb.dto.RefreshTokenRequest;
import com.tfm.edcuaweb.dto.RegisterStudentRequest;
import com.tfm.edcuaweb.dto.RegisterTeacherRequest;
import com.tfm.edcuaweb.dto.ResetPasswordRequest;
import com.tfm.edcuaweb.model.PasswordResetToken;
import com.tfm.edcuaweb.model.Role;
import com.tfm.edcuaweb.model.User;
import com.tfm.edcuaweb.repository.PasswordResetTokenRepository;
import com.tfm.edcuaweb.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepo;
	private final PasswordResetTokenRepository prtRepo;
    @Autowired
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authManager;
	private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepo;
	private final RefreshTokenService refreshTokenService;

	/*=================== Registro TEACHER =================*/
	public AuthResponse registerTeacher(RegisterTeacherRequest req) {
		if(userRepo.findByUsername(req.getUsername()).isPresent()){
			throw new IllegalArgumentException("El USERNAME ya esta en uso.");
		}
		
		if(userRepo.findByEmail(req.getEmail()).isPresent()) {
			throw new IllegalArgumentException("El EMAIL ya esta en uso.");
		}
        //Leemos el ROL desde el request - lo validamos
        Role role= Role.valueOf(req.getRole() != null? req.getRole().toUpperCase(): "TEACHER");

		User teacher = new User();
		teacher.setUsername(req.getUsername());
		teacher.setEmail(req.getEmail());
		teacher.setPassword(passwordEncoder.encode(req.getPassword()));
		teacher.setName(req.getName());
		teacher.setLastName(req.getLastName());
        teacher.setRole(role);

		userRepo.save(teacher);
		
		String access = jwtService.generateAccessToken(teacher);
		String refresh = jwtService.generateRefreshToken(teacher);

		return AuthResponse.builder()
				.accessToken(access)
				.refreshToken(refresh)
				.build();
	}
	// Registro ESTUDIANTE
	public AuthResponse registerStudent(RegisterStudentRequest request) {
		if (userRepo.existsByEmail(request.getEmail())) {
			throw new RuntimeException("El email ya está registrado.");
		}
		if (userRepo.existsByUsername(request.getUsername())) {
			throw new RuntimeException("El nombre de usuario ya está en uso.");
		}

        //Leemos el ROL desde el request lo validamos
        Role role= Role.valueOf(request.getRole() != null? request.getRole().toUpperCase(): "STUDENT");
		
		User student = new User();
		student.setUsername(request.getUsername());
		student.setEmail(request.getEmail());
		student.setPassword(passwordEncoder.encode(request.getPassword()));
        student.setName(request.getName());
        student.setLastName(request.getLastName());
		student.setRole(role);
        student.setEnrollmentNumber(generateEnrollmentNumber());
		
		userRepo.save(student);
		String access = jwtService.generateAccessToken(student );
        String refresh = jwtService.generateRefreshToken(student);

        return AuthResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .role(student.getRole())
                .username(student.getUsername())
                .enrollmentNumber(student.getEnrollmentNumber())
                .build();

	}

    private String generateEnrollmentNumber() {
        long count = userRepo.countByRole(Role.STUDENT) + 1;
        String year = String.valueOf(LocalDate.now().getYear());
        return String.format("STU-%s-%04d", year, count);
    }

    /*========= LOGIN =============*/
	 @Transactional
	public AuthResponse login(AuthRequest req) {
		Authentication auth = authManager.authenticate(
				new UsernamePasswordAuthenticationToken(req.getUsername(),req.getPassword()));

		User user =  userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		String access = jwtService.generateAccessToken(user);
        String refresh = jwtService.generateRefreshToken(user);

         refreshTokenService.create(user, refresh);

         return AuthResponse.builder()
                 .accessToken(access)
                 .refreshToken(refresh)
                 .role(user.getRole())
                 .username(user.getUsername())
                 .name(user.getName()+" "+user.getLastName())
                 .userId(user.getId())
                 .tokenType("Bearer")
                 .role(user.getRole())
                 .enrollmentNumber(user.getEnrollmentNumber())
                 .build();
	}

	 @Transactional
	public AuthResponse refreshToken(RefreshTokenRequest request) {
         RefreshToken refreshToken = refreshTokenRepo.findByRefToken(request.getRefreshToken())
                 .orElseThrow(() -> new RuntimeException("Refresh token inválido"));
         if (refreshToken.isRevoked()) {
             throw new RuntimeException("Refresh token revocado");
         }
		
		User user = refreshToken.getUser();

		String newAccessToken = jwtService.generateAccessToken(user);
		
		return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken.getRefToken())
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
	}
    @Transactional
    public AuthResponse refresh(String refreshToken) {
        User user = jwtService.validateAndGetUserFromRefresh(refreshToken);
        String access = jwtService.generateAccessToken(user);
        return AuthResponse.builder()
                .accessToken(access)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    /* ===== Cambio de contraseña (autenticado) ===== */
	 @Transactional
	 public void changePassword(User current, ChangePasswordRequest req) {
		 if(!passwordEncoder.matches(req.getCurrentPassword(), current.getPassword())) {
			 throw new IllegalArgumentException("La contraseña actual no es correcta.");
		 }
		 current.setPassword(passwordEncoder.encode(req.getNewPassword()));
		 userRepo.save(current);
	 }
	 	 /*============ Flujo de recuperacion(contraseña olvidada) ============*/
	 @Transactional
	 public void forgotPassword(ForgotPasswordRequest req) {
		 Optional<User> userOpt = userRepo.findByEmail(req.getEmail());
		 if(userOpt.isEmpty()) {
			 //Para no filtrar emails validos/invalidos responde 200
			 return;
		 }

         User user =userOpt.get();
		 PasswordResetToken token = PasswordResetToken.builder()
                 .token(UUID.randomUUID().toString())
                 .user(user)
                 .expiresAt(Instant.now().plus(30, ChronoUnit.MINUTES))
                 .used(false)
                 .build();
		 prtRepo.save(token);
         //Envio del Email para cambiar la contraseña


     }
	 /* RESET de la contraseña*/
	 @Transactional
	  public void resetPassword(ResetPasswordRequest req) {
		 PasswordResetToken token = prtRepo.findByToken(req.getToken())
				 .orElseThrow(()-> new IllegalArgumentException("Token inválido."));
		 
		 if(token.isExpired() || token.isUsed()) {
			 throw new IllegalArgumentException("Token expirado o ya utilizado.");
		 }
		 
		 User user = token.getUser();
		 user.setPassword(passwordEncoder.encode(req.getNewPassword()));
		 userRepo.save(user);
		 
		 token.setUsed(true);
		 prtRepo.save(token);
	 }
}


