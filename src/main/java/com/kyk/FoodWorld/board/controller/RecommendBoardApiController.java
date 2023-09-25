//package com.kyk.FoodWorld.board.controller;
//
//
//import com.kyk.FoodWorld.board.domain.dto.BoardSearchCond;
//import com.kyk.FoodWorld.board.domain.entity.Board;
//import com.kyk.FoodWorld.board.service.BoardServiceImpl;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/boards")
//@RequiredArgsConstructor
//public class RecommendBoardApiController {
//    private final BoardServiceImpl boardService;
//
//    @GetMapping("/api/recommendBoard")
//    public ResponseEntity<?> pageList(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
//                                      BoardSearchCond boardSearchDto) {
//
//        String boardType = "추천게시판";
//
//        Page<Board> boards = boardService.categoryBoardList(boardType, boardSearchDto, pageable);
//
//        for (Board board : boards) {
//            Board findBoard = boardService.findById(board.getId()).orElseThrow(() ->
//                    new IllegalArgumentException("게시글 가져오기 실패: 게시글을 찾지 못했습니다." + board.getId()));
//            boardService.updateCommentCount(findBoard.getId());
//        }
//
//        int nowPage = pageable.getPageNumber() + 1;
//
//        int startPage = Math.max(1, nowPage - 2);
//        int endPage = Math.min(nowPage + 2, boards.getTotalPages());
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("boards", boards.getContent());
//        response.put("startPage", startPage);
//        response.put("nowPage", nowPage);
//        response.put("endPage", endPage);
//        response.put("localDateTime", LocalDateTime.now());
//        response.put("previous", pageable.previousOrFirst().getPageNumber());
//        response.put("next", pageable.next().getPageNumber());
//        response.put("hasPrev", boards.hasPrevious());
//        response.put("hasNext", boards.hasNext());
//
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//}
