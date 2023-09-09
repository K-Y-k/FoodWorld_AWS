package com.kyk.FoodWorld.menu.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kyk.FoodWorld.member.domain.entity.Member;
import com.kyk.FoodWorld.menu.domain.dto.MenuRecommendUpdateForm;
import com.kyk.FoodWorld.web.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "menu_recommend")
public class MenuRecommend extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menuRecommend_id")
    private Long id;

    private String category;

    private String franchises;

    private String menuName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "member_id")
    private Member member;

    private String originalFileName;

    private String storedFileName;

    private String path;


    public MenuRecommend(String category, String franchises, String menuName, Member member, String originalFileName, String storedFileName, String path) {
        this.category = category;
        this.franchises = franchises;
        this.menuName = menuName;
        this.member = member;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.path = path;
    }


    public void updateMenu(MenuRecommendUpdateForm updateForm) {
        this.category = updateForm.getCategory();
        this.franchises = updateForm.getFranchises();
        this.menuName = updateForm.getMenuName();
    }

    public void updateMenuNewFile(MenuRecommendUpdateForm updateForm, String originalFileName, String storedFileName, String path) {
        this.category = updateForm.getCategory();
        this.franchises = updateForm.getFranchises();
        this.menuName = updateForm.getMenuName();
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.path = path;
    }
}
