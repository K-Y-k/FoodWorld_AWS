package com.kyk.FoodWorld.board.domain.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
public class RecommendBoardUpdateForm {
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

    private List<MultipartFile> attachFiles;

    private List<MultipartFile> imageFiles;

}
