package com.Online.Recipe.Management.System.RMS.controllers;

import com.Online.Recipe.Management.System.RMS.dtos.RegistrationRequest;
import com.Online.Recipe.Management.System.RMS.dtos.UserDTO;
import com.Online.Recipe.Management.System.RMS.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.Online.Recipe.Management.System.RMS.dtos.ForgotPasswordRequest;
import com.Online.Recipe.Management.System.RMS.dtos.JwtAuthenticationResponse;
import com.Online.Recipe.Management.System.RMS.dtos.LoginRequest;
import com.Online.Recipe.Management.System.RMS.dtos.ResetPasswordRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest) {
        UserDTO userDTO = userService.registerUser(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtAuthenticationResponse jwtResponse = userService.loginUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        userService.requestPasswordReset(forgotPasswordRequest.getEmail());
        return ResponseEntity.ok("Password reset instructions have been sent to your email if it exists in our system.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        try {
            userService.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword());
            return ResponseEntity.ok("Password has been successfully reset.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
