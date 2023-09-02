package com.kyk.FoodWorld.admin.controller;


import com.kyk.FoodWorld.admin.dto.AdminBoardDTO;
import com.kyk.FoodWorld.admin.dto.AdminChatMessageDTO;
import com.kyk.FoodWorld.admin.dto.AdminCommentDTO;
import com.kyk.FoodWorld.admin.dto.AdminMenuRecommendDTO;
import com.kyk.FoodWorld.board.domain.entity.Board;
import com.kyk.FoodWorld.board.service.BoardServiceImpl;
import com.kyk.FoodWorld.comment.service.CommentServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final BoardServiceImpl boardService;
    private final CommentServiceImpl commentService;
//    private final MenuRecommendServiceImpl menuRecommendService;
//    private final ChatService chatService;


    /**
     * 선택한 회원의 게시글/댓글/메뉴 Slice 페이징 조회
     */
    @GetMapping("/api/member/{dataType}")
    public ResponseEntity<?> childScroll(@PathVariable String dataType,
                                         @RequestParam(value = "memberId") String memberId,
                                         @RequestParam(value = "lastCursorChildId", defaultValue = "0") Long lastCursorChildId,
                                         @RequestParam(value = "childFirst") Boolean first,
                                         @PageableDefault(size=5) Pageable pageable) {
        log.info("lastCursorChildId={}", lastCursorChildId);
        log.info("dataType={}", dataType);

        // 받아온 dataType(게시글/댓글/메뉴)에 따른 각 Slice 페이징 처리
        switch (dataType) {
            case "board":
                return getBoardSlicePaging(memberId, lastCursorChildId, first, pageable);
            case "comment":
                return getCommentSlicePaging(memberId, lastCursorChildId, first, pageable, true);
//            case "menu":
//                return getMenuSlicePaging(memberId, lastCursorChildId, first, pageable);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 게시판 Slice 페이징 메서드
    private ResponseEntity<?> getBoardSlicePaging(String memberId, Long lastCursorChildId, Boolean first, Pageable pageable) {
        if (lastCursorChildId == 0) {
            Long firstCursorBoardId = boardService.findFirstCursorBoardIdInMember(Long.valueOf(memberId), null);
            Slice<Board> boards = boardService.searchBySliceByWriter(memberId, firstCursorBoardId, first, "", pageable, null);
            boolean hasNext = boards.hasNext();

            List<AdminBoardDTO> boardDTOList = boards.stream()
                    .map(m -> new AdminBoardDTO(m.getId(), m.getTitle(), m.getContent(), m.getWriter(), m.getBoardType(), m.getCreatedDate(), m.getCount(), m.getLikeCount(), m.getCommentCount()))
                    .collect(Collectors.toList());

            for (AdminBoardDTO board : boardDTOList) {
                log.info("게시글 json={}", board.getContent());
            }

            Slice<AdminBoardDTO> sliceBoards = new SliceImpl<>(boardDTOList, pageable, hasNext);

            for (AdminBoardDTO sliceBoard : sliceBoards) {
                log.info("최종 게시글 json={}", sliceBoard.getContent());
            }

            if (sliceBoards.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(sliceBoards, HttpStatus.OK);
            }
        } else {
            Slice<Board> boards = boardService.searchBySliceByWriter(memberId, lastCursorChildId, first, "", pageable, null);

            boolean hasNext = boards.hasNext();

            List<AdminBoardDTO> boardDTOList = boards.stream()
                    .map(m -> new AdminBoardDTO(m.getId(), m.getTitle(), m.getContent(), m.getWriter(), m.getBoardType(), m.getCreatedDate(), m.getCount(), m.getLikeCount(), m.getCommentCount()))
                    .collect(Collectors.toList());

            for (AdminBoardDTO board : boardDTOList) {
                log.info("게시글 json={}", board.getContent());
            }

            Slice<AdminBoardDTO> sliceBoards = new SliceImpl<>(boardDTOList, pageable, hasNext);

            for (AdminBoardDTO sliceBoard : sliceBoards) {
                log.info("최종 게시글 json={}", sliceBoard.getContent());
            }

            if (sliceBoards.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(sliceBoards, HttpStatus.OK);
            }
        }
    }

    // 댓글 Slice 페이징 메서드
    private ResponseEntity<?> getCommentSlicePaging(String boardOrMemberId, Long lastCursorChildId, Boolean first, Pageable pageable, Boolean memberAdmin) {
        if (lastCursorChildId == 0) {
            Long firstCursorCommentId = commentService.findFirstCursorCommentId(boardOrMemberId, memberAdmin);
            Slice<AdminCommentDTO> sliceComments = commentService.searchBySlice(firstCursorCommentId, first, pageable, boardOrMemberId, memberAdmin);

            if (sliceComments.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(sliceComments, HttpStatus.OK);
            }
        }
        else {
            Slice<AdminCommentDTO> sliceComments = commentService.searchBySlice(lastCursorChildId, first, pageable, boardOrMemberId, memberAdmin);

            if (sliceComments.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(sliceComments, HttpStatus.OK);
            }
        }
    }

    // 메뉴 Slice 페이징 메서드
//    private ResponseEntity<?> getMenuSlicePaging(String memberId, Long lastCursorChildId, Boolean first, Pageable pageable) {
//        if (lastCursorChildId == 0) {
//            Long firstCursorMenuId = menuRecommendService.findFirstCursorMenuId(memberId);
//            log.info("첫 id={}", firstCursorMenuId);
//
//            Slice<AdminMenuRecommendDTO> sliceMenuRecommends = menuRecommendService.searchBySlice(firstCursorMenuId, first, pageable, memberId);
//
//            if (sliceMenuRecommends.isEmpty()) {
//                return new ResponseEntity<>(HttpStatus.OK);
//            }
//            else {
//                return new ResponseEntity<>(sliceMenuRecommends, HttpStatus.OK);
//            }
//        } else {
//            Slice<AdminMenuRecommendDTO> sliceMenuRecommends = menuRecommendService.searchBySlice(lastCursorChildId, first, pageable, memberId);
//
//            if (sliceMenuRecommends.isEmpty()) {
//                return new ResponseEntity<>(HttpStatus.OK);
//            }
//            else {
//                return new ResponseEntity<>(sliceMenuRecommends, HttpStatus.OK);
//            }
//        }
//    }

    /**
     * 선택한 회원의 게시글/댓글/메뉴 삭제 후 Slice 페이징 조회
     */
    @GetMapping("/api/member/{dataType}/delete")
    public ResponseEntity<?> delete(@PathVariable String dataType,
                                    @RequestParam(value = "memberId") String memberId,
                                    @RequestParam(value = "childId") String childId,
                                    @RequestParam(value = "lastCursorChildId", defaultValue = "0") Long lastCursorChildId,
                                    @RequestParam(value = "childFirst") Boolean first,
                                    @PageableDefault(size=5) Pageable pageable) throws IOException {
        log.info("dataType={}", dataType);
        log.info("lastCursorChildId={}", lastCursorChildId);
        log.info("childId={}", childId);
        log.info("memberId={}", memberId);
        log.info("childFirst={}", first);

        // 받아온 dataType(게시글/댓글/메뉴)에 따른 각 삭제후 Slice 페이징 처리
        switch (dataType) {
            case "comment":
                commentService.delete(Long.valueOf(childId));
                return getCommentSlicePaging(memberId, lastCursorChildId, first, pageable, true);

            case "board":
                Board findBoard = boardService.findById(Long.valueOf(childId)).orElseThrow(() ->
                        new IllegalArgumentException("게시글 조회 실패: " + Long.valueOf(childId)));
                boardService.delete(findBoard.getId(), findBoard.getBoardType());
                return getBoardSlicePaging(memberId, lastCursorChildId, first, pageable);

//            case "menu":
//                MenuRecommend findMenu = menuRecommendService.findById(Long.valueOf(childId)).orElseThrow(() ->
//                        new IllegalArgumentException("메뉴 조회 실패: " + Long.valueOf(childId)));
//                menuRecommendService.delete(findMenu.getId());
//                return getMenuSlicePaging(memberId, lastCursorChildId, first, pageable);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * 선택한 게시글의 댓글 Slice 페이징 조회
     */
    @GetMapping("/api/comment")
    public ResponseEntity<?> commentScroll(@RequestParam(value = "boardId") String boardId,
                                           @RequestParam(value = "lastCursorCommentId", defaultValue = "0") Long lastCursorCommentId,
                                           @RequestParam(value = "commentFirst") Boolean first,
                                           @PageableDefault(size=5) Pageable pageable) {
        log.info("lastCursorCommentId={}", lastCursorCommentId);
        return getCommentSlicePaging(boardId, lastCursorCommentId, first, pageable, false);
    }

    /**
     * 선택한 게시글의 댓글 삭제 후 Slice 페이징 조회
     */
    @GetMapping("/api/comment/delete")
    public ResponseEntity<?> deleteComment(@RequestParam(value = "boardId") String boardId,
                                           @RequestParam(value = "commentId") String commentId,
                                           @RequestParam(value = "lastCursorCommentId", defaultValue = "0") Long lastCursorCommentId,
                                           @RequestParam(value = "commentFirst") Boolean first,
                                           @PageableDefault(size=5) Pageable pageable) {
        log.info("lastCursorCommentId={}", lastCursorCommentId);

        commentService.delete(Long.valueOf(commentId));
        return getCommentSlicePaging(boardId, lastCursorCommentId, first, pageable, false);
    }


    /**
     * 선택한 채팅방의 채팅 메시지 Slice 페이징 조회
     */
//    @GetMapping("/api/chatMessage")
//    public ResponseEntity<?> chatMessageScroll(@RequestParam(value = "chatRoomId") String chatRoomId,
//                                               @RequestParam(value = "lastCursorChatMessageId", defaultValue = "0") Long lastCursorChatMessageId,
//                                               @RequestParam(value = "chatMessageFirst") Boolean first,
//                                               @PageableDefault(size=5) Pageable pageable) {
//        log.info("lastCursorChatMessageId={}", lastCursorChatMessageId);
//        return getChatMessageSlicePaging(chatRoomId, lastCursorChatMessageId, first, pageable);
//    }

    /**
     * 선택한 채팅 메시지 삭제 후 Slice 페이징 조회
     */
//    @GetMapping("/api/chatMessage/delete")
//    public ResponseEntity<?> deleteChatMessage(@RequestParam(value = "chatRoomId") String chatRoomId,
//                                               @RequestParam(value = "chatMessageId") String chatMessageId,
//                                               @RequestParam(value = "lastCursorChatMessageId", defaultValue = "0") Long lastCursorChatMessageId,
//                                               @RequestParam(value = "chatMessageFirst") Boolean first,
//                                               @PageableDefault(size=5) Pageable pageable) {
//        log.info("lastCursorChatMessageId={}", lastCursorChatMessageId);
//        log.info("chatMessageId={}", chatMessageId);
//        log.info("chatRoomId={}", chatRoomId);
//
//        chatService.deleteChatMessage(Long.valueOf(chatMessageId));
//        return getChatMessageSlicePaging(chatRoomId, lastCursorChatMessageId, first, pageable);
//    }

    // 채팅 메시지 Slice 페이징 메서드
//    private ResponseEntity<?> getChatMessageSlicePaging(String chatRoomId, Long lastCursorChatMessageId, Boolean first, Pageable pageable) {
//        if (lastCursorChatMessageId == 0) {
//            Long firstCursorChatMessageId = chatService.findFirstCursorChatMessageId(chatRoomId);
//            Slice<AdminChatMessageDTO> sliceComments = chatService.searchBySlice(firstCursorChatMessageId, first, pageable, chatRoomId);
//
//            if (sliceComments.isEmpty()) {
//                return new ResponseEntity<>(HttpStatus.OK);
//            }
//            else {
//                return new ResponseEntity<>(sliceComments, HttpStatus.OK);
//            }
//        }
//        else {
//            Slice<AdminChatMessageDTO> sliceComments = chatService.searchBySlice(lastCursorChatMessageId, first, pageable, chatRoomId);
//
//            if (sliceComments.isEmpty()) {
//                return new ResponseEntity<>(HttpStatus.OK);
//            }
//            else {
//                return new ResponseEntity<>(sliceComments, HttpStatus.OK);
//            }
//        }
//    }

}
