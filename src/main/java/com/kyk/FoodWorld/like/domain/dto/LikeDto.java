package com.kyk.FoodWorld.like.domain.dto;


import com.kyk.FoodWorld.board.domain.entity.Board;
import com.kyk.FoodWorld.like.domain.entity.Like;
import com.kyk.FoodWorld.member.domain.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LikeDto {

    private Long id;

    private Member member;

    private Board board;


    // 엔티티에 @setter를 사용하지 않기 위해 dto에서 엔티티로 변환해주는 메서드 적용
    public static Like toEntity(Member member, Board board) {
        return Like.builder()
                .member(member)
                .board(board)
                .build();
    }

}
