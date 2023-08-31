package com.kyk.FoodWorld.exception.exhandler.advice;


import com.kyk.FoodWorld.exception.member.MemberNotFoundException;
import com.kyk.FoodWorld.web.HomeController;
import lombok.extern.slf4j.Slf4j;
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
@ControllerAdvice(assignableTypes = {HomeController.class})
public class ExControllerAdvice {
    /**
     * MemberNotFoundException일 때 여기로 호출
     */
    @ExceptionHandler
    public String MemberNotFoundExceptionHandler(MemberNotFoundException e, Model model) {
        log.error("[MemberNotFoundException] ex", e);

        model.addAttribute("message", e.getMessage());
        model.addAttribute("redirectUrl", "/");
        return "messages";
    }
}
