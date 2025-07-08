package smu.capstone.domain.chat.repository;

import smu.capstone.domain.chat.domain.ChatMessage;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomChatMessageRepository {
    List<ChatMessage> findRecentMessage(String roomId,
                                        LocalDateTime createdAt,
                                        String beforeMsgId,
                                        LocalDateTime beforeMsgTime,
                                        int size);
    List<ChatMessage> findRecentMessage(String roomId,
                                        LocalDateTime createdAt,
                                        int size);
}
