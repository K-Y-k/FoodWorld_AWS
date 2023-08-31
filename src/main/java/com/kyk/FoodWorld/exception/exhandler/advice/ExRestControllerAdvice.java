//package com.kyk.FoodWorld.exception.exhandler.advice;
//
//
//import com.kyk.FoodWorld.exception.member.MemberException;
//import com.kyk.FoodWorld.exception.member.MemberNotFoundException;
//import com.kyk.FoodWorld.exception.exhandler.ErrorResult;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
///**
// * 여러 컨트롤러에서 발생하는
// * 예외에 대한 처리를 공통으로 해주는 곳 (ControllerAdvice로 분리)
// *
// * @RestController에 예외가 터진 경우 예외 처리
// * -> @ControllerAdvice(annotations = RestController.class)
// *
// * 해당 패키지 포함 및 하위 패키지까지 예외 처리
// * (ex) 주문 관리 / 상품 관리 등 패키지가 나뉘면 이렇게 처리함)
// * -> @ControllerAdvice("org.example.controllers")
// *
// * 직접 특정 컨트롤러 인터페이스 및 클래스 지정
// * -> @ControllerAdvice(assignableTypes = {ControllerInterface.class, AbstractController.class})
// *
// */
//@Slf4j
//@RestControllerAdvice
//public class ExRestControllerAdvice {
//    /**
//     * 여기 컨트롤러에서 IllegalArgumentException이 발생하면
//     * ExceptionHandlerExceptionResolver를 호출하여 @ExceptionHandler가 있는지 확인하고
//     * 있으면 이 메서드가 호출되고 @RestController로 인해 JSON으로 자동 변환되어 정상 리턴된다.
//     *
//     * 정상 흐름이기에 Http 상태코드가 200 정상으로 반환된다.
//     * 예외를 터진 것을 잡은 것인라서 정상적으로 호출된 것으로 하기 싫으면 @ResponseStatus로 설정하자
//     * 이 덕분에 지저분하게 서블릿 컨테이너로 다시 올라가지 않는다.
//     */
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ErrorResult illegalExHandler(IllegalArgumentException e) {
//        log.error("[IllegalArgumentExceptionHandler] ex", e);
//        return new ErrorResult("BAD", e.getMessage());
//    }
//
//    /**
//     * MemberException일 때 여기로 호출
//     */
//    @ExceptionHandler
//    public ResponseEntity<ErrorResult> memberExHandler(MemberException e) {
//        log.error("[MemberExceptionHandler] ex", e);
//        ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
//        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
//    }
//
//    /**
//     * 최상위 예외 Exception로
//     * 여기에 등록된 @ExceptionHandler의 예외가 해당 없는 예외는 여기로 호출된다.
//     * 즉, 공통 처리 예외
//     */
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler
//    public ErrorResult exHandler(Exception e) {
//        log.error("[ExceptionHandler] ex", e);
//        return new ErrorResult("EX", "내부 오류");
//    }
//}
