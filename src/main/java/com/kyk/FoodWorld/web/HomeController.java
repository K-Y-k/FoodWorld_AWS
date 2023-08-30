package com.kyk.FoodWorld.web;

import com.kyk.FoodWorld.member.domain.LoginSessionConst;
import com.kyk.FoodWorld.member.domain.dto.LoginForm;
import com.kyk.FoodWorld.member.domain.entity.Member;
import com.kyk.FoodWorld.member.service.MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberServiceImpl memberService;
//    private final BoardServiceImpl boardService;
//    private final LikeServiceImpl likeService;
//    private final ChatService chatService;


    /**
     *  메인 폼 + 로그인 세션 체크 기능 (@SessionAttribute 활용)
     */
    @GetMapping("/")
    public String homeLoginCheck(@SessionAttribute(name = LoginSessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                                 @ModelAttribute("loginForm") LoginForm form,
                                 Model model) {

//        // 각 게시판 인기글 조회
//        List<Board> freeBoards = boardService.popularBoardList("자유게시판");
//        List<Board> recommendBoards = boardService.popularBoardList("추천게시판");
//        List<Board> muckstarBoards = boardService.popularBoardList("먹스타그램");
//
//        // 각 게시판 댓글 개수 가져오는 작업
//        findCommentCount(freeBoards);
//        findCommentCount(recommendBoards);
//        findCommentCount(muckstarBoards);
//
//        // 각 게시판 모델링
//        model.addAttribute("freeBoards", freeBoards);
//        model.addAttribute("recommendBoards", recommendBoards);
//        model.addAttribute("muckstarBoards", muckstarBoards);
//
//
//        // 채팅방 목록 처리
//        if (loginMember != null) {
//            List<ChatRoom> member1ChatRoom = chatService.findNotLeaveMessageRoom(loginMember.getId());
//
//            if (!member1ChatRoom.isEmpty()) {
//                model.addAttribute("member1ChatRoom", member1ChatRoom);
//            }
//        }

        return "main";
    }


    /**
     * 로그인 기능
     */
    @PostMapping("/")
    public String login(@ModelAttribute LoginForm form,
                        HttpServletRequest request) {
        log.info("로그인 아이디={}", form.getLoginId());
        log.info("로그인 패스워드={}", form.getLoginId());


        // 성공시 로그인 기능 적용후 멤버에 저장 틀릴시 예외처리
        Member loginMember = memberService.login(form.getLoginId(), form.getPassword()); // 폼에 입력한 아이디 패스워드 가져와서 멤버로 저장
        log.info("login? {}", loginMember);


        // 로그인 성공 처리
        // HttpSession 객체에  request.getSession()로 담으면 됨
        // 세션이 있으면 있는 세션을 반환하고 없으면 신규 세션을 생성한다. (true일 때)
        HttpSession session = request.getSession(); // 기본 값이 true이고, false는 없으면 생성 안함
        // 세션에 로그인 회원 정보를 보관한다.
        session.setAttribute("loginMember", loginMember);

        log.info("sessionId={}", session.getId());
        return "redirect:/";
    }


//    /**
//     * 글 좋아요 업데이트 기능
//     */
//    @GetMapping ("/{boardId}/like")
//    public String likeUpdate(@PathVariable Long boardId,
//                             @SessionAttribute(name = LoginSessionConst.LOGIN_MEMBER, required = false) Member loginMember,
//                             Model model) {
//        if(loginMember == null) {
//            log.info("로그인 상태가 아님");
//
//            model.addAttribute("message", "회원만 좋아요를 누를 수 있습니다. 로그인 먼저 해주세요!");
//            model.addAttribute("redirectUrl", "/members/login");
//            return "messages";
//        }
//
//        int likeCount = likeService.saveLike(loginMember.getId(), boardId);
//        boardService.updateLikeCount(boardId, likeCount);
//
//        return "redirect:/";
//    }
//
//
//    /**
//     * 클릭한 채팅방 조회
//     */
//    @GetMapping("/room")
//    public String goChatRoom(@RequestParam String roomId,
//                             @SessionAttribute(name = LoginSessionConst.LOGIN_MEMBER) Member loginMember,
//                             Model model){
//        // 클릭한 방을 조회
//        ChatRoom targetChatRoom = chatService.findRoomByRoomId(roomId);
//        model.addAttribute("targetChatRoom", targetChatRoom);
//
//        // 클릭한 방의 대화 메시지 가져오기
//        List<ChatMessage> chatMessages = chatService.findTALKMessage(roomId);
//        model.addAttribute("chatMessages", chatMessages);
//
//        // 현재 회원의 전체 채팅방 리스트
//        List<ChatRoom> member1ChatRoom = chatService.findMember1ChatRoom(loginMember.getId());
//        model.addAttribute("member1ChatRoom", member1ChatRoom);
//
//        // 등록한 날이 오늘 날짜이면 시/분까지만 나타나게 조건을 설정하기 위해서 현재 시간을 객체로 담아 보낸 것
//        model.addAttribute("localDateTime", LocalDateTime.now());
//
//
//        // 인기글 가져오기
//        List<Board> freeBoards = boardService.popularBoardList("자유게시판");
//        List<Board> recommendBoards = boardService.popularBoardList("추천게시판");
//        List<Board> muckstarBoards = boardService.popularBoardList("먹스타그램");
//
//        // 댓글 개수 가져오는 작업
//        findCommentCount(freeBoards);
//        findCommentCount(recommendBoards);
//        findCommentCount(muckstarBoards);
//
//        model.addAttribute("freeBoards", freeBoards);
//        model.addAttribute("recommendBoards", recommendBoards);
//        model.addAttribute("muckstarBoards", muckstarBoards);
//
//        return "main";
//    }
//
//
//    private void findCommentCount(List<Board> baordList) {
//        for (Board board : baordList) {
//            Board findBoard = boardService.findById(board.getId()).orElseThrow(() ->
//                    new IllegalArgumentException("게시글 가져오기 실패: 게시글을 찾지 못했습니다." + board.getId()));
//            boardService.updateCommentCount(findBoard.getId());
//        }
//    }

}
