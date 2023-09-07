package com.kyk.FoodWorld.board.repository;

import com.kyk.FoodWorld.board.domain.dto.BoardSearchCond;
import com.kyk.FoodWorld.board.domain.entity.Board;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;


import static com.kyk.FoodWorld.board.domain.entity.QBoard.board;
import static com.kyk.FoodWorld.board.domain.entity.QBoardFile.boardFile;
import static com.kyk.FoodWorld.member.domain.entity.QMember.member;
import static org.springframework.util.StringUtils.hasText;

/**
 * 사용자 정의 인터페이스를 상속받은 사용자 정의 리포지토리
 */
@Slf4j
public class BoardRepositoryCustomImpl implements BoardRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    public BoardRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em); // 이렇게 JPAQueryFactory를 사용할 수는 있다.
    }

    public List<Board> findByMemberId(Long memberId) {
        List<Board> findMemberBoardList = queryFactory.selectFrom(board)
                .leftJoin(board.boardFiles, boardFile)
                .where(
                        board.member.id.eq(memberId)
                )
                .fetch();

        return findMemberBoardList;
    }

    @Override
    public Long findFirstCursorBoardId(String boardType) {
        Board findBoard = queryFactory.selectFrom(board)
                .where(
                        board.boardType.eq(boardType)
                )
                .limit(1)
                .orderBy(board.id.desc())
                .fetchOne();

        if (findBoard != null) {
            return findBoard.getId();
        } else {
            return 0L;
        }
    }

    @Override
    public Long findFirstCursorBoardIdInMember(Long memberId, String boardType) {
        Board findBoard = queryFactory.selectFrom(board)
                .where(
                        board.member.id.eq(memberId),
                        boardTypeEq(boardType)
                )
                .limit(1)
                .orderBy(board.id.desc())
                .fetchOne();

        if (findBoard != null) {
            return findBoard.getId();
        } else {
            return 0L;
        }
    }


    @Override
    public Slice<Board> searchBySlice(String memberId, Long lastCursorBoardId, Boolean first, Pageable pageable, String boardType) {

        List<Board> results = queryFactory.selectFrom(board)
                .leftJoin(board.member, member).fetchJoin()
                .leftJoin(board.boardFiles, boardFile).fetchJoin()
                .where(
                        ltBoardId(lastCursorBoardId, first),
                        memberIdEq(memberId),
                        boardTypeEq(boardType)
                )
                .limit(pageable.getPageSize() + 1)
                .orderBy(board.id.desc())
                .fetch();

        log.info("리스트 개수={}", results.size());

        return checkLastPage(pageable, results);
    }

    @Override
    public Slice<Board> searchBySliceByWriter(String memberId, Long lastCursorBoardId, Boolean first, String writerSearchKeyword, Pageable pageable, String boardType) {

        List<Board> results = queryFactory.selectFrom(board)
                .leftJoin(board.member, member).fetchJoin()
                .leftJoin(board.boardFiles, boardFile).fetchJoin()
                .where(
                        ltBoardId(lastCursorBoardId, first),
                        memberIdEq(memberId),
                        boardTypeEq(boardType),
                        boardWriterEq(writerSearchKeyword)
                )
                .limit(pageable.getPageSize() + 1)
                .orderBy(board.id.desc())
                .fetch();

        log.info("리스트 개수={}", results.size());

        return checkLastPage(pageable, results);
    }

    // BooleanExpression으로 하면 조합 가능해진다.
    private BooleanExpression memberIdEq(String memberId) {
        return hasText(memberId) ? board.member.id.eq(Long.valueOf(memberId)) : null;
    }
    private BooleanExpression boardTypeEq(String boardType) {
        return hasText(boardType) ? board.boardType.eq(boardType) : null;
    }

    // no-offset 방식 처리하는 메서드
    private BooleanExpression ltBoardId(Long lastCursorBoardId, Boolean first) {
        if (lastCursorBoardId == null) {
            return null;
        }
        else if (first) {
            return board.id.loe(lastCursorBoardId);
        }
        else {
            return board.id.lt(lastCursorBoardId);
        }
    }

    // 무한 스크롤 방식 처리하는 메서드
    private Slice<Board> checkLastPage(Pageable pageable, List<Board> results) {

        boolean hasNext = false;

        // 조회한 결과 개수가 요청한 페이지 사이즈보다 크면 뒤에 더 있음, next = true
        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }



    @Override
    public List<Board> popularBoardList(String boardType) {
        int limit = 7;
        if (boardType.equals("먹스타그램")) {
            limit = 4;
        }

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime startToday = today.toLocalDate().atStartOfDay();
        LocalDateTime endToday = today.toLocalDate().atTime(23, 59, 59);

        log.info("오늘의 시작 시간 = {}", today.toLocalDate().atStartOfDay());
        log.info("오늘의 마지막 시간 = {}", today.toLocalDate().atTime(23, 59, 59));


        return queryFactory.selectFrom(board)
                .where(
                        board.boardType.eq(boardType),
                        board.createdDate.between(startToday, endToday)
                )
                .orderBy(board.count.multiply(7).add(board.likeCount.multiply(3)).desc(),
                        board.createdDate.desc(),
                        board.id.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public Page<Board> categoryBoardList(String boardType, BoardSearchCond boardSearchDto, Pageable pageable) {
        QueryResults<Board> results = queryFactory.selectFrom(board)
                .leftJoin(board.member, member)
                .where(
                        board.boardType.eq(boardType),
                        boardWriterEq(boardSearchDto.getWriterSearchKeyword()),
                        boardTitleEq(boardSearchDto.getTitleSearchKeyword()),

                        boardSubTypeEq(boardSearchDto.getSelectedCategory()),
                        boardAreaEq(boardSearchDto.getSelectedArea()),
                        boardMenuNameEq(boardSearchDto.getSelectedMenu())
                )
                .orderBy(board.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Board> content = results.getResults();     // .getResults()는 조회한 데이터를 가져옴
        long total = results.getTotal();                 // 총 개수

        return new PageImpl<>(content, pageable, total); // PageImpl<>로 반환해야한다.
    }

    private BooleanExpression boardSubTypeEq(String selectedCategory) {
        return hasText(selectedCategory) ? board.subType.eq(selectedCategory) : null;
    }
    private BooleanExpression boardAreaEq(String selectedArea) {
        return hasText(selectedArea) ? board.area.like("%" + selectedArea + "%"): null;
    }
    private BooleanExpression boardMenuNameEq(String selectedMenu) {
        return hasText(selectedMenu) ? board.menuName.eq(selectedMenu) : null;
    }
    private BooleanExpression boardWriterEq(String searchWriterKeyword) {
        return hasText(searchWriterKeyword) ? board.writer.like("%" + searchWriterKeyword + "%") : null;
    }
    private BooleanExpression boardTitleEq(String searchTitleKeyword) {
        return hasText(searchTitleKeyword) ? board.title.like("%" + searchTitleKeyword + "%")  : null;
    }

}
