package smu.capstone.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.capstone.domain.board.entity.Board;
import smu.capstone.domain.comment.entity.Comment;

import java.util.List;

// todo: 게시글 작성하면 댓글 cacade 만들어야됨
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardId(Long id); // 특정 게시글의 댓글 조회
    List<Comment> findAllByUserId(Long id); //특정 유저의 댓글 조회
    void deleteByBoard(Board board);

}

