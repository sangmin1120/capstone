package smu.capstone.object.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.capstone.object.comment.domain.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardId(Long id); // 특정 게시글의 댓글 조회
    List<Comment> findAllByUserId(Long id); //특정 유저의 댓글 조회
}

