package com.kyk.FoodWorld.board.controller;


import com.kyk.FoodWorld.board.domain.dto.BoardUploadForm;
import com.kyk.FoodWorld.board.domain.dto.MuckstarUpdateForm;
import com.kyk.FoodWorld.board.domain.dto.MucstarUploadForm;
import com.kyk.FoodWorld.board.domain.entity.Board;
import com.kyk.FoodWorld.board.domain.entity.BoardFile;
import com.kyk.FoodWorld.board.service.BoardServiceImpl;
import com.kyk.FoodWorld.comment.domain.dto.CommentDto;
import com.kyk.FoodWorld.comment.domain.dto.CommentUpdateDto;
import com.kyk.FoodWorld.comment.domain.dto.CommentUploadDto;
import com.kyk.FoodWorld.comment.domain.entity.Comment;
import com.kyk.FoodWorld.comment.service.CommentServiceImpl;
import com.kyk.FoodWorld.like.service.LikeServiceImpl;
import com.kyk.FoodWorld.member.domain.LoginSessionConst;
import com.kyk.FoodWorld.member.domain.entity.Member;
import com.kyk.FoodWorld.member.domain.entity.ProfileFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/boards")
public class MuckstarBoardController {

    private final BoardServiceImpl boardService;
    private final LikeServiceImpl likeService;
    private final CommentServiceImpl commentService;

    @Value("${file.attachFileLocation}")
    private String attachFileLocation;

    /**
     * 먹스타그램 메인 폼 반환
     */
    @GetMapping("/muckstarBoard")
    public String muckstarBoards(Model model) {
        String boardType = "먹스타그램";
        Long firstCursorBoardId = boardService.findFirstCursorBoardId(boardType);

        model.addAttribute("firstCursorBoardId", firstCursorBoardId);
        log.info(String.valueOf(firstCursorBoardId));

        return "boards/muckstarboard/muckstarBoard_main";
    }


    /**
     * 글 상세 조회 폼
     */
    @GetMapping("/muckstarBoard/{boardId}")
    public String board_detail(@PathVariable Long boardId,
                               @SessionAttribute(name = LoginSessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                               @ModelAttribute("comment") CommentDto commentDto,
                               @ModelAttribute("commentUpload") CommentUploadDto commentUploadDto,
                               @ModelAttribute("commentUpdate") CommentUpdateDto commentUpdateDto,
                               @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
                               Model model) {
        int nowPage = 1;
        int startPage = 1;
        int endPage = 1;

        // 조회수 상승
        boardService.updateCount(boardId);

        // 게시글의 번호를 가진 좋아요 row의 개수 가져와서 보냄
        int likeCount = likeService.countByBoard_Id(boardId);
        model.addAttribute("likeCount", likeCount);

        // 좋아요 개수 업데이트
        boardService.updateLikeCount(boardId, likeCount);

        // 댓글 가져오기
        Board board = boardService.findById(boardId).orElseThrow(() ->
                new IllegalArgumentException("게시글 가져오기 실패: 게시글을 찾지 못했습니다." + boardId));
        Page<Comment> comments = commentService.findPageListByBoardId(pageable, boardId);

        if (comments != null && !comments.isEmpty()) {
            nowPage = pageable.getPageNumber() + 1;
            startPage = Math.max(1, nowPage - 2);
            endPage = Math.min(nowPage + 2, comments.getTotalPages());
            long commentCount = comments.getTotalElements();

            model.addAttribute("comments", comments);
            model.addAttribute("commentCount", commentCount);

            model.addAttribute("nowPage", nowPage);
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);

            model.addAttribute("hasPrev", comments.hasPrevious());
            model.addAttribute("hasNext", comments.hasNext());
        }
        else {
            model.addAttribute("nowPage", nowPage);
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
        }

        // 파일 가져오기
        List<BoardFile> boardFiles = board.getBoardFiles();
        if (boardFiles != null && !boardFiles.isEmpty()) {
            model.addAttribute("boardFiles", boardFiles);
        }

        // 작성자의 프로필 사진
        ProfileFile findProfileFile = board.getMember().getProfileFile();
        model.addAttribute("profileFile",findProfileFile);

        model.addAttribute("board", board);

        // 등록한 날이 오늘 날짜이면 시/분까지만 나타나게 조건을 설정하기 위해서 현재 시간을 객체로 담아 보낸 것
        model.addAttribute("localDateTime", LocalDateTime.now());

        return "boards/muckstarboard/muckstarBoard_detail";
    }

