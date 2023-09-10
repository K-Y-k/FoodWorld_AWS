package com.kyk.FoodWorld.member.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kyk.FoodWorld.board.domain.entity.Board;
import com.kyk.FoodWorld.chat.domain.entity.ChatRoom;
import com.kyk.FoodWorld.comment.domain.entity.Comment;
import com.kyk.FoodWorld.follow.domain.entity.Follow;
import com.kyk.FoodWorld.like.domain.entity.Like;
import com.kyk.FoodWorld.menu.domain.entity.MenuRecommend;
import com.kyk.FoodWorld.web.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 회원 엔티티
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(length = 7)
    private String name;

    @Column(length = 10)
    private String loginId;

    @Column(length = 10)
    private String password;

    private String introduce;

    @JsonIgnoreProperties({"member"})
    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private ProfileFile profileFile;

    private int followCount;

    private int followingCount;

    private String role;

    @Builder.Default
    @JsonIgnoreProperties({"member"})
    @OneToMany(mappedBy = "fromMember", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Follow> fromMembers = new ArrayList<>();

    @Builder.Default
    @JsonIgnoreProperties({"member"})
    @OneToMany(mappedBy = "toMember", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Follow> toMembers = new ArrayList<>();
//
    @Builder.Default
    @JsonIgnoreProperties({"member"})
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Board> boards = new ArrayList<>();

    @Builder.Default
    @JsonIgnoreProperties({"member"})
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @Builder.Default
    @JsonIgnoreProperties({"member"})
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @JsonIgnoreProperties({"member"})
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<MenuRecommend> menuRecommends = new ArrayList<>();


    @Builder.Default
    @JsonIgnoreProperties({"member1"})
    @OneToMany(mappedBy = "member1", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ChatRoom> member1ChatRooms = new ArrayList<>();

    @Builder.Default
    @JsonIgnoreProperties({"member2"})
    @OneToMany(mappedBy = "member2", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ChatRoom> member2ChatRooms = new ArrayList<>();


    @Builder
    public Member(String name, String loginId, String password, String role) {
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.role = role;
    }

    
    // 비즈니스 로직
    // 변경 감지로 프로필 업데이트
    public void changeProfile(String name, String loginId, String password, String introduce){
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.introduce = introduce;
    }

}



