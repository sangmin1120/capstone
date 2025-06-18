package smu.capstone.domain.board.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.domain.board.entity.Board;
import smu.capstone.domain.board.event.BoardImageEvent;
import smu.capstone.domain.board.entity.BoardType;
import smu.capstone.domain.board.repository.BoardRepository;
import smu.capstone.domain.board.dto.BoardRequestDto;
import smu.capstone.domain.file.service.S3Service;
import smu.capstone.domain.comment.repository.CommentRepository;
import smu.capstone.domain.like.repository.LikeRepository;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.service.InfoService;

import java.util.Objects;

import static smu.capstone.common.errorcode.CommonStatusCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardModifyService {

    private final BoardRepository boardRepository;
    private final InfoService infoService;
    private final ApplicationEventPublisher publisher;
    private final S3Service s3Service;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public Board createBoard(BoardRequestDto requestDto) {
        // 작성자 찾기
        UserEntity currentUser = infoService.getCurrentUser();

        // 게시글 생성
        Board board = new Board();
        board.setUser(currentUser);
        board.setTitle(requestDto.getTitle());
        board.setContent(requestDto.getContent());
        board.setBoardType(requestDto.getBoardType());
        board.setImgUrl(requestDto.getImgUrl());

        // BoardType이 MARKET이면 price 설정
        if (requestDto.getBoardType() == BoardType.MARKET) {
            board.setPrice(requestDto.getPrice());
        }

        Board successBoard = boardRepository.save(board);

        //트랜젝션 이벤트 발행
        publisher.publishEvent(new BoardImageEvent(null, successBoard.getImgUrl()));

        return successBoard;
    }

    @Transactional
    public void updateBoard(Long boardId, BoardRequestDto requestDto) {
        String OldImgUrl = null;
        try {
            UserEntity currentUser = infoService.getCurrentUser();

            Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new RestApiException(NOT_FOUND_BOARD_ID));

            //업데이트 전 각 이미지 url 저장
            OldImgUrl = board.getImgUrl();
            String recentImgUrl = requestDto.getImgUrl();

            if (!currentUser.equals(board.getUser())) {
                throw new RestApiException(FORBIDDEN);
            }

            board.setTitle(requestDto.getTitle());
            board.setContent(requestDto.getContent());
            board.setBoardType(requestDto.getBoardType());
            board.setImgUrl(requestDto.getImgUrl());

            if (requestDto.getBoardType() == BoardType.MARKET) {
                board.setPrice(requestDto.getPrice());
            } else {
                board.setPrice(null); // 다른 게시판 유형일 경우 price 초기화
            }

            boardRepository.save(board);

            //만약 기존 이미지 Url이 있고, 갱신되는 이미지 URL가 기존 URL과 같지 않을 경우,
            //null이나 다른 URL로 변경된다면 트랜젝션 커밋 후 삭제 처리
            if (!Objects.equals(requestDto.getImgUrl(), OldImgUrl)) {
                publisher.publishEvent(new BoardImageEvent(OldImgUrl, recentImgUrl));
            }
        }catch (RestApiException e) {
            //Exception으로 이벤트 발행 안될 경우 삭제 처리
            if(requestDto.getImgUrl()!=null && !Objects.equals(requestDto.getImgUrl(), OldImgUrl)) {
                log.info("예외발생, afterUrl 삭제: {}", requestDto.getImgUrl());
                s3Service.deleteObject(requestDto.getImgUrl());
            }
            //다시 던짐
            throw e;
        }
    }

    @Transactional
    public void deleteBoard(Long id) {
        UserEntity currentUser = infoService.getCurrentUser();
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RestApiException(NOT_FOUND_BOARD_ID));

        // 작성자 확인
        if (!board.getUser().equals(currentUser)) {
            throw new RestApiException(FORBIDDEN);
        }

        // 나중에 수정할 필요 있음
        // 1. 관련 댓글 삭제
        commentRepository.deleteByBoard(board);
        // 2. 관련 좋아요 삭제
        likeRepository.deleteByBoard(board);
        // 3. 게시글 삭제
        boardRepository.delete(board);

        String ImgUrl = board.getImgUrl();

        //이벤트 발행
        publisher.publishEvent(new BoardImageEvent(ImgUrl, null));
    }

}

