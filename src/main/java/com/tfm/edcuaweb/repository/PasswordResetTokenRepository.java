package com.tfm.edcuaweb.repository;

import java.util.Optional;

import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tfm.edcuaweb.model.PasswordResetToken;
import org.springframework.stereotype.Repository;

@Repository

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long> {
	Optional<PasswordResetToken> findByToken(String token);

}
