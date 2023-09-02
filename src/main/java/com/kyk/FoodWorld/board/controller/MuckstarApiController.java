package com.kyk.FoodWorld.board.controller;


import com.kyk.FoodWorld.board.domain.entity.Board;
import com.kyk.FoodWorld.board.service.BoardServiceImpl;
import com.kyk.FoodWorld.like.service.LikeServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class MuckstarApiController {

    private final BoardServiceImpl boardService;
    private final LikeServiceImpl likeService;

    /**
     * ajax 비동기로 받은 마지막 id를 기준으로 json 변형후 보내줌
     */
    @GetMapping("/api/muckstarBoard")
    public ResponseEntity<?> muckstarBoardsScroll(@RequestParam(value = "lastCursorBoardId", defaultValue = "0") Long lastCursorBoardId,
                                                  @RequestParam(value = "first") Boolean first,
                                                  @RequestParam(value = "writerSearchKeyword") String writerSearchKeyword,
                                                  @PageableDefault(size=3) Pageable pageable) {
        String boardType = "먹스타그램";

        if (writerSearchKeyword.isBlank()) {
            Slice<Board> boards = boardService.searchBySlice("", lastCursorBoardId, first, pageable, boardType);
            return new ResponseEntity<>(boards, HttpStatus.OK);
        }
        else {
            Slice<Board> boards = boardService.searchBySliceByWriter("", lastCursorBoardId, first, writerSearchKeyword, pageable, boardType);
            return new ResponseEntity<>(boards, HttpStatus.OK);
        }
    }


    /**
     * 메인 글에서 좋아요 업데이트 기능
     */
    @GetMapping("/api/muckstarBoard/{boardId}/like")
    public ResponseEntity<?> likeUpdate(@PathVariable Long boardId,
                                        @RequestParam(value = "userId") Long memberId) {

        log.info("게시물 id = {}", boardId);
        log.info("회원 = {}", memberId);

        int likeCount = likeService.saveLike(memberId, boardId);
        boardService.updateLikeCount(boardId, likeCount);

        Board findBoard = boardService.findById(boardId).orElseThrow(() ->
                new IllegalArgumentException("게시글 조회 실패: 해당 게시글이 존재하지 않습니다." + boardId));

        int resultLikeCount = findBoard.getLikeCount();

        return new ResponseEntity<>(resultLikeCount, HttpStatus.OK);
    }
}
