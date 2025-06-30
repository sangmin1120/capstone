package smu.capstone.domain.chatroom.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import smu.capstone.domain.file.service.S3Service;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatRoomTransactionListener {

    private final S3Service s3Service;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCommit(ChatMessageFileEvent event) {
        log.info("onCommit {}", event.toString());
        s3Service.deleteChatObjects(event.getRoomId());
    }
}