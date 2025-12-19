package com.tfm.edcuaweb.controller;

import com.tfm.edcuaweb.dto.ChangePasswordRequest;
import com.tfm.edcuaweb.dto.UpdateProfileRequest;
import com.tfm.edcuaweb.dto.UserProfileResponse;
import com.tfm.edcuaweb.model.User;
import com.tfm.edcuaweb.service.AuthService;
import com.tfm.edcuaweb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/user")
@RequiredArgsConstructor
public class

UserController {
    private final UserService userService;
    private final AuthService authService;

    @GetMapping("/me")
    public UserProfileResponse getMyProfile(@AuthenticationPrincipal User user) {
        return userService.getProfile(user.getId());
    }

    @PutMapping("/me")
    public UserProfileResponse updateMyProfile(
            @AuthenticationPrincipal User user,
            @RequestBody UpdateProfileRequest request
    ) {
        return userService.updateProfile(user.getId(), request);
    }
    /*
    @PostMapping("/me/passwordChange")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal User user, @RequestBody ChangePasswordRequest req){
        authService.changePassword(user, req);
        return ResponseEntity.ok().build();
    }
     */

}
