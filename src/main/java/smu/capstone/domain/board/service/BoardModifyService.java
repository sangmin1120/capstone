package smu.capstone.domain.board.service;

//import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.domain.board.entity.Board;
import smu.capstone.domain.board.event.BoardImageEvent;
import smu.capstone.domain.board.repository.BoardRepository;
import smu.capstone.domain.board.dto.BoardRequestDto;
import smu.capstone.domain.file.service.S3Service;
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

    @Transactional
    public Board createBoard(BoardRequestDto requestDto) {
        // 작성자 찾기
        UserEntity currentUser = infoService.getCurrentUser();

        // 게시글 생성
        Board board = Board.builder()
                .user(currentUser)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .boardType(requestDto.getBoardType())
                .imgUrl(requestDto.getImgUrl())
                .build();

        Board successBoard = boardRepository.save(board);
        publisher.publishEvent(new BoardImageEvent(null, successBoard.getImgUrl()));

        return successBoard;
    }

    @Transactional
    public void updateBoard(Long boardId, BoardRequestDto requestDto) {
        try {
            UserEntity currentUser = infoService.getCurrentUser();
            //Optional로 꺼내 값이 있는지 확인
            Board board = boardRepository.findById(boardId).orElseThrow(
                    () -> new RestApiException(NOT_FOUND));

            if (!currentUser.equals(board.getUser())) {
                throw new RestApiException(NOT_FOUND_BOARD_ID);
            }

            //삭제 전 이미지 url 저장
            String OldImgUrl = board.getImgUrl();
            String recentImgUrl = requestDto.getImgUrl();

            //수정할 부분: 업데이트 부분
            Board updated = board.update(requestDto);
            boardRepository.save(updated);

            //만약 기존 이미지 Url이 있고, 갱신되는 이미지 URL가 기존 URL과 같지 않을 경우,
            //null이나 다른 URL로 변경된다면 트랜젝션 커밋 후 삭제 처리
            if (!Objects.equals(requestDto.getImgUrl(), OldImgUrl)) {
                publisher.publishEvent(new BoardImageEvent(OldImgUrl, recentImgUrl));
            }
        }catch (RestApiException e) {
            //Exception으로 이벤트 발행 안될 경우 삭제 처리
            if(requestDto.getImgUrl()!=null) {
                log.info("예외발생, afterUrl 삭제: {}", requestDto.getImgUrl());
                s3Service.deleteObject(requestDto.getImgUrl());
            }
            throw e;
        }
    }

    @Transactional
    public void deleteBoard(Long id) {
        UserEntity currentUser = infoService.getCurrentUser();
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new RestApiException(NOT_FOUND));

        // 게시글의 주인이 아니면 삭제 불가
        if (!board.getUser().equals(currentUser)) {
            throw new RestApiException(FORBIDDEN);
        }

        String ImgUrl = board.getImgUrl();
        boardRepository.delete(board);

        if(ImgUrl != null){
            publisher.publishEvent(new BoardImageEvent(ImgUrl, null));
        }
    }
}