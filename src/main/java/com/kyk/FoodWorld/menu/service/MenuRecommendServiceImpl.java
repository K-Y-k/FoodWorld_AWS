package com.kyk.FoodWorld.menu.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.kyk.FoodWorld.admin.dto.AdminMenuRecommendDTO;
import com.kyk.FoodWorld.member.domain.entity.Member;
import com.kyk.FoodWorld.member.repository.MemberRepository;
import com.kyk.FoodWorld.menu.domain.dto.MenuRecommendUpdateForm;
import com.kyk.FoodWorld.menu.domain.dto.MenuRecommendUploadForm;
import com.kyk.FoodWorld.menu.domain.dto.MenuSearchCond;
import com.kyk.FoodWorld.menu.domain.entity.MenuRecommend;
import com.kyk.FoodWorld.menu.repository.MenuRecommendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class MenuRecommendServiceImpl implements MenuRecommendService{
    private final MenuRecommendRepository menuRecommendRepository;
    private final MemberRepository memberRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;


    @Override
    public void uploadMenu(Long memberId, MenuRecommendUploadForm uploadForm) throws IOException {
        Member member = memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("회원 조회 실패: " + memberId));

        MultipartFile imageFile = uploadForm.getImageFile();

        if (imageFile.getOriginalFilename() != null && !imageFile.getOriginalFilename().isBlank()) {
            imageUpload(member, uploadForm);
        } else {
            throw new IllegalArgumentException("메뉴 사진을 선택해주세요.");
        }
    }

    private void imageUpload(Member member, MenuRecommendUploadForm uploadForm) throws IOException {
        String originalFileName = uploadForm.getImageFile().getOriginalFilename();

        // 파일에 이름을 붙일 랜덤으로 식별자 지정
        UUID uuid = UUID.randomUUID();
        String storedFileName = uuid + "_" + originalFileName;

        // S3 버킷에 넣을 파일 설정 및 넣기
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(uploadForm.getImageFile().getSize());
        metadata.setContentType(uploadForm.getImageFile().getContentType());

        amazonS3.putObject(bucket, "menuRecommend/"+storedFileName, uploadForm.getImageFile().getInputStream(), metadata);

        // 파일 불러오기 경로
        String path = amazonS3.getUrl(bucket, "menuRecommend/"+storedFileName).toString();

        // DB에 파일 관련 필드 값 저장
        MenuRecommend menuRecommendEntity = uploadForm.toSaveEntity(member, uploadForm, storedFileName, path);
        menuRecommendRepository.save(menuRecommendEntity);
    }


    @Override
    public Page<MenuRecommend> PageList(Long memberId, Pageable pageable) {
        return menuRecommendRepository.findByMemberId(pageable, memberId);
    }

    @Override
    public Optional<MenuRecommend> findById(Long menuRecommendId) {
        return menuRecommendRepository.findById(menuRecommendId);
    }


    @Override
    public MenuRecommend randomPick(String selectedCategory) {
        if (selectedCategory == null || selectedCategory.equals("전체")) {
            Random random = new Random();
            Long lastId = menuRecommendRepository.findLastId();
            int randomId = random.nextInt(lastId.intValue()) + 1; // 랜덤 함수는 0~넣은값-1까지 랜덤으로 가져오므로 1~넣은값으로 가져오고 싶어 +1

            return menuRecommendRepository.findById((long) randomId).orElseThrow(() ->
                    new IllegalArgumentException("메뉴 가져오기 실패: 메뉴룰 찾지 못했습니다." + randomId));
        }
        else {
            Long findRandomId = menuRecommendRepository.findLastIdWithCategory(selectedCategory);

            return menuRecommendRepository.findById(findRandomId).orElseThrow(() ->
                    new IllegalArgumentException("메뉴 가져오기 실패: 메뉴룰 찾지 못했습니다." + findRandomId));
        }
    }


    @Override
    public void updateMenu(Long menuRecommendId, MenuRecommendUpdateForm updateForm) throws IOException {
        MenuRecommend menuRecommend = menuRecommendRepository.findById(menuRecommendId).orElseThrow(() ->
                new IllegalArgumentException("메뉴 조회 실패: " + menuRecommendId));

        MultipartFile imageFile = updateForm.getImageFile();

        if (imageFile.getOriginalFilename() != null && !imageFile.getOriginalFilename().isBlank()) {
            // 기존 저장되었던 이미지 파일 삭제 작업
            // 삭제 대상 객체 생성
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, "menuRecommend/" + menuRecommend.getStoredFileName());

            // 삭제 처리
            amazonS3.deleteObject(deleteObjectRequest);


            // 이미지 파일 다시 새로 등록 작업
            String originalFileName = imageFile.getOriginalFilename();

            // 파일에 이름을 붙일 랜덤으로 식별자 지정
            UUID uuid = UUID.randomUUID();
            String storedFileName = uuid + "_" + originalFileName;

            // S3 버킷에 넣을 파일 설정 및 넣기
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(imageFile.getSize());
            metadata.setContentType(imageFile.getContentType());

            amazonS3.putObject(bucket, "menuRecommend/"+storedFileName, imageFile.getInputStream(), metadata);

            // 파일 불러오기 경로
            String path = amazonS3.getUrl(bucket, "menuRecommend/"+storedFileName).toString();

            // 변경 감지로 DB 값 수정
            menuRecommend.updateMenuNewFile(updateForm, originalFileName, storedFileName, path);

        } else {
            menuRecommend.updateMenu(updateForm);
        }
    }

    @Override
    public void delete(Long menuRecommendId) throws IOException {
        MenuRecommend menuRecommend = menuRecommendRepository.findById(menuRecommendId).orElseThrow(() ->
                new IllegalArgumentException("메뉴 가져오기 실패: 메뉴룰 찾지 못했습니다." + menuRecommendId));

        // 삭제 대상 객체 생성
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, "menuRecommend/" + menuRecommend.getStoredFileName());

        // 삭제 처리
        amazonS3.deleteObject(deleteObjectRequest);

        menuRecommendRepository.delete(menuRecommendId);
    }

    @Override
    public Page<MenuRecommend> categoryMenuList(MenuSearchCond menuSearchCond, Pageable pageable) {
       return menuRecommendRepository.categoryMenuList(menuSearchCond, pageable);
    }

    @Override
    public Long findFirstCursorMenuId(String memberId) {
        return menuRecommendRepository.findFirstCursorMenuId(memberId);
    }

    @Override
    public Slice<AdminMenuRecommendDTO> searchBySlice(Long lastCursorId, Boolean first, Pageable pageable, String memberId) {
        return menuRecommendRepository.searchBySlice(lastCursorId, first, pageable, memberId);
    }
    
    public String getBinPath() {
        // 기본 파일 불러오기 경로
        return amazonS3.getUrl(bucket, "menuRecommend/bin.JPG").toString();
    }
}
