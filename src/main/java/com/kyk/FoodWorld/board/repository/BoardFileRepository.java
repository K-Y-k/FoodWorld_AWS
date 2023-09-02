package com.kyk.FoodWorld.board.repository;


import com.kyk.FoodWorld.board.domain.entity.Board;
import com.kyk.FoodWorld.board.domain.entity.BoardFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 파일 관련 스프링 데이터 JPA 리포지토리
 */
public interface BoardFileRepository extends JpaRepository<BoardFile, Long> {
    List<BoardFile> findByBoard(Board board);
}
