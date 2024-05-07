package com.kyk.FoodWorld.member.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    CUSTOMER("ROLE_CUSTOMER", "고객"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String title;
}
