package com.kyk.FoodWorld.board.domain.dto;


import com.kyk.FoodWorld.board.domain.entity.Board;
import com.kyk.FoodWorld.member.domain.entity.Member;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class MuckstarUploadForm extends UploadFormBase {
    private String subType;

    public Board toSaveEntity(Member member) {
        return Board.builder()
                .title("먹스타그램 게시글")
                .content(super.getContent())
                .writer(member.getName())
                .member(member)
                .boardType(super.getBoardType())
                .subType(subType)
                .fileAttached(1)
                .build();
    }

}
