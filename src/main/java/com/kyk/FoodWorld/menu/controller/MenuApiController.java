package com.kyk.FoodWorld.menu.controller;


import com.kyk.FoodWorld.menu.domain.entity.MenuRecommend;
import com.kyk.FoodWorld.menu.service.MenuRecommendServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/menu")
public class MenuApiController {
    private final MenuRecommendServiceImpl menuRecommendService;

    @GetMapping("/api/randomMenu")
    public ResponseEntity<?> randomMenuPick(String selectedCategory) {
        log.info("선택된 카테고리 = {}", selectedCategory);
        MenuRecommend randomPick = menuRecommendService.randomPick(selectedCategory);

        return new ResponseEntity<>(randomPick, HttpStatus.OK);
    }
}
