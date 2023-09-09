package com.kyk.FoodWorld.follow.repository;

import com.kyk.FoodWorld.follow.domain.dto.FollowDto;
import com.kyk.FoodWorld.follow.domain.entity.Follow;
import com.kyk.FoodWorld.member.domain.dto.MemberDto;
import com.kyk.FoodWorld.member.domain.dto.ProfileFileDto;
import com.kyk.FoodWorld.member.domain.entity.Member;
import com.kyk.FoodWorld.member.domain.entity.QMember;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.kyk.FoodWorld.follow.domain.entity.QFollow.follow;

@Slf4j
public class FollowRepositoryCustomImpl implements FollowRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public FollowRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    /**
     * 회원을 팔로우한 회원들로 최신 팔로우 id 가져오기
     */
    @Override
    public Long findFirstCursorFollowerId(Member member) {
        Follow findFollower = queryFactory.selectFrom(follow)
                .where(
                       follow.toMember.eq(member)
                )
                .limit(1)
                .orderBy(follow.id.desc())
                .fetchOne();

        if (findFollower != null) {
            return findFollower.getId();
        } else {
            return 0L;
        }
    }

    /**
     * 팔로우한 회원들만 페이징
     */
    @Override
    public Slice<FollowDto> searchBySlice(Member member, Long lastCursorFollowerId, Boolean first, Pageable pageable) {

        List<Follow> results = queryFactory.selectFrom(follow)
                .leftJoin(follow.fromMember).fetchJoin()
                .leftJoin(follow.toMember).fetchJoin()
                .where(
                        ltFollowerId(lastCursorFollowerId, first),
                        follow.toMember.eq(member)
                )
                .limit(pageable.getPageSize() + 1)
                .orderBy(follow.id.desc())
                .fetch();

        List<FollowDto> followDtos = results.stream()
                        .map(m -> new FollowDto(m.getId(),
                                new MemberDto(m.getFromMember().getId(), m.getFromMember().getName(), m.getFromMember().getLoginId(), m.getFromMember().getIntroduce(), m.getFromMember().getFollowCount(), m.getFromMember().getFollowingCount(), m.getFromMember().getRole(),
                                        new ProfileFileDto(m.getFromMember().getProfileFile().getPath())),
                                new MemberDto(m.getFromMember().getId(), m.getFromMember().getName(), m.getFromMember().getLoginId(), m.getFromMember().getIntroduce(), m.getFromMember().getFollowCount(), m.getFromMember().getFollowingCount(), m.getFromMember().getRole(),
                                        new ProfileFileDto(m.getFromMember().getProfileFile().getPath()))))
                                .collect(Collectors.toList());


        log.info("팔로워 리스트 ={}", results.size());
        return checkLastPage(pageable, followDtos);
    }

    // BooleanExpression으로 하면 조합 가능해진다.
    // no-offset 방식 처리하는 메서드
    private BooleanExpression ltFollowerId(Long lastCursorFollowerId, Boolean first) {
        if (lastCursorFollowerId == null) {
            return null;
        }
        else if (first) {
            return follow.id.loe(lastCursorFollowerId);
        }
        else {
            return follow.id.lt(lastCursorFollowerId);
        }
    }

    // 무한 스크롤 방식 처리하는 메서드
    private Slice<FollowDto> checkLastPage(Pageable pageable, List<FollowDto> results) {

        boolean hasNext = false;

        // 조회한 결과 개수가 요청한 페이지 사이즈보다 크면 뒤에 더 있음, next = true
        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }


    /**
     * 현재 회원이 팔로우한 회원을 팔로우한 회원들과
     * 현재 회원이 팔로잉한 회원을 팔로우한 회원들을 랜덤으로 리스트로 담아 반환
     */
    public List<Member> recommendMember(Long currentMemberId) {
        Random rand = new Random();
        List<Long> noMemberList = new ArrayList<>();                    // 랜덤 선택한 후 중복 방지를 위한 제외명단
        List<Member> addFollowerList = new ArrayList<>();               // 현재 회원을 팔로우한 회원들과 현재 회원이 팔로잉한 회원들을 합한 리스트
        List<Member> resultFollowerList = new ArrayList<>();            // 최종 팔로우 추천 리스트

        int followerTotalCount = 0;                                     // 현재 회원의 최종 팔로워들의 팔로워 개수
        int listCount = 0;                                              // 반복 카운트


        QMember follower = new QMember("follower");              // 각 필요한 필드의 유형에 따른 선언
        QMember following = new QMember("following");
        QMember currentMember = new QMember("currentMember");


        // 현재 회원을 팔로우한 회원들의 리스트
        List<Member> followerList = queryFactory
                .select(follower)
                .from(follow)
                .join(follow.fromMember, follower)
                .join(follow.toMember, currentMember)
                .where(
                        follow.toMember.id.eq(currentMemberId)
                )
                .fetch();
        // 현재 회원이 팔로잉한 회원들의 리스트
        List<Member> followingList = queryFactory
                .select(following)
                .from(follow)
                .join(follow.fromMember, currentMember)
                .join(follow.toMember, following)
                .where(
                        follow.fromMember.id.eq(currentMemberId)
                )
                .fetch();

        // 하나의 리스트로 합한다. addAll은 중복된 요소는 제외된다.
        addFollowerList.addAll(followerList);
        addFollowerList.addAll(followingList);


        // 현재 회원이 팔로우한 회원을 팔로우한 회원들과 현재 회원이 팔로잉한 회원을 팔로우한 회원들의 합산 수
        for (Member member : addFollowerList) {
            Long followerOfFollowerCount = queryFactory
                    .selectDistinct(follower.id.count())
                    .from(follow)
                    .join(follow.fromMember, follower)
                    .join(follow.toMember, currentMember)
                    .where(
                            follower.id.ne(currentMemberId),      // 본인인 현재 회원이 나오지 않게 방지 조건
                            currentMember.id.ne(currentMemberId), // 본인인 현재 회원이 나오지 않게 방지 조건
                            currentMember.id.eq(member.getId())   // 해당 회원을 팔로우한 팔로워만 나오게 하는 조건
                    )
                    .fetchCount();

            log.info("followerOfFollowerCount = {}", followerOfFollowerCount);
            followerTotalCount += followerOfFollowerCount;
            log.info("followerTotalCount = {}", followerTotalCount);
        }

        // 합한 팔로우 리스트가 하나이상 존재하고 위 카운트 쿼리로 회원의 현재 연관된 팔로워를 가져올 수 있는 합산수인 followerTotalCount가 15개보다 크면
        if (addFollowerList.size() > 0 && followerTotalCount >= 15) {
            // followerTotalCount=15개만 리스트에 담는다.
            followerOfFollowerList(currentMemberId, rand, noMemberList, resultFollowerList, listCount, 15, follower, currentMember, addFollowerList, followingList);
        }
        else { // followerTotalCount가 15개보다 작으면
            // 회원의 현재 연관된 팔로워를 가져올 수 있는 합산수(followerTotalCount)만큼만 리스트에 담는다.
            followerOfFollowerList(currentMemberId, rand, noMemberList, resultFollowerList, listCount, followerTotalCount, follower, currentMember, addFollowerList, followingList);
        }

        return resultFollowerList;
    }

    // 연관된 팔로워들을 불러오는 메소드
    private void followerOfFollowerList(Long currentMemberId, Random rand, List<Long> noMemberList, List<Member> resultFollowerList, int listCount, int followerTotalCount, QMember follower, QMember currentMember, List<Member> followerList,  List<Member> followingList) {
        while (listCount < followerTotalCount) { // followerTotalCount 만큼 담을 때까지 반복
            int FollowerIndex = rand.nextInt(followerList.size());   // 랜덤으로 합산한 팔로우 리스트에서 하나 선택
            Member FollowerMember = followerList.get(FollowerIndex);
            log.info("팔로워 이름 = {}", FollowerMember.getName());

            List<Member> followerOfFollowerList = queryFactory     // 랜덤으로 선택한 회원의 팔로워 리스트 조회
                    .select(follower)
                    .from(follow)
                    .join(follow.fromMember, follower)
                    .join(follow.toMember, currentMember)
                    .where(
                            follower.id.ne(currentMemberId),            // 본인인 현재 회원이 나오지 않게 방지 조건
                            follower.id.notIn(noMemberList),            // 중복방지를 위한 제외대상 리스트에 포함되지 않게 조건
                            follower.notIn(followingList),              // 본인인 현재 회원이 이미 팔로우한 회원은 포함되지 않게 조건
                            currentMember.id.ne(currentMemberId),       // 본인인 현재 회원이 나오지 않게 방지 조건
                            currentMember.id.eq(FollowerMember.getId()) // 해당 회원을 팔로우한 팔로워만 나오게 하는 조건
                    )
                    .fetch();

            if (followerOfFollowerList.size() > 0) { // 연관된 팔로워가 있으면
                // 랜덤으로 선택한 회원의 팔로워 리스트에서 1명을 랜덤으로 또 뽑아서
                int followerOfFollowerIndex = rand.nextInt(followerOfFollowerList.size());
                Member FollowerOfFollowerMember = followerOfFollowerList.get(followerOfFollowerIndex);
                log.info("팔로워의 팔로워 이름 = {}", FollowerOfFollowerMember.getName());

                noMemberList.add(FollowerOfFollowerMember.getId()); // 이번에 뽑았으니 중복방지를 위한 제외 대상 리스트에 추가하고
                resultFollowerList.add(FollowerOfFollowerMember);   // 최종 반환할 팔로워 리스트에 담는다.
            }
            listCount++;
        }
    }

}
