package com.kyk.FoodWorld.exception.member;

public class DuplicatedMemberLoginIdException extends MemberException {
    public DuplicatedMemberLoginIdException(String message) {
        super(message);
    }
}
