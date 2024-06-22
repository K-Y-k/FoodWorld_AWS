package com.kyk.FoodWorld.board.domain.dto;

import com.kyk.FoodWorld.board.domain.entity.Board;
import com.kyk.FoodWorld.member.domain.entity.Member;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;


@Data
public class UploadFormBase {
    private Long id;
    @NotBlank(message = "내용을 입력해주세요")
    @Size(max = 500, message = "최대 500글자입니다.")
    private String content;

    private String boardType;
    private List<MultipartFile> imageFiles;

    public Board toSaveEntity(Member findMember) {
        return null;
    }
}
