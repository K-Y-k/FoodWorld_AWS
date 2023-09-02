package com.kyk.FoodWorld.comment.domain.dto;


import com.kyk.FoodWorld.board.domain.entity.Board;
import com.kyk.FoodWorld.comment.domain.entity.Comment;
import com.kyk.FoodWorld.member.domain.entity.Member;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 댓글 등록 Dto
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CommentUploadDto {
    private Long id;
    @NotBlank(message = "내용을 입력해주세요")
    @Size(max = 250, message = "최대 250글자입니다.")
    private String content;


    // Dto -> Entity
    public Comment toEntity(Member member, Board board) {
        return Comment.builder()
                .content(content)
                .writer(member.getName())
                .member(member)
                .board(board)
                .build();
    }

}
