package smu.capstone.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.capstone.domain.comment.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardId(Long id); // 특정 게시글의 댓글 조회
    List<Comment> findAllByUserId(Long id); //특정 유저의 댓글 조회
}

