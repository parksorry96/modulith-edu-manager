package com.edumanager.user.internal.web;

import com.edumanager.shared.dto.response.ApiResponse;
import com.edumanager.user.dto.*;
import com.edumanager.user.internal.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        LoginResponse response=authenticationService.login(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/signup")
    public ApiResponse<UserDto> signup(@Valid @RequestBody SignupRequest request){
        UserDto user=authenticationService.signup(request);
        return ApiResponse.success(user);
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenRefreshResponse>  refresh(@Valid @RequestBody TokenRefreshRequest request){
        TokenRefreshResponse response=authenticationService.refreshToken(request);
        return ApiResponse.success(response);
    }

    //TODO : JWT 블랙리스트 REDIS 구현
//    @PostMapping("/logout")
}
