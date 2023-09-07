package com.kyk.FoodWorld.menu.repository;


import com.kyk.FoodWorld.admin.dto.AdminMenuRecommendDTO;
import com.kyk.FoodWorld.menu.domain.dto.MenuSearchCond;
import com.kyk.FoodWorld.menu.domain.entity.MenuRecommend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

/**
 * QueryDsl을 구현하기 위한 사용자 정의 인터페이스
 */
public interface MenuRecommendRepositoryCustom {
    Page<MenuRecommend> categoryMenuList(MenuSearchCond menuSearchCond, Pageable pageable);
    Long findFirstCursorMenuId(String memberId);
    Slice<AdminMenuRecommendDTO> searchBySlice(Long lastCursorId, Boolean first, Pageable pageable, String memberId);
}
