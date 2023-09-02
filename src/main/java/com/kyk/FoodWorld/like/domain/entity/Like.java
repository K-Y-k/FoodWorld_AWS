package com.kyk.FoodWorld.like.domain.entity;

import com.kyk.FoodWorld.board.domain.entity.Board;
import com.kyk.FoodWorld.member.domain.entity.Member;
import com.kyk.FoodWorld.web.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@Getter
@Entity
@Table(name = "Like_Count")
public class Like extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

}
