package smu.capstone.domain.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.capstone.domain.board.entity.Board;
import smu.capstone.domain.like.entity.Like;
import smu.capstone.domain.member.entity.UserEntity;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    // 현재 유저가 특정 게시글에 좋아요를 눌렀는지 확인
    boolean existsByUserAndBoard(UserEntity user, Board board);

    // 특정 유저가 특정 게시글의 좋아요 정보를 가져옴
    Optional<Like> findByUserAndBoard(UserEntity user, Board board);

    // 특정 유저가 특정 게시글에 좋아요를 눌렀다면 해당 엔티티 삭제
    void deleteByUserAndBoard(UserEntity user, Board board);


}


