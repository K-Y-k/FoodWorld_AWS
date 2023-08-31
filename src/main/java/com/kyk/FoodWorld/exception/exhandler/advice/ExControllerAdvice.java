package com.kyk.FoodWorld.exception.exhandler.advice;


import com.kyk.FoodWorld.exception.exhandler.ErrorResult;
import com.kyk.FoodWorld.exception.member.DuplicatedMemberLoginIdException;
import com.kyk.FoodWorld.exception.member.DuplicatedMemberNameException;
import com.kyk.FoodWorld.exception.member.MemberException;
import com.kyk.FoodWorld.exception.member.MemberNotFoundException;
import com.sun.jdi.request.DuplicateRequestException;
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
     * MemberNotFoundException, DuplicatedMemberNameException, DuplicatedMemberLoginIdException일 때 여기로 호출
     */
    @ExceptionHandler({MemberNotFoundException.class, DuplicatedMemberNameException.class, DuplicatedMemberLoginIdException.class})
    public String MemberNotFoundExHandler(MemberException e, Model model) {
        log.error("[" + e.getClass() + "] ex", e);

        model.addAttribute("message", e.getMessage());
        model.addAttribute("redirectUrl", "/");
        return "messages";
    }
}
