package com.kyk.FoodWorld.board.domain.dto;


import com.kyk.FoodWorld.board.domain.entity.Board;
import com.kyk.FoodWorld.member.domain.entity.Member;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 글 저장 전송 객체
 */
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
public class BoardUploadForm {
    private Long id;

    @NotBlank(message = "제목을 입력해주세요")
    @Size(max = 60, message = "최대 50글자입니다.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요")
    @Size(max = 500, message = "최대 500글자입니다.")
    private String content;

    private String boardType;
    @NotBlank(message = "카테고리를 선택해주세요")
    private String subType;
    private String area;
    private String menuName;

    private List<MultipartFile> imageFiles;
    private List<MultipartFile> attachFiles;
    private int fileAttached;


    public Board toSaveEntity(Member member, BoardUploadForm boardDto) {
        return Board.builder()
                .title(title)
                .content(content)
                .writer(member.getName())
                .member(member)
                .boardType(boardType)
                .subType(subType)
                .area(boardDto.getArea())
                .menuName(boardDto.getMenuName())
                .fileAttached(0)
                .build();
    }
    public Board toSaveFileEntity(Member member, BoardUploadForm boardDto) {
        return Board.builder()
                .title(title)
                .content(content)
                .writer(member.getName())
                .member(member)
                .boardType(boardType)
                .subType(subType)
                .area(boardDto.getArea())
                .menuName(boardDto.getMenuName())
                .fileAttached(1)
                .build();
    }


    public BoardUploadForm(String boardType) {
        this.boardType = boardType;
    }

}