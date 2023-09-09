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

    /**
     * 윈도우 환경과 달리 리눅스 환경의 MySQL에서는 대소문자 구분을 함
     */
    @Query(value = "SELECT MENU_RECOMMEND_ID FROM menu_recommend " +
                    "WHERE CATEGORY = :selectedCategory " +
                    "ORDER BY RAND() " +
                    "LIMIT 1", nativeQuery = true)
    Long findLastIdWithCategory(String selectedCategory);

}
