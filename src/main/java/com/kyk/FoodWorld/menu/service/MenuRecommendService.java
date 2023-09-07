package com.kyk.FoodWorld.menu.service;


import com.kyk.FoodWorld.admin.dto.AdminMenuRecommendDTO;
import com.kyk.FoodWorld.menu.domain.dto.MenuRecommendUpdateForm;
import com.kyk.FoodWorld.menu.domain.dto.MenuRecommendUploadForm;
import com.kyk.FoodWorld.menu.domain.dto.MenuSearchCond;
import com.kyk.FoodWorld.menu.domain.entity.MenuRecommend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.io.IOException;
import java.util.Optional;

public interface MenuRecommendService {
    void uploadMenu(Long memberId, MenuRecommendUploadForm uploadForm) throws IOException;
    Page<MenuRecommend> PageList(Long memberId, Pageable pageable);
    Optional<MenuRecommend> findById(Long menuRecommendId);
    MenuRecommend randomPick(String selectedCategory);
    void updateMenu(Long menuRecommendId, MenuRecommendUpdateForm updateForm) throws IOException;
    void delete(Long menuRecommendId) throws IOException;
    Page<MenuRecommend> categoryMenuList(MenuSearchCond menuSearchCond, Pageable pageable);
    Long findFirstCursorMenuId(String memberId);
    Slice<AdminMenuRecommendDTO> searchBySlice(Long lastCursorId, Boolean first, Pageable pageable, String memberId);
}
