//package com.tfm.edcuaweb.service;
package com.tfm.edcuaweb.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.tfm.edcuaweb.dto.UserProfileResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tfm.edcuaweb.dto.UpdateProfileRequest;
import com.tfm.edcuaweb.model.User;
import com.tfm.edcuaweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService  implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtUtil;

//    public String updateProfile(String username, UpdateProfileRequest update) {
//        User user = userRepository.findByUsername(username).
//                orElseThrow(() -> new RuntimeException("El nombre de usuario ya está en uso."));
//
//        if (update.getEmail() != null) {
//            user.setEmail(update.getEmail());
//        }
//        if (update.getNewPassword() != null) {
//            user.setPassword(passwordEncoder.encode(update.getNewPassword()));
//        }
//        userRepository.save(user);
//        return "Perfil Actualizado.";
//    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UserProfileResponse res = new UserProfileResponse();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setLastName(user.getLastName());
        res.setUsername(user.getUsername());
        res.setEmail(user.getEmail());
        res.setRole(user.getRole().name());
        return res;
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest req) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (req.getName() != null) user.setName(req.getName());
        if (req.getLastName() != null) user.setLastName(req.getLastName());
        if (req.getUsername() != null) user.setUsername(req.getUsername());
        if (req.getEmail() != null) user.setEmail(req.getEmail());

        userRepository.save(user);

        return getProfile(userId);
    }
}
