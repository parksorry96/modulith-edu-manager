package com.edumanager.user.internal.service;

import com.edumanager.shared.dto.response.PageResponse;
import com.edumanager.shared.exception.BusinessException;
import com.edumanager.shared.exception.ErrorCode;
import com.edumanager.user.dto.*;
import com.edumanager.user.internal.domain.User;
import com.edumanager.user.internal.domain.UserStatus;
import com.edumanager.user.internal.mapper.UserMapper;
import com.edumanager.user.internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toDto(user);
    }

    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toDto(user);
    }

    public PageResponse<UserDto> searchUsers(UserSearchRequest request, Pageable pageable) {
        Page<User> users;

        if (request.getKeyword() != null) {
            users = userRepository.searchUsers(request.getKeyword(), pageable);
        } else if (request.getRole() != null) {
            users = userRepository.findByRole(request.getRole(), pageable);
        } else if (request.getStatus() != null) {
            users = userRepository.findByStatus(request.getStatus(), pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        return PageResponse.of(users.map(userMapper::toDto));
    }

    @Transactional
    public UserDto createUser(CreateUserDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.addRole(dto.getRole());

        User saved = userRepository.save(user);
        log.info("User created: id={}, email={}", saved.getId(), saved.getEmail());

        return userMapper.toDto(saved);
    }

    @Transactional
    public UserDto updateUser(Long userId, UpdateUserDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호 변경
        if (dto.getNewPassword() != null) {
            if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                throw new BusinessException(ErrorCode.INVALID_PASSWORD);
            }
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        userMapper.updateUserFromDto(dto, user);
        User updated = userRepository.save(user);

        return userMapper.toDto(updated);
    }

    @Transactional
    public void changeUserStatus(Long userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.setStatus(status);
        if (status == UserStatus.ACTIVE) {
            user.unlockAccount();
        }

        userRepository.save(user);
        log.info("User status changed: userId={}, status={}", userId, status);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
        log.info("User deleted: userId={}", userId);
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest request){
        User user= userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())){
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("비밀번호 변경 사용자 {}",email);
    }

}
