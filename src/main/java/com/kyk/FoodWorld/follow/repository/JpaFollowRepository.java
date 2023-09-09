package com.kyk.FoodWorld.follow.repository;

import com.kyk.FoodWorld.follow.domain.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JpaFollowRepository extends JpaRepository<Follow, Long>, FollowRepositoryCustom {

	/**
	 * 현재 회원이 상대 회원을 팔로우한 상태인지 확인
	 */
	Optional<Follow> findByFromMember_IdAndToMember_Id(Long memberId, Long boardId);

	/**
	 * 팔로워 수 조회
	 */
	int countByToMember_Id(Long toMemberId);


	/**
	 * 팔로잉 수 조회
	 */
	int countByFromMember_Id(Long fromMemberId);


	/**
	 * 팔로우 해제
	 */
	void deleteByFromMember_IdAndToMember_Id(Long fromMemberId, Long toMemberId);

}
