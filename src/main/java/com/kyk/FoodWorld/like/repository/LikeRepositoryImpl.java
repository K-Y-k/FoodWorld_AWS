package com.kyk.FoodWorld.like.repository;

import com.kyk.FoodWorld.like.domain.entity.Like;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository{

    private final JPALikeRepository likeRepository;
    
    @Override
    public Like save(Like like) {
        return likeRepository.save(like);
    }

    @Override
    public Optional<Like> findByMember_IdAndBoard_Id(Long memberId, Long boardId) {
        return likeRepository.findByMember_IdAndBoard_Id(memberId, boardId);
    }

    @Override
    public int countByBoard_Id(Long boardId) {
        return likeRepository.countByBoard_Id(boardId);
    }

    @Override
    public void delete(Long likeId) {
        likeRepository.deleteById(likeId);
    }

    @Override
    public void deleteByBoardId(Long boardId) {
        likeRepository.deleteByBoard_Id(boardId);
    }

}
