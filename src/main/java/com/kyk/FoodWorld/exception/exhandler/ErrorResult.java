package com.kyk.FoodWorld.exception.exhandler;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 오류가 발생했을 때의 결과 객체
 */
@Data
@AllArgsConstructor
public class ErrorResult {
    private String code;    // 오류 내용
    private String message; // 메시지
}
