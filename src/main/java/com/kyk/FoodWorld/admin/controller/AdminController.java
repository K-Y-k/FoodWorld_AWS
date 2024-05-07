package com.kyk.FoodWorld.admin.controller;


import com.kyk.FoodWorld.board.domain.dto.BoardSearchCond;
import com.kyk.FoodWorld.board.domain.entity.Board;
import com.kyk.FoodWorld.board.service.BoardServiceImpl;
import com.kyk.FoodWorld.chat.domain.dto.ChatSearchCond;
import com.kyk.FoodWorld.chat.domain.entity.ChatRoom;
import com.kyk.FoodWorld.chat.service.ChatService;
import com.kyk.FoodWorld.member.domain.LoginSessionConst;
import com.kyk.FoodWorld.member.domain.dto.MemberSearchCond;
import com.kyk.FoodWorld.member.domain.entity.Member;
import com.kyk.FoodWorld.member.service.MemberServiceImpl;
import com.kyk.FoodWorld.menu.domain.dto.MenuSearchCond;
import com.kyk.FoodWorld.menu.domain.entity.MenuRecommend;
import com.kyk.FoodWorld.menu.service.MenuRecommendServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MemberServiceImpl memberService;
    private final BoardServiceImpl boardService;
    private final MenuRecommendServiceImpl menuRecommendService;
    private final ChatService chatService;

    
    /**
     * 회원 관리 폼
     */
    @GetMapping("/members")
    public String adminMember(@SessionAttribute(name = LoginSessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                              @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                              Model model,
                              MemberSearchCond memberSearchCond) {
        // 관리자 체크
        String messages = checkAdmin(loginMember, model);
        if (messages != null) return messages;

        Page<Member> members;
        String memberSearchKeyword = memberSearchCond.getMemberSearchKeyword();

        // 검색 키워드에 따른 페이징된 회원 가저오기
        if (memberSearchKeyword == null) {
            members = memberService.findPageBy(pageable);
        } else {
            members = memberService.findByNameContaining(memberSearchKeyword, pageable);
        }

        // 회원 페이징 모델
        int nowPage = pageable.getPageNumber() + 1;
        int startPage = Math.max(1, nowPage - 2);
        int endPage = Math.min(nowPage + 2, members.getTotalPages());

        model.addAttribute("members", members);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        model.addAttribute("previous", pageable.previousOrFirst().getPageNumber());
        model.addAttribute("next", pageable.next().getPageNumber());

        model.addAttribute("hasPrev", members.hasPrevious());
        model.addAttribute("hasNext", members.hasNext());
        
        model.addAttribute("localDateTime", LocalDateTime.now());
        model.addAttribute("memberSearchKeyword", memberSearchKeyword);

        return "admin/admin_member";
    }

    // 관리자 체크 메서드
    private static String checkAdmin(Member loginMember, Model model) {
        if (loginMember == null) {
            log.info("로그인 상태가 아님");

            model.addAttribute("message", "로그인 먼저 해주세요!");
            model.addAttribute("redirectUrl", "/members/login");
            return "messages";
        } else if (!loginMember.getRole().equals("ADMIN")) {
            log.info("관리자님이 아닙니다.");

            model.addAttribute("message", "관리자님이 아닙니다.");
            model.addAttribute("redirectUrl", "/");
            return "messages";
        }
        return null;
    }

    /**
     * 회원 추방 기능
     */
    @GetMapping("/member/delete/{memberId}")
    public String memberDelete(@PathVariable Long memberId,
                               @SessionAttribute(name = LoginSessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                               Model model) {
        if (loginMember == null) {
            log.info("로그인 상태가 아님");

            model.addAttribute("message", "로그인 먼저 해주세요!");
            model.addAttribute("redirectUrl", "/members/login");
            return "messages";
        } else if (loginMember.getRole().equals("ADMIN")) {
            memberService.delete(memberId);
        } else {
            log.info("관리자님이 아닙니다.");

            model.addAttribute("message", "관리자님이 아닙니다.");
            model.addAttribute("redirectUrl", "/");
            return "messages";
        }

        model.addAttribute("message", "회원 추방 성공");
        model.addAttribute("redirectUrl", "/admin/members");
        return "messages";
    }


    /**
     * 자유게시판 관리 폼
     */
    @GetMapping("/freeBoard")
    public String adminFreeBoard(@SessionAttribute(name = LoginSessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                                 @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                 Model model,
                                 BoardSearchCond boardSearchCond) {
        // 관리자 체크
        String messages = checkAdmin(loginMember, model);
        if (messages != null) return messages;

        Page<Board> boards;
        String boardType = "자유게시판";
        String writerSearchKeyword = boardSearchCond.getWriterSearchKeyword();
        String titleSearchKeyword = boardSearchCond.getTitleSearchKeyword();

        // 검색 키워드에 따른 페이징된 게시글 가저오기
        boards = searchKeyWord(pageable, boardType, writerSearchKeyword, titleSearchKeyword);

        // 댓글 개수
        boardCommentCount(boards);

        // 게시판 페이징 모델
        boardPagingModel(pageable, model, boardSearchCond, boards);

        return "admin/admin_freeBoard";
    }
    
    /**
     * 추천 게시판 관리 폼
     */
    @GetMapping("/recommendBoard")
    public String adminRecommendBoard(@SessionAttribute(name = LoginSessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                                 @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                 Model model,
                                 BoardSearchCond boardSearchCond) {
        // 관리자 체크
        String messages = checkAdmin(loginMember, model);
        if (messages != null) return messages;

        Page<Board> boards;
        String boardType = "추천게시판";
        String writerSearchKeyword = boardSearchCond.getWriterSearchKeyword();
        String titleSearchKeyword = boardSearchCond.getTitleSearchKeyword();

        // 키워드의 컬럼에 따른 페이징된 게시글 가저오기
        boards = searchKeyWord(pageable, boardType, writerSearchKeyword, titleSearchKeyword);

        // 댓글 개수
        boardCommentCount(boards);

        // 게시판 페이징 모델
        boardPagingModel(pageable, model, boardSearchCond, boards);

        return "admin/admin_recommendBoard";
    }

    /**
     * 먹스타그램 관리 폼
     */
    @GetMapping("/muckstarBoard")
    public String adminMuckstarBoard(@SessionAttribute(name = LoginSessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                                     @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                     Model model,
                                     BoardSearchCond boardSearchCond) {
        // 관리자 체크
        String messages = checkAdmin(loginMember, model);
        if (messages != null) return messages;

        Page<Board> boards;
        String boardType = "먹스타그램";
        String writerSearchKeyword = boardSearchCond.getWriterSearchKeyword();

        // 검색 키워드에 따른 페이징된 게시글 가저오기
        if (writerSearchKeyword == null) {
            boards = boardService.findPageListByBoardType(pageable, boardType);
        } else {
            boards = boardService.findByWriterContaining(writerSearchKeyword, pageable, boardType);
        }

        // 댓글 개수
        boardCommentCount(boards);

        // 게시글 페이징 모델
        boardPagingModel(pageable, model, boardSearchCond, boards);

        return "admin/admin_muckstarBoard";
    }

    // 게시글 검색에 따른 페이지 처리 메서드
    private Page<Board> searchKeyWord(Pageable pageable, String boardType, String writerSearchKeyword, String titleSearchKeyword) {
        Page<Board> boards;
        if (writerSearchKeyword == null && titleSearchKeyword == null) {
            boards = boardService.findPageListByBoardType(pageable, boardType);
        } else if (writerSearchKeyword.isEmpty()) {
            boards = boardService.findByTitleContaining(titleSearchKeyword, pageable, boardType);
        } else if (titleSearchKeyword.isEmpty()) {
            boards = boardService.findByWriterContaining(writerSearchKeyword, pageable, boardType);
        } else {
            boards = boardService.findByTitleContainingAndWriterContaining(titleSearchKeyword, writerSearchKeyword, pageable, boardType);
        }
        return boards;
    }
    
    // 댓글 개수 메서드
    private void boardCommentCount(Page<Board> boards) {
        for (Board board : boards) {
            Board findBoard = boardService.findById(board.getId()).orElseThrow(() ->
                    new IllegalArgumentException("게시글 가져오기 실패: 게시글을 찾지 못했습니다." + board.getId()));
            boardService.updateCommentCount(findBoard.getId());
        }
    }

    // 게시글 페이징 모델 메서드
    private void boardPagingModel(Pageable pageable, Model model, BoardSearchCond boardSearchCond, Page<Board> boards) {
        int nowPage = pageable.getPageNumber() + 1;
        int startPage = Math.max(1, nowPage - 2);
        int endPage = Math.min(nowPage + 2, boards.getTotalPages());

        model.addAttribute("boards", boards);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        model.addAttribute("previous", pageable.previousOrFirst().getPageNumber());
        model.addAttribute("next", pageable.next().getPageNumber());

        model.addAttribute("hasPrev", boards.hasPrevious());
        model.addAttribute("hasNext", boards.hasNext());

        model.addAttribute("localDateTime", LocalDateTime.now());
        model.addAttribute("writerSearchKeyword", boardSearchCond.getWriterSearchKeyword());
        model.addAttribute("titleSearchKeyword", boardSearchCond.getTitleSearchKeyword());
    }
    
    /**
     * 게시글 삭제 기능
     */
    @GetMapping("/boards/delete/{boardType}/{boardId}")
    public String boardDelete(@PathVariable String boardType,
                              @PathVariable Long boardId,
                              @SessionAttribute(name = LoginSessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        switch (boardType) {
            case "자유게시판":
                boardType = "freeBoard";
                break;
            case "추천게시판":
                boardType = "recommendBoard";
                break;
            case "먹스타그램":
                boardType = "muckstarBoard";
                break;
        }

        if (loginMember == null) {
            log.info("로그인 상태가 아님");

            model.addAttribute("message", "로그인 먼저 해주세요!");
            model.addAttribute("redirectUrl", "/members/login");
            return "messages";
        } else if (loginMember.getRole().equals("ADMIN")) {
            boardService.delete(boardId, boardType);
        } else {
            log.info("관리자님이 아닙니다.");

            model.addAttribute("message", "관리자님이 아닙니다.");
            model.addAttribute("redirectUrl", "/");
            return "messages";
        }

        model.addAttribute("message", "게시글 삭제 성공");
        redirectAttributes.addAttribute("boardId", boardId);
        model.addAttribute("redirectUrl", "/admin/"+boardType);
        return "messages";
    }


    /**
     * 메뉴 랜덤 추천 관리 폼
     */
    @GetMapping("/menu")
    public String adminMenu(@SessionAttribute(name = LoginSessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                            Model model,
                            MenuSearchCond menuSearchCond) {
        // 관리자 체크
        String messages = checkAdmin(loginMember, model);
        if (messages != null) return messages;

        // 검색 키워드에 따른 페이징된 메뉴 가저오기
        Page<MenuRecommend> menuRecommends = menuRecommendService.categoryMenuList(menuSearchCond, pageable);

        // 메뉴 페이징 모델
        int nowPage = pageable.getPageNumber() + 1;
        int startPage = Math.max(1, nowPage - 2);
        int endPage = Math.min(nowPage + 2, menuRecommends.getTotalPages());

        model.addAttribute("menuRecommends", menuRecommends);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        model.addAttribute("previous", pageable.previousOrFirst().getPageNumber());
        model.addAttribute("next", pageable.next().getPageNumber());

        model.addAttribute("hasPrev", menuRecommends.hasPrevious());
        model.addAttribute("hasNext", menuRecommends.hasNext());

        model.addAttribute("localDateTime", LocalDateTime.now());
        model.addAttribute("menuNameKeyword", menuSearchCond.getMenuNameKeyword());
        model.addAttribute("franchisesKeyword", menuSearchCond.getFranchisesKeyword());

        return "admin/admin_menu";
    }

    /**
     * 메뉴 삭제 기능
     */
    @GetMapping("/menu/delete/{menuRecommendId}")
    public String menuDelete(@PathVariable Long menuRecommendId,
                             @SessionAttribute(name = LoginSessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                             Model model) throws IOException {
        if (loginMember == null) {
            log.info("로그인 상태가 아님");

            model.addAttribute("message", "로그인 먼저 해주세요!");
            model.addAttribute("redirectUrl", "/members/login");
            return "messages";
        } else if (loginMember.getRole().equals("ADMIN")) {
            menuRecommendService.delete(menuRecommendId);
        } else {
            log.info("관리자님이 아닙니다.");

            model.addAttribute("message", "관리자님이 아닙니다.");
            model.addAttribute("redirectUrl", "/");
            return "messages";
        }

        model.addAttribute("message", "메뉴 삭제 성공");
        model.addAttribute("redirectUrl", "/admin/menu");
        return "messages";
    }


    /**
     * 채팅방 관리 폼
     */
    @GetMapping("/chat")
    public String adminChatRoom(@SessionAttribute(name = LoginSessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                                @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                Model model,
                                ChatSearchCond chatSearchCond) {
        String messages = checkAdmin(loginMember, model);
        if (messages != null) return messages;

        // 검색 키워드의 컬럼에 따른 페이징된 채팅방 가저오기
        Page<ChatRoom> chatRooms = chatService.searchChatRoomByMember(chatSearchCond.getMemberSearchKeyword(), pageable);

        // 채팅방 페이징 모델
        int nowPage = pageable.getPageNumber() + 1;
        int startPage = Math.max(1, nowPage - 2);
        int endPage = Math.min(nowPage + 2, chatRooms.getTotalPages());

        model.addAttribute("chatRooms", chatRooms);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        model.addAttribute("previous", pageable.previousOrFirst().getPageNumber());
        model.addAttribute("next", pageable.next().getPageNumber());

        model.addAttribute("hasPrev", chatRooms.hasPrevious());
        model.addAttribute("hasNext", chatRooms.hasNext());

        model.addAttribute("localDateTime", LocalDateTime.now());
        model.addAttribute("memberSearchKeyword", chatSearchCond.getMemberSearchKeyword());

        return "admin/admin_chat";
    }

    /**
     * 채팅방 삭제 기능
     */
    @GetMapping("/chatRoom/delete/{chatRoomId}")
    public String chatRoomDelete(@PathVariable String chatRoomId,
                                 @SessionAttribute(name = LoginSessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                                 Model model) {
        if (loginMember == null) {
            log.info("로그인 상태가 아님");

            model.addAttribute("message", "로그인 먼저 해주세요!");
            model.addAttribute("redirectUrl", "/members/login");
            return "messages";
        } else if (loginMember.getRole().equals("ADMIN")) {
            ChatRoom findChatRoom = chatService.findRoomByRoomId(chatRoomId);
            chatService.delete(findChatRoom);
        } else {
            log.info("관리자님이 아닙니다.");

            model.addAttribute("message", "관리자님이 아닙니다.");
            model.addAttribute("redirectUrl", "/");
            return "messages";
        }

        model.addAttribute("message", "채팅방 삭제 성공");
        model.addAttribute("redirectUrl", "/admin/chat");
        return "messages";
    }
}
