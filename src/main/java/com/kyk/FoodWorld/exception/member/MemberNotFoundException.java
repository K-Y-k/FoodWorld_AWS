package com.kyk.FoodWorld.exception.member;

import com.kyk.FoodWorld.exception.member.MemberException;

public class MemberNotFoundException extends MemberException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}
