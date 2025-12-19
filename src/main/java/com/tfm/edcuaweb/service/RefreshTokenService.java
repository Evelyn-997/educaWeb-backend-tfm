package com.tfm.edcuaweb.service;

import com.tfm.edcuaweb.model.RefreshToken;
import com.tfm.edcuaweb.model.User;
import com.tfm.edcuaweb.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository repo;

    @Transactional
    public RefreshToken create(User user, String token) {

        RefreshToken rt = new RefreshToken();
        rt.setRefToken(token);
        rt.setUser(user);
        rt.setRevoked(false);

        return repo.save(rt);
    }
    @Transactional
    public void revokeToken(String token) {
        System.out.println("Intentando revocar token: " + token);

        repo.findByRefToken(token).ifPresentOrElse(rt -> {
            System.out.println("Token encontrado en BD, revocando...");
            rt.setRevoked(true);
            repo.save(rt);
        }, () -> {
            System.out.println("❌ Token NO encontrado en BD");
        });

    }

    @Transactional
    public void revokeAllByUser(User user) {
        repo.revokeAllByUser(user);
    }


}
