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
public class BoardUploadForm extends UploadFormBase {
    @NotBlank(message = "제목을 입력해주세요")
    @Size(max = 60, message = "최대 50글자입니다.")
    private String title;

    @NotBlank(message = "카테고리를 선택해주세요")
    private String subType;
    private String area;
    private String menuName;
    private List<MultipartFile> attachFiles;


    public Board toSaveEntity(Member member) {
        return Board.builder()
                .title(title)
                .content(super.getContent())
                .writer(member.getName())
                .member(member)
                .boardType(super.getBoardType())
                .subType(subType)
                .area(area)
                .menuName(menuName)
                .fileAttached(0)
                .build();
    }
}