package com.finance.backend.controller;

import com.finance.backend.dto.user.CreateUserRequest;
import com.finance.backend.dto.user.UpdateUserRequest;
import com.finance.backend.dto.user.UserResponse;
import com.finance.backend.model.Role;
import com.finance.backend.security.RequireRoles;
import com.finance.backend.security.RequestUserContext;
import com.finance.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequireRoles(Role.ADMIN)
    @GetMapping
    public List<UserResponse> listUsers() {
        return userService.getAllUsers();
    }

    @RequireRoles(Role.ADMIN)
    @GetMapping("/{userId}")
    public UserResponse getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @RequireRoles({Role.VIEWER, Role.ANALYST, Role.ADMIN})
    @GetMapping("/me")
    public UserResponse me(HttpServletRequest request) {
        return userService.toResponse(RequestUserContext.get(request));
    }

    @RequireRoles(Role.ADMIN)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @RequireRoles(Role.ADMIN)
    @PutMapping("/{userId}")
    public UserResponse updateUser(@PathVariable Long userId, @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateUser(userId, request);
    }

    @RequireRoles(Role.ADMIN)
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
