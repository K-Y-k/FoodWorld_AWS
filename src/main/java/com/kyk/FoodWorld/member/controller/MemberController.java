package com.kyk.FoodWorld.member.controller;


import com.kyk.FoodWorld.member.domain.LoginSessionConst;
import com.kyk.FoodWorld.member.domain.dto.JoinForm;
import com.kyk.FoodWorld.member.domain.dto.LoginForm;
import com.kyk.FoodWorld.member.domain.dto.UpdateForm;
import com.kyk.FoodWorld.member.domain.entity.Member;
import com.kyk.FoodWorld.member.domain.entity.ProfileFile;
import com.kyk.FoodWorld.member.service.MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberServiceImpl memberService;
//    private final BoardServiceImpl boardService;
//    private final FollowServiceImpl followService;


    /**
     *  회원 등록 폼
     */
    @GetMapping("/join")
    public String memberRegisterForm(@ModelAttribute("joinForm") JoinForm form) { // 컨트롤러에서 뷰로 넘어갈 때 이 데이터를 넣어 보낸다.
        return "members/member_join";
    }

    /**
     *  회원 저장 기능
     */
    @PostMapping("/join")
    public String save(@Valid @ModelAttribute("joinForm") JoinForm form,
                       BindingResult bindingResult,
                       Model model) {
        if (bindingResult.hasErrors()) {
            return "members/member_join";
        }
        
        // 문자열에 빈 공백 있는지 검사
        String memberName = form.getName();
        String loginId = form.getLoginId();
        String password = form.getPassword();

        String messages = checkSpace(memberName, model, "닉네임에 빈 공간이 올 수 없습니다!");
        if (messages != null) return messages;

        String messages1 = checkSpace(loginId, model, "아이디에 빈 공간이 올 수 없습니다!");
        if (messages1 != null) return messages1;

        String messages2 = checkSpace(password, model, "비밀번호에 빈 공간이 올 수 없습니다!");
        if (messages2 != null) return messages2;


        memberService.join(form);
        model.addAttribute("message", "회원가입 되었습니다!");
        model.addAttribute("redirectUrl", "/");
        return "messages";
    }

    // 문자열에 빈 공백 있는지 검사 메서드
    private static String checkSpace(String memberName, Model model, String attributeValue) {
        for (int i = 0; i < memberName.length(); i++) {
            char nameChar = memberName.charAt(i);
            if (nameChar == ' ') {
                model.addAttribute("message", attributeValue);
                model.addAttribute("redirectUrl", "/members/join");
                return "messages";
            }
        }
        return null;
    }


    /**
     *  로그인 폼
     */
    @GetMapping("/login")
    public String memberLoginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "members/member_login";
    }

    /**
     *  로그인 처리 기능
     */
    // 서블릿에서도 세션을 지원하는 방식 (HttpServletRequest로 활용)
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm loginForm,
                        BindingResult bindingResult,
                        HttpServletRequest request) {
        // 검증 실패 : 오류가 있으면 폼으로 반환
        if (bindingResult.hasErrors()) {
            return "members/member_login";
        }

        // 성공 로직 : 로그인 기능 적용후 멤버에 저장
        Member loginMember = memberService.login(loginForm.getLoginId(), loginForm.getPassword()); // 폼에 입력한 아이디 패스워드 가져와서 멤버로 저장
        log.info("login? {}", loginMember);

        // 로그인 성공 처리
        // 세션을 생성해서 세션에 로그인 회원 정보를 보관한다.
        HttpSession session = request.getSession();
        session.setAttribute(LoginSessionConst.LOGIN_MEMBER, loginMember);

        log.info("로그인 성공");
        log.info("sessionId={}", session.getId());

        return "redirect:/";
    }

    /**
     * 로그아웃 기능
     */
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) { // SessionManager에서 request로 사용
        // request.getSession( ) 안에 false는 현재 세션이 있으면 기존 세션 반환, 세션이 없으면 null을 반환
        //                            true는 현재 세션이 있으면 기존 세션 반환, 세션이 없으면 새로운 세션 생성
        HttpSession session = request.getSession(false); // 로그아웃은 없으면 새로운 세션을 생성할 필요 없기에 false를 넣음

        if (session != null) { // 세션이 있으면
            session.invalidate(); // 해당 세션이랑 그 안의 데이터를 모두 지운다.
            log.info("로그아웃 완료");
        }

        return "redirect:/";
    }


    /**
     *  회원 프로필 조회 폼
     */
    @GetMapping("/profile/{memberId}")
    public String memberProfileForm(@SessionAttribute(name = LoginSessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                                    @PathVariable Long memberId,
                                    Model model) {
        String boardType = "먹스타그램";


        // 회원 정보
        Member member = memberService.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("회원 가져오기 실패: 회원을 찾지 못했습니다." + memberId));
        model.addAttribute("member", member);

        // 프로필 사진
        ProfileFile profileFile = member.getProfileFile();
        if (profileFile != null && !profileFile.getStoredFileName().isEmpty()) {
            model.addAttribute("profileFile", profileFile);
        }

//        // 해당 회원이 작성한 게시글 총 개수
//        int boardsTotalCount = boardService.boardsTotalCount(memberId);
//        model.addAttribute("boardsTotalCount", boardsTotalCount);
//
//        // 팔로우, 팔로워 수
//        int followCount = followService.countByToMember_Id(memberId);
//        model.addAttribute("followCount", followCount);
//
//        int followingCount = followService.countByFromMember_Id(memberId);
//        model.addAttribute("followingCount", followingCount);
//
//
//        // 현재 회원이 팔로우한 상태인지 확인
//        try {
//            Optional<Follow> followState = followService.findByFromMember_IdAndToMember_Id(loginMember.getId(), memberId);
//
//            if (followState.isPresent()){
//                model.addAttribute("unFollow", followState);
//            }
//        } catch (NullPointerException e){
//            log.info("NPE 에러");
//        }
//
//        // 회원의 팔로워 최근 id 가져오기
//        try {
//            Long firstCursorFollowerId = followService.findFirstCursorFollowerId(member);
//            log.info("최근 팔로워 id = {}", firstCursorFollowerId);
//            model.addAttribute("firstCursorFollowerId", firstCursorFollowerId);
//        } catch (NullPointerException e){
//            log.info("NPE 에러");
//        }
//
//        // 회원의 먹스타그램 최근 id 가져오기
//        try {
//            Long firstCursorBoardIdInMember = boardService.findFirstCursorBoardIdInMember(memberId, boardType);
//            model.addAttribute("firstCursorBoardId", firstCursorBoardIdInMember);
//        } catch (NullPointerException e){
//            log.info("NPE 에러");
//        }
//
//        // 회원과 연관된 팔로워들 추천 리스트
//        if (loginMember != null) {
//            List<Member> recommendMembers = followService.recommendMember(loginMember.getId());
//
//            if (!recommendMembers.isEmpty()) {
//                for (Member member1 : recommendMembers) {
//                    log.info("최종 결과 팔로우 추천 리스트 = {}", member1.getName());
//                }
//
//                model.addAttribute("recommendMembers", recommendMembers);
//            }
//        }

        return "members/member_profile";
    }


    /**
     * 프로필 수정 폼
     */
    @GetMapping("/profile/{memberId}/edit")
    public String memberProfileUpdateForm(@SessionAttribute(name = LoginSessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                                          @PathVariable Long memberId,
                                          Model model) {
        // 비회원이나 다른 회원이 접근 대비 제한
        if (loginMember == null || !Objects.equals(loginMember.getId(), memberId)) {
            model.addAttribute("message", "회원님의 프로필이 아닙니다!");
            model.addAttribute("redirectUrl", "/");
            return "messages";
        }

        Member member = memberService.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("회원 가져오기 실패: 회원을 찾지 못했습니다." + memberId));

        // 프로필 사진 가져오기
        ProfileFile profileFile = member.getProfileFile();
        if (profileFile != null && !profileFile.getStoredFileName().isEmpty()) {
            model.addAttribute("profileFile", profileFile);
        }

        model.addAttribute("updateForm", member);

        return "members/member_profileUpdate";
    }

    /**
     * 프로필 수정 기능
     */
    @PostMapping("/profile/{memberId}/edit")
    public String memberProfileUpdateForm(@PathVariable Long memberId,
                                          @Valid @ModelAttribute("updateForm") UpdateForm form, BindingResult bindingResult,
                                          Model model,
                                          HttpServletRequest request) throws IOException {
        if (bindingResult.hasErrors()) {
            Member member = memberService.findById(memberId).orElseThrow(() ->
                    new IllegalArgumentException("회원 가져오기 실패: 회원을 찾지 못했습니다." + memberId));

            // 프로필 사진 가져오기
            ProfileFile profileFile = member.getProfileFile();
            if (profileFile != null && !profileFile.getStoredFileName().isEmpty()) {
                model.addAttribute("profileFile", profileFile);
            }

            return "members/member_profileUpdate";
        }

        // 문자열에 빈 공백 있는지 검사
        String memberName = form.getName();
        String loginId = form.getLoginId();
        String password = form.getPassword();

        String messages = checkSpace(memberName, model, "닉네임에 빈 공간이 올 수 없습니다!");
        if (messages != null) return messages;

        String messages1 = checkSpace(loginId, model, "아이디에 빈 공간이 올 수 없습니다!");
        if (messages1 != null) return messages1;

        String messages2 = checkSpace(password, model, "비밀번호에 빈 공간이 올 수 없습니다!");
        if (messages2 != null) return messages2;


        // 업데이트 적용된 회원의 아이디를 반환한 것을 가져온 이유는 아래 새로 바뀐 내용으로 재갱신하기 위해서
        Long changeMemberId = memberService.changeProfile(memberId, form);


        // 메인페이지의 이름 동기화시키기 위해 로그아웃후 로그인을 새로 거치고 리다이렉트
        HttpSession getSession = request.getSession(false);

        if (getSession != null) {    // 세션이 있으면
            getSession.invalidate(); // 해당 세션이랑 그 안의 데이터를 모두 지운다.
        }

        Member member = memberService.findById(changeMemberId).orElseThrow(() ->
                new IllegalArgumentException("회원 가져오기 실패: 회원을 찾지 못했습니다." + changeMemberId));
        Member loginMember = memberService.login(member.getLoginId(), member.getPassword());

        HttpSession createSession = request.getSession();
        createSession.setAttribute("loginMember", loginMember);

        return "redirect:/";
    }


    /**
     * 회원 탈퇴
     */
    @GetMapping("/profile/{memberId}/delete")
    public String memberUnregister(@PathVariable Long memberId,
                                   HttpServletRequest request,
                                   Model model) {
        memberService.delete(memberId);

        // request.getSession( ) 안에 false는 현재 세션이 있으면 기존 세션 반환, 세션이 없으면 null을 반환
        //                            true는 현재 세션이 있으면 기존 세션 반환, 세션이 없으면 새로운 세션 생성
        HttpSession session = request.getSession(false); // 회원탈퇴는 없으면 새로운 세션을 생성할 필요 없기에 false를 넣음

        if (session != null) { // 세션이 있으면
            session.invalidate(); // 해당 세션이랑 그 안의 데이터를 모두 지운다.
        }

        model.addAttribute("message", "회원탈퇴 하였습니다.");
        model.addAttribute("redirectUrl", "/");
        return "messages";
    }


}
