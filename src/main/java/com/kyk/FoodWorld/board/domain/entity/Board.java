package com.kyk.FoodWorld.board.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kyk.FoodWorld.comment.domain.entity.Comment;
import com.kyk.FoodWorld.like.domain.entity.Like;
import com.kyk.FoodWorld.member.domain.entity.Member;
import com.kyk.FoodWorld.web.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Entity
public class Board extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column(length = 60)
    private String title;

    @Column(length = 500)
    private String content;

    @Column(updatable = false)
    private String writer;

    private String boardType;
    private String subType;
    private String area;
    private String menuName;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int count;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int likeCount;

    // 단방향 관계
    // Fetch 전략의 기본값은 EAGER(즉시 로딩)이지만 필요하지 않은 쿼리도 JPA에서 함께 조회하기 때문에 N+1 문제를 야기할 수 있어,
    // Fetch 전략을 LAZY(지연 로딩)로 설정
    @ManyToOne(fetch = FetchType.LAZY) // Board 입장에서는 member와 다대일 관계이므로
    @JsonIgnore                        // 양방향 관계에서 무한 참조 방지
    @JoinColumn(name = "member_id")    // Board 엔티티는 Member 엔티티의 id 필드를 "member_id"라는 이름으로 외래 키를 가짐
    private Member member;

    // 양방향 관계를 맺어줌
    // 게시글이 삭제되면 파일 또한 삭제되어야 하기 때문에 CascadeType.REMOVE와 orphanRemoval = true 속성을 사용
    @Builder.Default
    @JsonIgnoreProperties({"board"}) // API JSON으로 가져오고 싶은 엔티티 필드이면 이렇게 따로 속성 무시하여 가져오게 하여 양방향 관계에서 무한 참조 방지
    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<BoardFile> boardFiles = new ArrayList<>();

    @Builder.Default
    @JsonIgnoreProperties({"board"})
    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OrderBy("id asc") // 정렬
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @JsonIgnore
    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    private int fileAttached;

    private int commentCount;

    public Board(String title, String content, String writer, Member member, String boardType, String subType) {
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.member = member;
        this.boardType = boardType;
        this.subType = subType;
    }
    public Board(String title, String content, String writer, Member member, String boardType, String subType, String area, String menuName, int count, int likeCount) {
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.member = member;
        this.boardType = boardType;
        this.subType = subType;
        this.area = area;
        this.menuName = menuName;
        this.count = count;
        this.likeCount = likeCount;
    }


    // 연관관계 편의 메서드
    public void setMember(Member member) {
        this.member = member;
    }


    // 변경 감지 메서드
    public void updateBoard(String title, String content, String subType){
        this.title = title;
        this.content = content;
        this.subType = subType;
    }
    public void updateRecommendBoard(String title, String content, String subType, String area, String menuName){
        this.title = title;
        this.content = content;
        this.subType = subType;
        if (area != null) {
            this.area = area;
        }
        if (menuName != null) {
            this.menuName = menuName;
        }
    }
    public void updateFileAttached(int fileAttached){
        this.fileAttached = fileAttached;
    }

    public void updateLikeCount(int likeCount){
        this.likeCount = likeCount;
    }

    public void updateCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}