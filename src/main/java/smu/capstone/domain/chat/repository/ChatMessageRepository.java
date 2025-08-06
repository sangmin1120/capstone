package smu.capstone.domain.chat.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import smu.capstone.domain.chat.domain.ChatMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String>, CustomChatMessageRepository {
    @Override
    <S extends ChatMessage> S save(S entity);

    //자세한 쿼리 필요 - createBy 위주. - 해결
    @Query("{ 'chatRoomId' : ?0, 'sentAt' : { $gt: ?1 } }")
    List<ChatMessage> findAllBychatRoomId(String chatRoomId, LocalDateTime sentAt, Sort sort, Collation collation);

    @Query(value = "{ 'chatRoomId' : ?0 }", delete = true)
    @org.springframework.data.mongodb.core.annotation.Collation(value = "ko")
    void deleteAllByChatRoomId(String chatRoomId);

    @Query(value = "{ 'chatRoomId' : ?0, 'messageType' : {$in: ['FILE', 'IMAGE']} }", fields = "{'message': 1, '_id': 0 }")
    @org.springframework.data.mongodb.core.annotation.Collation(value = "ko")
    List<String> findAllFileMessagesByChatRoomId(String chatRoomId);

    @org.springframework.data.mongodb.core.annotation.Collation(value = "ko")
    Optional<ChatMessage> findFirstByChatRoomIdOrderBySentAtDesc(String chatRoomId);

    //long countByChatRoomIdAndSenderNotAndSentAtAfter(String ChatRoomId, String sender, LocalDateTime sentAt);

    /***
     *
     * @param ChatRoomId
     * @param sentAt - 채팅방 리스트 사용 시 createAt과 lastLeaveAt 비교해 Max값을 파라미터로 입력
     * @return long
     */
    @org.springframework.data.mongodb.core.annotation.Collation(value = "ko")
    long countByChatRoomIdAndSentAtAfter(String ChatRoomId, LocalDateTime sentAt);
}
