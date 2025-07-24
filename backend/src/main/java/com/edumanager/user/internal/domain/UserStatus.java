package com.edumanager.user.internal.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {

    PENDING("대기중"),
    ACTIVE("활성"),
    INACTIVE("비활성"),
    LOCKED("잠김"),
    DELETED("삭제됨");

    private final String description;
}
