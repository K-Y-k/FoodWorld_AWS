package com.kyk.FoodWorld.menu.repository;


import com.kyk.FoodWorld.menu.domain.entity.MenuRecommend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JPAMenuRecommendRepository extends JpaRepository<MenuRecommend, Long>, MenuRecommendRepositoryCustom {

    Page<MenuRecommend> findPageListByMemberId(Pageable pageable, Long memberId);

    @Query("select max(m.id) from MenuRecommend m")
    Long findLastId();

    @Query(value = "SELECT MENU_RECOMMEND_ID FROM MENU_RECOMMEND " +
                    "WHERE CATEGORY = :selectedCategory " +
                    "ORDER BY RAND() " +
                    "LIMIT 1", nativeQuery = true)
    Long findLastIdWithCategory(String selectedCategory);

}
