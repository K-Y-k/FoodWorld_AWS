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
public class MuckstarUpdateForm extends UpdateFormBase {
    private String subType;
}
