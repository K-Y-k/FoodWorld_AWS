package com.kyk.FoodWorld.board.domain.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;


/**
 * 글 수정시 전송될 전송객체
 */
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
public class FreeBoardUpdateForm extends UpdateFormBase {
    @NotBlank(message = "제목을 입력해주세요")
    @Size(max = 60, message = "최대 50글자입니다.")
    private String title;

    @NotBlank(message = "카테고리를 선택해주세요")
    private String subType;

    private List<MultipartFile> attachFiles;
}
