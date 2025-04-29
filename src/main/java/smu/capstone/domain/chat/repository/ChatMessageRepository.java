package smu.capstone.domain.chat.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import smu.capstone.domain.chat.domain.ChatMessage;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    @Override
    <S extends ChatMessage> S save(S entity);

    //자세한 쿼리 필요 - createBy 위주. - 해결
    @Query("{ 'chatRoomId' : ?0, 'sentAt' : { $gt: ?1 } }")
    List<ChatMessage> findAllBychatRoomId(String chatRoomId, LocalDateTime sentAt, Sort sort, Collation collation);

    @Query(value = "{ 'chatRoomId' : ?0 }", delete = true)
    @org.springframework.data.mongodb.core.annotation.Collation(value = "ko")
    void deleteAllByChatRoomId(String chatRoomId);

}