    /**
     * 댓글 등록
     */
    @PostMapping("/muckstarBoard/{boardId}/comment")
    public String commentUpload(@PathVariable Long boardId,
                                @SessionAttribute(name = LoginSessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                                @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
                                @Valid @ModelAttribute("commentUpload") CommentUploadDto commentUploadDto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        int nowPage = 1;
        int startPage = 1;
        int endPage = 1;

        // 세션에 회원 데이터가 없으면 홈 화면으로 이동
        if (loginMember == null) {
            log.info("로그인 상태가 아님");

            model.addAttribute("message", "회원만 댓글을 작성할 수 있습니다. 로그인 먼저 해주세요!");
            model.addAttribute("redirectUrl", "/members/login");
            return "messages";
        }

        log.info("바인딩 에러 정보 = {}", bindingResult);
        if (bindingResult.hasErrors()) {
            int likeCount = likeService.countByBoard_Id(boardId);
            model.addAttribute("likeCount", likeCount);

            boardService.updateLikeCount(boardId, likeCount);

            Board board = boardService.findById(boardId).orElseThrow(() ->
                    new IllegalArgumentException("게시글 가져오기 실패: 게시글을 찾지 못했습니다." + boardId));
            Page<Comment> comments = commentService.findPageListByBoardId(pageable, boardId);

            if (comments != null && !comments.isEmpty()) {
                nowPage = pageable.getPageNumber() + 1;
                startPage = Math.max(1, nowPage - 2);
                endPage = Math.min(nowPage + 2, comments.getTotalPages());
                long commentCount = comments.getTotalElements();

                model.addAttribute("comments", comments);
                model.addAttribute("commentCount", commentCount);

                model.addAttribute("nowPage", nowPage);
                model.addAttribute("startPage", startPage);
                model.addAttribute("endPage", endPage);

                model.addAttribute("hasPrev", comments.hasPrevious());
                model.addAttribute("hasNext", comments.hasNext());
            }
            else {
                model.addAttribute("nowPage", nowPage);
                model.addAttribute("startPage", startPage);
                model.addAttribute("endPage", endPage);
            }

            List<BoardFile> boardFiles = board.getBoardFiles();
            if (boardFiles != null && !boardFiles.isEmpty()) {
                model.addAttribute("boardFiles", boardFiles);
            }

            Member member = board.getMember();
            ProfileFile profileFile = member.getProfileFile();
            if (profileFile != null && !profileFile.getStoredFileName().isEmpty()) {
                model.addAttribute("profileFile", profileFile);
            }

            model.addAttribute("board", board);
            model.addAttribute("localDateTime", LocalDateTime.now());

            return "boards/muckstarboard/muckstarBoard_detail";
        }

        commentService.saveComment(loginMember.getId(), boardId, commentUploadDto);
        boardService.updateCommentCount(boardId);

        redirectAttributes.addAttribute("boardId", boardId);

        redirectAttributes.addAttribute("boardId", boardId);
        return "redirect:/boards/muckstarBoard/{boardId}";
    }

    /**
     * 댓글 수정
     */
    @PostMapping("/muckstarBoard/comments/{boardId}/{commentId}/edit")
    public String commentUpdate(@PathVariable Long boardId, @PathVariable Long commentId,
                                @SessionAttribute(name = LoginSessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                                @ModelAttribute("commentUpdate") CommentUpdateDto commentUpdateDto,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        // 세션에 회원 데이터가 없으면 홈 화면으로 이동
        if(loginMember == null) {
            log.info("로그인 상태가 아님");

            model.addAttribute("message", "로그인 먼저 해주세요!");
            model.addAttribute("redirectUrl", "/members/login");
            return "messages";
        }

        commentService.updateComment(commentId, commentUpdateDto);

        redirectAttributes.addAttribute("boardId", boardId);
        return "redirect:/boards/muckstarBoard/{boardId}";
    }

    /**
     * 댓글 삭제
     */
    @GetMapping("/muckstarBoard/comments/{boardId}/{commentId}/delete")
    public String commentDelete(@PathVariable Long boardId, @PathVariable Long commentId,
                                @SessionAttribute(name = LoginSessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        // 세션에 회원 데이터가 없으면 홈 화면으로 이동
        if(loginMember == null) {
            log.info("로그인 상태가 아님");

            model.addAttribute("message", "로그인 먼저 해주세요!");
            model.addAttribute("redirectUrl", "/members/login");
            return "messages";
        }

        commentService.delete(commentId);
        boardService.updateCommentCount(boardId);

        redirectAttributes.addAttribute("boardId", boardId);
        return "redirect:/boards/muckstarBoard/{boardId}";
    }

    /**
     * 글 등록 폼
     */
    @GetMapping("/muckstarBoard/upload")
    public String uploadForm(@SessionAttribute(name = LoginSessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                             @ModelAttribute("uploadForm") BoardUploadForm boardDto,
                             Model model) {
        // 세션에 회원 데이터가 없으면 홈 화면으로 이동
        if(loginMember == null) {
            log.info("로그인 상태가 아님");

            model.addAttribute("message", "회원만 글을 작성할 수 있습니다. 로그인 먼저 해주세요!");
            model.addAttribute("redirectUrl", "/members/login");
            return "messages";
        }

        log.info("로그인 상태 {}", loginMember);
        return "boards/muckstarboard/muckstarBoard_upload";
    }


    /**
     * 글 등록 기능
     */
    @PostMapping("/muckstarBoard/upload")
    public String boardUpload(@SessionAttribute(LoginSessionConst.LOGIN_MEMBER) Member loginMember,
                              @Valid @ModelAttribute("uploadForm") MucstarUploadForm boardDto, BindingResult bindingResult,
                              Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            return "boards/muckstarboard/muckstarBoard_upload";
        }

        if (boardDto.getImageFiles().get(0).getOriginalFilename().equals("")) {
            model.addAttribute("message", "먹스타그램은 사진이 필수입니다!");
            model.addAttribute("redirectUrl", "/boards/muckstarBoard/upload");
            return "messages";
        }

        boardService.muckstarUpload(loginMember.getId(), boardDto);
        return "redirect:/boards/muckstarBoard";
    }


    /**
     * 글 수정 폼
     */
    @GetMapping("/muckstarBoard/{boardId}/edit")
    public String editForm(@PathVariable Long boardId,
                           Model model) {
        Board board = boardService.findById(boardId).orElseThrow(() ->
                new IllegalArgumentException("게시글 가져오기 실패: 게시글을 찾지 못했습니다." + boardId));

        model.addAttribute("updateForm", board);

        return "boards/muckstarboard/muckstarBoard_edit";
    }

    /**
     * 글 수정 기능
     */
    @PostMapping("/muckstarBoard/{boardId}/edit")
    public String edit(@PathVariable Long boardId,
                       @Valid @ModelAttribute("updateForm") MuckstarUpdateForm updateParam, BindingResult bindingResult,
                       Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            Board board = boardService.findById(boardId).orElseThrow(() ->
                    new IllegalArgumentException("게시글 가져오기 실패: 게시글을 찾지 못했습니다." + boardId));

            model.addAttribute("updateForm", board);
            model.addAttribute("boardId", boardId);
            return "boards/muckstarBoard/muckstarBoard_edit";
        }

        boardService.updateBoard(boardId, null, null, updateParam);
        return "redirect:/boards/muckstarBoard/{boardId}";
    }


    /**
     * 글 삭제 기능
     */
    @GetMapping("/muckstarBoard/{boardId}/delete")
    public String delete(@PathVariable Long boardId,
                         @SessionAttribute(LoginSessionConst.LOGIN_MEMBER) Member loginMember,
                         Model model) {
        Board findBoard = boardService.findById(boardId).orElseThrow(() ->
                new IllegalArgumentException("게시글 가져오기 실패: 게시글을 찾지 못했습니다." + boardId));

        if (findBoard.getMember().getId().equals(loginMember.getId())) {
            boardService.delete(boardId, findBoard.getBoardType());
        } else {
            model.addAttribute("message", "회원님이 작성한 글만 삭제할 수 있습니다!");
            model.addAttribute("redirectUrl", "/boards/muckstarBoard");
            return "messages";
        }

        return "redirect:/boards/muckstarBoard";
    }


    /**
     * 상세 글에서 좋아요 업데이트 기능
     */
    @GetMapping ("/muckstarBoard/{boardId}/like")
    public String likeUpdate(@PathVariable Long boardId,
                             @SessionAttribute(name = LoginSessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if(loginMember == null) {
            log.info("로그인 상태가 아님");

            model.addAttribute("message", "회원만 좋아요를 누를 수 있습니다. 로그인 먼저 해주세요!");
            model.addAttribute("redirectUrl", "/members/login");
            return "messages";
        }

        likeService.saveLike(loginMember.getId(), boardId);
        redirectAttributes.addAttribute("boardId", boardId);

        return "redirect:/boards/muckstarBoard/{boardId}";
    }
}