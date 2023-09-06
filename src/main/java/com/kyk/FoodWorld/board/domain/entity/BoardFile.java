package com.kyk.FoodWorld.board.domain.entity;

import com.kyk.FoodWorld.web.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BoardFile extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "boardFile_id")
    private Long id;

    private String originalFileName;

    private String storedFileName;

    private String path;

    private String attachedType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;


    public BoardFile(String storedFileName, Board board) {
        this.storedFileName = storedFileName;
        this.board = board;
    }

    // board 엔티티에 originalFileName, storedFileName 컬럼을 추가하지 않기 위해
    public static BoardFile toBoardFileEntity(Board board, String originalFileName, String storedFileName, String path, String attachedType) {
         return BoardFile.builder()
                 .originalFileName(originalFileName)
                 .storedFileName(storedFileName)
                 .path(path)
                 .board(board)
                 .attachedType(attachedType)
                 .build();
    }

    public void updateBoardFile(String originalFileName, String storedFileName, String path, String attachedType){
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.path = path;
        this.attachedType = attachedType;
    }


}
