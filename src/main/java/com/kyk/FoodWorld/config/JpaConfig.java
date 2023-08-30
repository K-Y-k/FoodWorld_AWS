package com.kyk.FoodWorld.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 메인 어플리케이션에 @EnableJpaAuditing를 넣으면 
 * @EnableJpaAuditing이 있으면 무조건 하나의 엔티티 클래스가 있어야하는데
 * 
 * 테스트 환경에서도 메인 어플리케이션에 넣은 @EnableJpaAuditing이 적용되는데
 * 테스트 환경에서는 하나의 엔티티도 못가져올 수 있으므로 따로 분리한 것
 */
@Configuration
@EnableJpaAuditing // JPA Auditing 활성화
public class JpaConfig {}
