package com.kyk.FoodWorld.exception;

public class DuplicatedMemberLoginIdException extends RuntimeException {

    public DuplicatedMemberLoginIdException(String message) {
        super(message);
    }
}