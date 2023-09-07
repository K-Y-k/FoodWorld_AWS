package com.kyk.FoodWorld.menu.repository;

import com.kyk.FoodWorld.admin.dto.AdminMenuRecommendDTO;
import com.kyk.FoodWorld.menu.domain.dto.MenuSearchCond;
import com.kyk.FoodWorld.menu.domain.entity.MenuRecommend;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static com.kyk.FoodWorld.member.domain.entity.QMember.member;
import static com.kyk.FoodWorld.menu.domain.entity.QMenuRecommend.menuRecommend;
import static org.springframework.util.StringUtils.hasText;

/**
 * 사용자 정의 인터페이스를 상속받은 사용자 정의 리포지토리
 */
@Slf4j
public class MenuRecommendRepositoryCustomImpl implements MenuRecommendRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    public MenuRecommendRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em); // 이렇게 JPAQueryFactory를 사용할 수는 있다.
    }

    @Override
    public Page<MenuRecommend> categoryMenuList(MenuSearchCond menuSearchCond, Pageable pageable) {
        QueryResults<MenuRecommend> results = queryFactory.selectFrom(menuRecommend)
                .leftJoin(menuRecommend.member, member)
                .where(
                        menuCategoryEq(menuSearchCond.getSelectedCategory()),
                        menuNameEq(menuSearchCond.getMenuNameKeyword()),
                        franchisesEq(menuSearchCond.getFranchisesKeyword())
                )
                .orderBy(menuRecommend.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MenuRecommend> content = results.getResults();   // .getResults()는 조회한 데이터를 가져옴
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);      // PageImpl<>로 반환해야한다.
    }

    private BooleanExpression menuCategoryEq(String selectedCategory) {
        if ("카테고리 전체".equals(selectedCategory)) {
            return null;
        } else {
            return hasText(selectedCategory) ? menuRecommend.category.eq(selectedCategory) : null;
        }
    }
    private BooleanExpression menuNameEq(String getMenuNameKeyword) {
        return hasText(getMenuNameKeyword) ? menuRecommend.menuName.like("%" + getMenuNameKeyword + "%"): null;
    }
    private BooleanExpression franchisesEq(String getFranchisesKeyword) {
        return hasText(getFranchisesKeyword) ? menuRecommend.franchises.like("%" + getFranchisesKeyword + "%") : null;
    }


    /**
     * 처음에는 최근 Id로 직접 받아오기 위해 필요한 메서드
     */
    @Override
    public Long findFirstCursorMenuId(String memberId) {
        MenuRecommend findMenuRecommend = queryFactory.selectFrom(menuRecommend)
                .where(
                        menuRecommend.member.id.eq(Long.valueOf(memberId))
                )
                .limit(1)
                .orderBy(menuRecommend.id.desc())
                .fetchOne();

        if (findMenuRecommend != null) {
            return findMenuRecommend.getId();
        }
        else {
            return 0L;
        }
    }

    /**
     * 크기를 제한한 리스트와 다음 페이지의 여부를 같이 반환하는 Slice 페이징
     */
    @Override
    public Slice<AdminMenuRecommendDTO> searchBySlice(Long lastCursorId, Boolean first, Pageable pageable, String memberId) {

        List<MenuRecommend> menuRecommends = queryFactory.selectFrom(menuRecommend)
                .leftJoin(menuRecommend.member, member).fetchJoin()
                .where(
                        ltMenuId(lastCursorId, first),
                        memberIdEq(memberId)
                )
                .limit(pageable.getPageSize() + 1)
                .orderBy(menuRecommend.id.desc())
                .fetch();
        log.info("리스트 개수={}", menuRecommends.size());

        List<AdminMenuRecommendDTO> menuRecommendDTOList = menuRecommends.stream()
                .map(m -> new AdminMenuRecommendDTO(m.getId(), m.getCategory(), m.getFranchises(), m.getMenuName(), m.getCreatedDate()))
                .collect(Collectors.toList());

        return checkLastPage(pageable, menuRecommendDTOList);
    }
    // BooleanExpression으로 하면 조합 가능해진다.
    private BooleanExpression memberIdEq(String memberId) {
        return hasText(memberId) ? menuRecommend.member.id.eq(Long.valueOf(memberId)) : null;
    }
    // no-offset 방식 처리하는 메서드
    private BooleanExpression ltMenuId(Long lastCursorId, Boolean first) {
        if (lastCursorId == null) {
            return null;
        }
        else if (first) {
            return menuRecommend.id.loe(lastCursorId);
        }
        else {
            return menuRecommend.id.lt(lastCursorId);
        }
    }

    // 무한 스크롤 방식 처리하는 메서드
    private Slice<AdminMenuRecommendDTO> checkLastPage(Pageable pageable, List<AdminMenuRecommendDTO> results) {

        boolean hasNext = false;

        // 조회한 결과 개수가 요청한 페이지 사이즈보다 크면 뒤에 더 있음, next = true
        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }
}
