package com.kyk.FoodWorld.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * 스프링 환경 불필요
 *
 * ProfileController나 Environment 모두 자바 클래스(인터페이스)이기 때문에 쉽게 테스트 할 수 있다.
 * Environment는 인터페이스라서 가짜 구현체인 MockEnvironment(스프링에서 제공)를 사용해서 테스트하면 된다.
 */
@SpringBootTest
public class ProfileControllerUnitTest {

    @Test
    public void real_profile이_조회된다() {
        //given
        String expectedProfile = "real";
        MockEnvironment env = new MockEnvironment();
        env.addActiveProfile(expectedProfile);
        env.addActiveProfile("real-db");

        ProfileController controller = new ProfileController(env);

        //when
        String profile = controller.profile();

        //then
        assertThat(profile).isEqualTo(expectedProfile);
    }

    @Test
    public void active_profile이_없으면_default가_조회된다() {
        //given
        String expectedProfile = "default";
        MockEnvironment env = new MockEnvironment();

        ProfileController controller = new ProfileController(env);

        //when
        String profile = controller.profile();

        //then
        assertThat(profile).isEqualTo(expectedProfile);
    }
}