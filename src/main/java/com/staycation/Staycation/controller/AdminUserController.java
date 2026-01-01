package com.staycation.Staycation.controller;

import com.staycation.Staycation.entity.User;
import com.staycation.Staycation.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @PatchMapping("/{userId}/role")
    public ResponseEntity<User> changeUserRole(
            @PathVariable Long userId,
            @RequestBody ChangeRoleRequest request
    ) {
        User updated = userService.changeUserRole(userId, request.getRole());
        return ResponseEntity.ok(updated);
    }

    @Data
    public static class ChangeRoleRequest {
        private String role;   // e.g. "MANAGER"
    }
}
