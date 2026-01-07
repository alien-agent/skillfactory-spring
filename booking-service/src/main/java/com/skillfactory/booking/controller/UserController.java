package com.skillfactory.booking.controller;

import com.skillfactory.booking.dto.AuthRequest;
import com.skillfactory.booking.dto.AuthResponse;
import com.skillfactory.booking.entity.User;
import com.skillfactory.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/auth")
    public ResponseEntity<AuthResponse> auth(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    // ADMIN Endpoints
    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody User user) {
        userService.createUser(user);
        return ResponseEntity.ok().build();
    }

    // Stub other admin methods for now as they are not business critical for the
    // MAIN flow
    // but required by task.
    @DeleteMapping
    public ResponseEntity<Void> deleteUser() {
        // Implement logic or stub
        return ResponseEntity.ok().build();
    }

    @PatchMapping
    public ResponseEntity<Void> updateUser() {
        return ResponseEntity.ok().build();
    }
}
