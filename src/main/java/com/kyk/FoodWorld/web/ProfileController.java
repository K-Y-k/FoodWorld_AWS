package com.kyk.FoodWorld.web;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * 배포 시에 선택해야 할 포트 번호(8081, 8082)를 판단하는 API 추가
 */
@RequiredArgsConstructor
@RestController
public class ProfileController {

    private final Environment env;

    @GetMapping("/profile")
    public String profile() {
        List<String> profiles = Arrays.asList(env.getActiveProfiles());           // 현재 실행 중인 ActiveProfile을 모두 가져온다.(즉, real/real-db 등 활성화 되어있으면 모두 담긴다.)
        List<String> realProfiles = Arrays.asList("real", "real1", "real2");      // real, real1, real2는 모두 배포에 사용될 profile이라 이 중 하나라도 있으면 그 값을 반환한다.
        String defaultProfile = profiles.isEmpty() ? "default" : profiles.get(0);

        return profiles.stream()
                .filter(realProfiles::contains)
                .findAny()
                .orElse(defaultProfile);
    }
}
