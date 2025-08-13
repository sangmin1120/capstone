package smu.capstone.domain.chatroom.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import smu.capstone.domain.chatroom.repository.ChatRoomUserRepository;
import smu.capstone.domain.member.util.UserDeleteEvent;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserDeleteChatEventListener {

    private final ChatRoomUserRepository chatRoomUserRepository;

    @EventListener
    public void userDeleteChatEventListener(UserDeleteEvent event) {
        log.info("UserDeleteEventListener received userId: " + event.getUserId());
        chatRoomUserRepository.bulkChatRoomOpponentDeleted(event.getUserId());
        chatRoomUserRepository.bulkChatRoomActivation(event.getUserId());
    }
}
