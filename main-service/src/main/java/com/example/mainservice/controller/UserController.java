package com.example.mainservice.controller;

import com.example.mainservice.model.User;
import com.example.mainservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "User management operations (admin only)")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create a new user", description = "Create a new user with specified role (admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role")
    })
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            User user = userService.createUser(request.getUsername(), request.getPassword(), request.getRole());
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "role", user.getRole()
                ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Get all users", description = "List all users in the system (admin only)")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Users retrieved successfully",
            content = @Content(mediaType = "application/json", 
            array = @ArraySchema(schema = @Schema(implementation = User.class)))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role")
    })
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @Operation(
        summary = "Get user by username", 
        description = "Get user details by username (accessible by admin or the user themselves)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role or be the user"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #username == authentication.name")
    public ResponseEntity<?> getUserByUsername(
            @Parameter(description = "Username to retrieve", required = true)
            @PathVariable String username) {
        Optional<User> user = userService.findByUsername(username);
        return user
            .map(u -> {
                Map<String, Object> userDetails = new HashMap<>();
                userDetails.put("id", u.getId());
                userDetails.put("username", u.getUsername());
                userDetails.put("role", u.getRole());
                userDetails.put("enabled", u.isEnabled());
                userDetails.put("createdAt", u.getCreatedAt());
                return ResponseEntity.ok(userDetails);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Change user password", 
        description = "Change a user's password (accessible by admin or the user themselves)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role or be the user"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{username}/password")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #username == authentication.name")
    public ResponseEntity<?> changePassword(
            @Parameter(description = "Username to update", required = true)
            @PathVariable String username,
            @Valid @RequestBody ChangePasswordRequest request) {
        boolean success = userService.changePassword(username, request.getNewPassword());
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(
        summary = "Update user status", 
        description = "Enable or disable a user account (admin only, cannot disable self)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User status updated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role and cannot be self"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{username}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN') and #username != authentication.name")
    public ResponseEntity<?> updateUserStatus(
            @Parameter(description = "Username to update", required = true)
            @PathVariable String username,
            @Valid @RequestBody UpdateStatusRequest request) {
        boolean success = userService.updateUserStatus(username, request.isEnabled());
        if (success) {
            return ResponseEntity.ok(Map.of(
                "message", "User status updated successfully",
                "username", username,
                "enabled", request.isEnabled()
            ));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(
        summary = "Update user role", 
        description = "Change a user's role (admin only, cannot change self)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User role updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid role"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role and cannot be self"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{username}/role")
    @PreAuthorize("hasRole('ROLE_ADMIN') and #username != authentication.name")
    public ResponseEntity<?> updateUserRole(
            @Parameter(description = "Username to update", required = true)
            @PathVariable String username,
            @Valid @RequestBody UpdateRoleRequest request) {
        try {
            boolean success = userService.updateUserRole(username, request.getRole());
            if (success) {
                return ResponseEntity.ok(Map.of(
                    "message", "User role updated successfully",
                    "username", username,
                    "role", request.getRole()
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(
        summary = "Delete user", 
        description = "Permanently delete a user account (admin only, cannot delete self)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role and cannot be self"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN') and #username != authentication.name")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "Username to delete", required = true)
            @PathVariable String username) {
        boolean success = userService.deleteUser(username);
        if (success) {
            return ResponseEntity.ok(Map.of(
                "message", "User deleted successfully",
                "username", username
            ));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Inner request classes with OpenAPI annotations
    
    @Schema(description = "Request to create a new user")
    public static class CreateUserRequest {
        @Schema(description = "Username for the new user", example = "newuser", required = true)
        @NotBlank(message = "Username is required")
        private String username;
        
        @Schema(description = "Password for the new user", example = "password123", required = true, format = "password")
        @NotBlank(message = "Password is required")
        private String password;
        
        @Schema(description = "Role for the new user (ROLE_USER or ROLE_ADMIN)", example = "ROLE_USER", required = true)
        @NotBlank(message = "Role is required")
        private String role;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    @Schema(description = "Request to change a user's password")
    public static class ChangePasswordRequest {
        @Schema(description = "New password", example = "newSecurePassword", required = true, format = "password")
        @NotBlank(message = "New password is required")
        private String newPassword;

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
    
    @Schema(description = "Request to update a user's enabled status")
    public static class UpdateStatusRequest {
        @Schema(description = "New enabled status", example = "true")
        private boolean enabled;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
    
    @Schema(description = "Request to update a user's role")
    public static class UpdateRoleRequest {
        @Schema(description = "New role (ROLE_USER or ROLE_ADMIN)", example = "ROLE_ADMIN", required = true)
        @NotBlank(message = "Role is required")
        private String role;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}