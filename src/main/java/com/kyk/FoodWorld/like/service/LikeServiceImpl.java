package com.kyk.FoodWorld.like.service;


import com.kyk.FoodWorld.board.domain.entity.Board;
import com.kyk.FoodWorld.board.repository.BoardRepository;
import com.kyk.FoodWorld.like.domain.dto.LikeDto;
import com.kyk.FoodWorld.like.domain.entity.Like;
import com.kyk.FoodWorld.like.repository.LikeRepository;
import com.kyk.FoodWorld.member.domain.entity.Member;
import com.kyk.FoodWorld.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LikeServiceImpl implements LikeService{
    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;


    @Override
    public int saveLike(Long memberId, Long boardId) {
        Optional<Like> findLike = likeRepository.findByMember_IdAndBoard_Id(memberId, boardId);

        if (findLike.isPresent()){
            likeRepository.delete(findLike.get().getId());
            return 0;
        } else {
            Member memberEntity = memberRepository.findById(memberId).orElseThrow(() ->
                    new IllegalArgumentException("회원 조회 실패: " + memberId));
            Board boardEntity = boardRepository.findById(boardId).orElseThrow(() ->
                    new IllegalArgumentException("글 조회 실패: " + boardId));

            Like likeEntity = LikeDto.toEntity(memberEntity, boardEntity);
            likeRepository.save(likeEntity);

            return 1;
        }
    }

    @Override
    public int countByBoard_Id(Long boardId) {
        return likeRepository.countByBoard_Id(boardId);
    }

    public void deleteByBoard_Id(Long boardId) {
        likeRepository.deleteByBoardId(boardId);
    }

}
