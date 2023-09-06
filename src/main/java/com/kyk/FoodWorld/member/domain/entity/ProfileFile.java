package com.kyk.FoodWorld.member.domain.entity;

import com.kyk.FoodWorld.web.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProfileFile extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profileFile_id")
    private Long id;

    private String originalFileName;

    private String storedFileName;

    private String path;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    public ProfileFile(String originalFileName, String storedFileName, String path, Member member) {
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.path = path;
        this.member = member;
    }
}
