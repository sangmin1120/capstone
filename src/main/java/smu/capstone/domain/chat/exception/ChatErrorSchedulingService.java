package smu.capstone.domain.chat.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.domain.chat.domain.ChatMessage;
import smu.capstone.domain.chat.repository.ChatMessageRepository;
import smu.capstone.domain.chatroom.repository.ChatRoomUserRepository;

import java.util.List;

import static smu.capstone.domain.chat.exception.AsyncExceptionHandler.SAVE_DLQ_KEY;
import static smu.capstone.domain.chat.exception.AsyncExceptionHandler.UPDATE_DLQ_KEY;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatErrorSchedulingService {

    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final RedisTemplate<String, ChatMessage> redisTemplate;

    @Scheduled(cron = "0 */5 * * * *")  //매분마다 체크
    public void saveMessageScheduling(){
        try {
            if(!redisTemplate.hasKey(SAVE_DLQ_KEY)){
                //없다면 넘어감
                return;
            }

            List<ChatMessage> chatMessages = redisTemplate.opsForList().range(SAVE_DLQ_KEY, 0, -1);
            if(chatMessages == null || chatMessages.isEmpty()){
                redisTemplate.delete(SAVE_DLQ_KEY);
                return;
            }

            chatMessageRepository.saveAll(chatMessages);

            //성공 시 리스트 전체 삭제
            redisTemplate.delete(SAVE_DLQ_KEY);
        }catch (Exception e){
            log.error("chat DLQ 에러 발생: {}", e.getMessage(), e.fillInStackTrace());
        }
    }

    @Transactional
    @Scheduled(cron = "0 0 */6 * * *")
    public void loadMessageScheduling(){
        if(!redisTemplate.hasKey(UPDATE_DLQ_KEY)){
            return;
        }
        for(int i = 0; i<1000; i++){
            ChatMessage msg = redisTemplate.opsForList().rightPop(UPDATE_DLQ_KEY);
            if(msg == null){
                break;
            }
            try {
                chatRoomUserRepository.afterChatRoomUpdate(msg.getSender(), msg.getChatRoomId(), msg.getSentAt());
            }catch (Exception e){
                //redisTemplate.opsForList().leftPush("Fail_update_msg", msg);
                log.error("[ChatErrorSchedulingService] DLQ 재처리 실패, 무시: {}", e.getMessage());
                log.error("{} {} {}", msg.getChatRoomId(), msg.getSender(), msg.getSentAt());
            }
        }
    }
}
