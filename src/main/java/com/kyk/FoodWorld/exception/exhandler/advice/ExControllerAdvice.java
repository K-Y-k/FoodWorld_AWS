package com.kyk.FoodWorld.exception.exhandler.advice;


import com.kyk.FoodWorld.exception.exhandler.ErrorResult;
import com.kyk.FoodWorld.exception.member.MemberNotFoundException;
import com.kyk.FoodWorld.web.HomeController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 여러 컨트롤러에서 발생하는
 * 예외에 대한 처리를 공통으로 해주는 곳 (ControllerAdvice로 분리)
 *
 * @ControllerAdvice 예외 처리 전용
 */
@Slf4j
@ControllerAdvice
public class ExControllerAdvice {
    /**
     * MemberException일 때 여기로 호출
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResult> memberExHandler(MemberNotFoundException e) {
        log.error("[MemberExceptionHandler] ex", e);
        ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }
}
