package com.edumanager.user.internal.service;

import com.edumanager.shared.exception.BusinessException;
import com.edumanager.shared.exception.ErrorCode;
import com.edumanager.shared.security.JwtService;
import com.edumanager.shared.security.constants.SecurityConstants;
import com.edumanager.user.dto.*;
import com.edumanager.user.internal.domain.User;
import com.edumanager.user.internal.domain.UserPrincipal;
import com.edumanager.user.internal.domain.UserStatus;
import com.edumanager.user.internal.mapper.UserMapper;
import com.edumanager.user.internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginResponse login(LoginRequest request){
        User user=userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new BusinessException(ErrorCode.INVALID_CREDENTIALS));
        if(!user.canLogin()){
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
        }
        if(!passwordEncoder.matches(request.getPassword(),user.getPassword())){

            user.increaseLoginFailCount();
            userRepository.save(user);

            if(user.getLoginFailCount()>=5){
                throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
            }

        }
        userRepository.updateLastLoginSuccess(user.getId(), LocalDateTime.now());

        UserPrincipal userPrincipal= UserPrincipal.create(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal,null,userPrincipal.getAuthorities());
        String accessToken=jwtService.generateAccessToken(authentication);
        String refreshToken=jwtService.generateRefreshToken(authentication);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(SecurityConstants.Jwt.TOKEN_PREFIX.trim())
                .expiresIn(jwtService.getAccessTokenExpiration())
                .user(userMapper.toDto(user))
                .build();
    }

    @Transactional
    public UserDto signup(SignupRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        User user=userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(true);        //일단 true 나중에 이메일 인증 로직 추가
        user.addRole(request.getRole());

        User saved=userRepository.save(user);
        log.info("회원가입 userId:{}, email:{}", saved.getId(), saved.getEmail());

        return userMapper.toDto(saved);
    }

    public TokenRefreshResponse refreshToken(TokenRefreshRequest request){
        String newAccessToken=jwtService.refreshAccessToken(request.getRefreshToken());

        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .tokenType(SecurityConstants.Jwt.TOKEN_PREFIX.trim())
                .expiresIn(jwtService.getAccessTokenExpiration())
                .build();
    }
}
