package com.edumanager.user.internal.mapper;

import com.edumanager.user.dto.CreateUserDto;
import com.edumanager.user.dto.SignupRequest;
import com.edumanager.user.dto.UpdateUserDto;
import com.edumanager.user.dto.UserDto;
import com.edumanager.user.internal.domain.User;
import com.edumanager.user.internal.domain.UserStatus;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports={UserStatus.class}
)
public interface UserMapper {
    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)  // Service에서 encode
    @Mapping(target = "roles", ignore = true)     // Service에서 처리
    @Mapping(target = "status", expression = "java(dto.isActive() ? UserStatus.ACTIVE : UserStatus.PENDING)")
    @Mapping(target = "emailVerified", source = "emailVerified")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "lockedAt", ignore = true)
    @Mapping(target = "loginFailCount", ignore = true)
    User toEntity(CreateUserDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toEntity(SignupRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)      // 이메일은 변경 불가
    @Mapping(target = "password", ignore = true)    // Service에서 별도 처리
    @Mapping(target = "roles", ignore = true)       // 권한은 별도 API로 변경
    @Mapping(target = "status", ignore = true)      // 상태는 별도 API로 변경
    void updateUserFromDto(UpdateUserDto dto, @MappingTarget User user);

}
