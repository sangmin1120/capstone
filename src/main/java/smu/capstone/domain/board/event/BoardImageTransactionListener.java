package smu.capstone.domain.board.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import smu.capstone.domain.file.service.S3Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;

/***
 * 참고: 해당 리스너 안에서 에러가 날 경우 전역 ExceptionListner가 감지하지 못합니다
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class BoardImageTransactionListener {

    private final S3Service s3Service;

    //board 수정/삭제 트랜젝션 성공 시 실행
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCommit(BoardImageEvent event) {
        log.info("onCommit before: {}, after: {}", event.getBeforeImgUrl(), event.getAfterImgUrl());
        //업로드 시 null - return을 통해 처리하지 않음
        if(event.getBeforeImgUrl() == null){
            return;
        }
        //수정 및 삭제의 경우 성공할 경우 전의 내용을 삭제해야 하므로 삭제 처리
        //실패 시 RDB에 넣어 배치 처리
        s3Service.deleteObject(event.getBeforeImgUrl());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onRollback(BoardImageEvent event) {
        log.info("onRollback before: {}, after: {}", event.getBeforeImgUrl(), event.getAfterImgUrl());
        if(event.getAfterImgUrl() == null){
            return;
        }
        s3Service.deleteObject(event.getAfterImgUrl());
    }
}