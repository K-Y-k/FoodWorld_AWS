package com.kyk.FoodWorld.board.domain.dto;

import com.kyk.FoodWorld.board.domain.entity.Board;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
public class BoardListDto {
    private Long id;
    private String title;
    private String writer;
    private String content;
    private LocalDateTime createdDate;
    private String boardType;
    private String subType;
    private int count;
    private int likeCount;

    public static BoardListDto toBoardListDto(Board board) {
        BoardListDto boardDto = new BoardListDto();
        boardDto.setId(board.getId());
        boardDto.setTitle(board.getTitle());
        boardDto.setWriter(board.getWriter());
        boardDto.setContent(boardDto.getContent());
        boardDto.setCreatedDate(board.getCreatedDate());
        boardDto.setBoardType(board.getBoardType());
        boardDto.setSubType(board.getSubType());
        boardDto.setCount(board.getCount());
        boardDto.setLikeCount(board.getLikeCount());

        return boardDto;
    }

}
