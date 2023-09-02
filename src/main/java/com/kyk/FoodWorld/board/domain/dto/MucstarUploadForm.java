package com.kyk.FoodWorld.board.domain.dto;


import com.kyk.FoodWorld.board.domain.entity.Board;
import com.kyk.FoodWorld.member.domain.entity.Member;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class MucstarUploadForm {
    private Long id;

    @NotBlank(message = "내용을 입력해주세요")
    @Size(max = 500, message = "최대 500글자입니다.")
    private String content;

    @NotEmpty(message = "게시판을 선택해주세요!")
    private String boardType;
    private String subType;

    private List<MultipartFile> imageFiles;
    private List<String> originalFileName;
    private List<String> storedFileName;


    public Board toSaveFileEntity(Member member, MucstarUploadForm boardDto) {
        return Board.builder()
                .title("먹스타그램 게시글")
                .content(content)
                .writer(member.getName())
                .member(member)
                .boardType(boardType)
                .subType(subType)
                .fileAttached(1)
                .build();
    }

}
