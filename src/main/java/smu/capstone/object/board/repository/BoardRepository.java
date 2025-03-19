package smu.capstone.object.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.capstone.object.board.domain.Board;
import smu.capstone.object.board.domain.BoardType;


import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByBoardType(BoardType boardType);

    //대소문자 무시 추가?
    List<Board> findByTitleContainingOrContentContaining(String keyword, String keyword1);

    Optional<Board> findById(Long boardId);
}
