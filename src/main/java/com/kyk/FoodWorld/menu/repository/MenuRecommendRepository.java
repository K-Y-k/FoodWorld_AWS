package com.kyk.FoodWorld.menu.repository;


import com.kyk.FoodWorld.admin.dto.AdminMenuRecommendDTO;
import com.kyk.FoodWorld.menu.domain.dto.MenuSearchCond;
import com.kyk.FoodWorld.menu.domain.entity.MenuRecommend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface MenuRecommendRepository {

    MenuRecommend save(MenuRecommend menuRecommend);

    Optional<MenuRecommend> findById(Long menuRecommendId);

    Page<MenuRecommend> findByMemberId(Pageable pageable, Long memberId);

    Long findLastId();
    Long findLastIdWithCategory(String selectedCategory);

    void delete(Long boardId);

    Page<MenuRecommend> categoryMenuList(MenuSearchCond menuSearchCond, Pageable pageable);

    Long findFirstCursorMenuId(String memberId);
    Slice<AdminMenuRecommendDTO> searchBySlice(Long lastCursorId, Boolean first, Pageable pageable, String memberId);

}
