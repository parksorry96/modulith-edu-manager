package com.edumanager.user.internal.web;

import com.edumanager.shared.dto.response.ApiResponse;
import com.edumanager.user.dto.ChangePasswordRequest;
import com.edumanager.user.dto.CreateUserDto;
import com.edumanager.user.dto.UpdateUserDto;
import com.edumanager.user.dto.UserDto;
import com.edumanager.user.internal.domain.UserStatus;
import com.edumanager.user.internal.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserDto> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails){
        UserDto user =userService.findByEmail(userDetails.getUsername());
        return ApiResponse.success(user);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id==authentication.principal.id")
    public ApiResponse<UserDto> getUser(@PathVariable Long id){
        UserDto user=userService.findById(id);
        return ApiResponse.success(user);
    }



    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserDto> createUser(@Valid @RequestBody CreateUserDto request) {
        UserDto user = userService.createUser(request);
        return ApiResponse.success(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ApiResponse<UserDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserDto request) {
        UserDto user = userService.updateUser(id, request);
        return ApiResponse.success(user);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> changeUserStatus(
            @PathVariable Long id,
            @RequestParam UserStatus status) {
        userService.changeUserStatus(id, status);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success();
    }

    @PatchMapping("/me/password")
    public ApiResponse<Void> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userDetails.getUsername(), request);
        return ApiResponse.success();
    }
}
