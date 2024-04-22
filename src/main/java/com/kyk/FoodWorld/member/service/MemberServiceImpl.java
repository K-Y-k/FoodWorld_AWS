package com.kyk.FoodWorld.member.service;


import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.kyk.FoodWorld.board.domain.entity.Board;
import com.kyk.FoodWorld.board.domain.entity.BoardFile;
import com.kyk.FoodWorld.board.repository.BoardFileRepository;
import com.kyk.FoodWorld.board.repository.BoardRepository;
import com.kyk.FoodWorld.exception.member.DuplicatedMemberLoginIdException;
import com.kyk.FoodWorld.exception.member.MemberNotFoundException;
import com.kyk.FoodWorld.exception.member.DuplicatedMemberNameException;
import com.kyk.FoodWorld.member.domain.dto.JoinForm;
import com.kyk.FoodWorld.member.domain.dto.UpdateForm;
import com.kyk.FoodWorld.member.domain.entity.Member;
import com.kyk.FoodWorld.member.domain.entity.ProfileFile;
import com.kyk.FoodWorld.member.repository.MemberRepository;
import com.kyk.FoodWorld.menu.domain.entity.MenuRecommend;
import com.kyk.FoodWorld.menu.repository.MenuRecommendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;
    private final MenuRecommendRepository menuRecommendRepository;


    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;


    /**
     * 회원가입
     */
    @Override
    public Long join(JoinForm form) {
        Member memberEntity = form.toEntity();

        // 닉네임/아이디 중복 회원 검증 로직
        validateDuplicateMember(memberEntity);

        Member savedMember = memberRepository.saveMember(memberEntity);

        // 파일에 이름을 붙일 랜덤으로 식별자 지정 및 프로필 파일 엔티티 생성
        UUID uuid = UUID.randomUUID();
        String storedFileName = uuid + "_user_icon.PNG";

        ProfileFile profileFile = ProfileFile.builder()
                .originalFileName("user_icon.PNG")
                .storedFileName(storedFileName)
                .path(getProfileFilePath(storedFileName))
                .member(memberEntity)
                .build();

        // S3 버킷에 복사 작업
        try {
            // 기존 기본 프로필 사진 객체 가져오기
            S3Object o = amazonS3.getObject(new GetObjectRequest(bucket, "profileFile/user_icon.PNG"));

            // 기본 프로필 사진 복사 객체 생성
            CopyObjectRequest copyObjRequest = new CopyObjectRequest(
                    o.getBucketName(), // 복사 대상 버킷 이름
                    o.getKey(),        // 복사 대상 객체 키
                    bucket,                        // 새로 복사의 버킷 이름
                    "profileFile/" + storedFileName // 새로 복사의 객체 키
            );

            // 복사
            amazonS3.copyObject(copyObjRequest);
        } catch (SdkClientException e) {
            e.printStackTrace();
        }

        // DB에서도 저장
        memberRepository.saveProfile(profileFile);

        return savedMember.getId();
    }

    // 중복 검증 로직
    private void validateDuplicateMember(Member memberEntity) {
        // 중복이 있으면 EXCEPTION
        // 같은 로그인 아이디가 있는지 찾음
        // 반환형태가 Optional<Member>이므로 이렇게 예와처리 가능
        log.info("memberEntityId={}", memberEntity.getId());

        memberRepository.findByName(memberEntity.getName())
                .ifPresent(m -> {
                    throw new DuplicatedMemberNameException("이미 존재하는 닉네임입니다.");
                });

        memberRepository.findByLoginId(memberEntity.getLoginId())
                .ifPresent(m -> {
                    throw new DuplicatedMemberLoginIdException("이미 존재하는 아이디입니다.");
                });
    }

    /**
     * 로그인 기능
     */
    @Override
    public Member login(String loginId, String password) {        // 로그인 실패 시 MemberNotFoundException으로 반환
        // 람다를 활용하여 축약한 것
        return memberRepository.findByLoginId(loginId)
                .filter(m -> m.getPassword().equals(password))
                .orElseThrow(() -> new MemberNotFoundException("아이디 또는 패스워드가 일치하지 않습니다."));
    }


    /**
     * 전체 회원 조회
     */
    @Override
    public List<Member> findAll() {
        return memberRepository.findAll();
    }


    /**
     * 회원 찾기
     */
    @Override
    public Optional<Member> findById(Long memberId) {
        return memberRepository.findById(memberId);
    }


    /**
     * 회원 수정
     */
    @Override
    public Long changeProfile(Long memberId, UpdateForm form) throws IOException {
        Member findMember = memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("회원 조회 실패: " + memberId));

        MultipartFile imageFile = form.getProfileImage();

        // 새로 받아온 프로필 사진이 있으면
        if (imageFile.getOriginalFilename() != null && !imageFile.getOriginalFilename().isBlank()) {
            // 현재 회원의 기존 프로필 사진을 찾고 실제 저장되었던 디렉토리의 프로필 사진을 삭제
            ProfileFile findMemberProfile = memberRepository.findProfileByMember(findMember);
            deleteBeforeProfileFile(findMemberProfile);

            // DB 내용 변경 및 프로필 사진 새로 생성
            findMember.changeProfile(form.getName(), form.getLoginId(), form.getPassword(), form.getIntroduce());
            profileImageUpload(form, findMember);
        } else { // 새로 받은 프로필 사진이 없으면
            findMember.changeProfile(form.getName(), form.getLoginId(), form.getPassword(), form.getIntroduce());
        }

        log.info("회원 프로필 변경");
        return findMember.getId();
    }


    /**
     * 회원 프로필 사진 경로 가져오기
     */
    public String findProfileLocation(Long memberId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("회원 조회 실패: " + memberId));

        ProfileFile findMemberProfile = memberRepository.findProfileByMember(findMember);

        return findMemberProfile.getPath();
    }


    @Override
    public Page<Member> findPageBy(Pageable pageable) {
        return memberRepository.findPageBy(pageable);
    }

    @Override
    public Page<Member> findByNameContaining(String memberSearchKeyword, Pageable pageable) {
        return memberRepository.findByNameContaining(memberSearchKeyword, pageable);
    }


    /**
     * 회원 탈퇴 및 추방 : 현재 회원과 관련된 물리 실제 파일들도 같이 삭제처리를 해야한다!
     */
    @Override
    public void delete(Long memberId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("회원 조회 실패: " + memberId));

        // 현재 회원의 기존 프로필 사진을 찾고 실제 저장되었던 디렉토리의 프로필 사진을 삭제
        ProfileFile findMemberProfile = memberRepository.findProfileByMember(findMember);
        deleteBeforeProfileFile(findMemberProfile);


        // 현재 회원이 작성했던 게시글을 찾고
        List<Board> byMemberBoardList = boardRepository.findByMemberId(memberId);

        // 각 게시글마다 실제 저장되었던 디렉토리의 파일을 삭제
        for (Board board : byMemberBoardList) {
            List<BoardFile> findBoardFiles = boardFileRepository.findByBoard(board);

            // 자유게시판과 추천게시판은 첨부 파일도 같이 넣을 수 있어 따로 첨부파일 + 이미지파일 삭제
            if (board.getBoardType().equals("자유게시판") || board.getBoardType().equals("추천게시판")) {
                for (BoardFile boardFile : findBoardFiles) {
                    if (boardFile.getAttachedType().equals("attached")) {
                        deleteFile("attachFile/" + boardFile.getStoredFileName());
                    } else {
                        deleteFile("imageFile/" + boardFile.getStoredFileName());
                    }
                }
            } else { // 먹스타그램은 이미지 파일만 삭제
                for (BoardFile boardFile : findBoardFiles) {
                    deleteFile("imageFile/" + boardFile.getStoredFileName());
                }
            }
        }


        // 메뉴 랜덤 추천 파일도 삭제 처리
        Page<MenuRecommend> findMenus = menuRecommendRepository.findByMemberId(null, memberId);
        for (MenuRecommend findMenu : findMenus) {
            deleteFile("menuRecommend/" + findMenu.getStoredFileName());
        }


        // DB 삭제처리 : CASCADE 설정으로 회원 엔티티가 삭제되면 연관 매핑된 엔티티는 모두 삭제된다.
        memberRepository.delete(memberId);
    }

    // 이전 파일 삭제 처리 메서드
    private void deleteBeforeProfileFile(ProfileFile findMemberProfile) {
        // 삭제 대상 객체 생성
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, "profileFile/" + findMemberProfile.getStoredFileName());

        // 삭제 처리
        amazonS3.deleteObject(deleteObjectRequest);
    }

    // 프로필 이미지 변경 메서드
    private void profileImageUpload(UpdateForm form, Member member) throws IOException {
        String originalFilename = form.getProfileImage().getOriginalFilename();

        // 파일에 이름을 붙일 랜덤으로 식별자 지정
        UUID uuid = UUID.randomUUID();
        String storedFileName = uuid + "_" + originalFilename;

        // S3 버킷에 넣을 파일 설정 및 넣기
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(form.getProfileImage().getSize());
        metadata.setContentType(form.getProfileImage().getContentType());

        amazonS3.putObject(bucket, "profileFile/"+storedFileName, form.getProfileImage().getInputStream(), metadata);

        // 회원의 기존 프로필 사진에서 교체
        memberRepository.updateProfileImage(originalFilename, storedFileName, getProfileFilePath(storedFileName), member.getId());
    }


    // 회원 프로필 사진 경로 가져오기 메서드
    private String getProfileFilePath(String storedFileName) {
        return amazonS3.getUrl(bucket, "profileFile/" + storedFileName).toString();
    }


    // 게시글 파일 삭제 메서드
    private void deleteFile(String key) {
        // 삭제 대상 객체 생성
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, key);

        // 삭제 처리
        amazonS3.deleteObject(deleteObjectRequest);
    }

    // 회원가입에서의 닉네임 체크 메서드
    public int checkName(String memberName) {
        // 문자열에 빈 공백 검사
        for (int i = 0; i < memberName.length(); i++) {
            char nameChar = memberName.charAt(i);
            if (nameChar == ' ') {
                return 2;
            }
        }

        // 문자열 길이 검사
        int nameLength = memberName.length();
        if (nameLength < 2 || nameLength > 7) {
            return 3;
        }
        else { // 중복 검사
            return memberRepository.checkName(memberName);
        }
    }

    // 회원가입에서의 로그인 아이디 체크 메서드
    public int checkLoginId(String memberLoginId) {
        // 문자열에 빈 공백 검사
        for (int i = 0; i < memberLoginId.length(); i++) {
            char nameChar = memberLoginId.charAt(i);
            if (nameChar == ' ') {
                return 2;
            }
        }

        // 문자열 길이 검사
        int loginIdLength = memberLoginId.length();
        if (loginIdLength < 3 || loginIdLength > 10) {
            return 3;
        }
        else { // 중복 검사
            return memberRepository.checkLoginId(memberLoginId);
        }
    }

    // 회원 수정에서의 닉네임 변경 메서드
    public int updateCheckName(String memberName, Long memberId) {
        // 문자열에 빈 공백 검사
        for (int i = 0; i < memberName.length(); i++) {
            char nameChar = memberName.charAt(i);
            if (nameChar == ' ') {
                return 2;
            }
        }

        // 문자열 길이 검사
        int loginIdLength = memberName.length();
        if (loginIdLength < 3 || loginIdLength > 10) {
            return 3;
        }
        else { // 중복 검사
            return memberRepository.updateCheckName(memberName, memberId);
        }
    }

    // 회원 수정에서의 로그인 아이디 변경 메서드
    public int updateCheckLoginId(String memberLoginId, Long memberId) {
        // 문자열에 빈 공백 검사
        for (int i = 0; i < memberLoginId.length(); i++) {
            char nameChar = memberLoginId.charAt(i);
            if (nameChar == ' ') {
                return 2;
            }
        }

        // 문자열 길이 검사
        int loginIdLength = memberLoginId.length();
        if (loginIdLength < 3 || loginIdLength > 10) {
            return 3;
        }
        else { // 중복 검사
            return memberRepository.updateCheckLoginId(memberLoginId, memberId);
        }
    }


    // 입력한 정보에 빈 공간 확인과 빈 공간인 정보를 가공하여 메시지 출력 메서드
    public String checkSpace(String memberName, String loginId, String password, Model model, String redirectUrl) {
        List<String> totalBlankMessage = new ArrayList<>();
        if (checkSpace(memberName)) {
            totalBlankMessage.add("닉네임");
        }
        if (checkSpace(loginId)) {
            totalBlankMessage.add("아이디");
        }
        if (checkSpace(password)) {
            totalBlankMessage.add("비밀번호");
        }

        if (!totalBlankMessage.isEmpty()) {
            StringBuilder resultBlankMessage = new StringBuilder();

            for (String message : totalBlankMessage) {
                if (resultBlankMessage.length() == 0) {
                    resultBlankMessage.append(message);
                } else {
                    resultBlankMessage.append(", ").append(message);
                }
            }
            resultBlankMessage.append("에 빈 공간이 올 수 없습니다.");

            model.addAttribute("message", resultBlankMessage);
            model.addAttribute("redirectUrl", redirectUrl);
            return "messages";
        }

        return null;
    }

    // 문자열에 빈 공백 있는지 검사 메서드
    public boolean checkSpace(String value) {
        if (value.contains(" ")) {
            return true;
        }

        return false;
    }
}


