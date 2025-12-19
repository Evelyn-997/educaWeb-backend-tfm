package com.tfm.edcuaweb.repository;

import com.tfm.edcuaweb.model.RefreshToken;
import com.tfm.edcuaweb.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefToken(String token);

    @Modifying
    @Query("""
        UPDATE RefreshToken rt
        SET rt.revoked = true
        WHERE rt.user = :user
          AND rt.revoked = false
    """)
    void revokeAllByUser(@Param("user") User user);
}
