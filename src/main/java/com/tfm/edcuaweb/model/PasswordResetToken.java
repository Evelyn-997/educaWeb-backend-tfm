package com.tfm.edcuaweb.model;

import java.time.Instant;

import jakarta.persistence.*;
import lombok.*;

@Entity  @NoArgsConstructor @AllArgsConstructor @Builder
@Data @Setter @Getter
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true, length = 120)
	private String token;
	
	@JoinColumn(name = "user_id")
	private User user;
	
	@Column(nullable = false)
	private Instant expiresAt;

	@Column(nullable = false)
	private boolean used = false;

	public boolean isExpired() {
		return Instant.now().isAfter(expiresAt);
	}


}

