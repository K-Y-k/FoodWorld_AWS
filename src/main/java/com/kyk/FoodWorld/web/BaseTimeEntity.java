package com.kyk.FoodWorld.web;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass                              // JPA 엔티티 클래스들이 BaseTimeEntity에 상속할 경우 아래 필드들도 칼럼으로 인식하게 한다.
@EntityListeners(AuditingEntityListener.class) // BaseTimeEntity 클래스에 Auditing 기능을 포함시킨다.
public abstract class BaseTimeEntity {

    @CreatedDate        // 엔티티가 생성되어 저장될 때 시간이 자동 저장된다.
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdDate;

    @LastModifiedDate  // 조회한 엔티티의 값을 변경할 때 시간이 자동 저장된다.
    @DateTimeFormat(pattern = "yyyy-MM-dd/HH:mm:ss")
    private LocalDateTime lastModifiedDate;

}
