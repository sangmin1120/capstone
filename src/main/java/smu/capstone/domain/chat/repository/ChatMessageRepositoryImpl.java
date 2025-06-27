package smu.capstone.domain.chat.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.stereotype.Repository;
import smu.capstone.domain.chat.domain.ChatMessage;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class ChatMessageRepositoryImpl implements CustomChatMessageRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<ChatMessage> findRecentMessage(String roomId, LocalDateTime createdAt,
                                               String beforeMsgId, LocalDateTime beforeMsgTime, int size) {
        Query query = new Query();
        query.addCriteria(
                Criteria.where("chatRoomId").is(roomId)
                        .and("sentAt").gt(createdAt)
                        .orOperator(
                                Criteria.where("sentAt").lt(beforeMsgTime),
                                Criteria.where("sentAt").lte(beforeMsgTime)
                                        .and("_id").lt(beforeMsgId)
                        ))
                .with(Sort.by(Sort.Direction.DESC, "sentAt", "_id"))
                .limit(size);

        Collation collection = Collation.of("ko");
        query.collation(collection);
        return mongoTemplate.find(query, ChatMessage.class);
    }

    @Override
    public List<ChatMessage> findRecentMessage(String roomId, LocalDateTime createdAt, int size) {
        Query query = new Query();
        query.addCriteria(
                Criteria.where("chatRoomId").is(roomId)
                        .and("sentAt").gt(createdAt))
                .with(Sort.by(Sort.Direction.DESC, "sentAt", "_id"))
                .limit(size);

        Collation collection = Collation.of("ko");
        query.collation(collection);
        return mongoTemplate.find(query, ChatMessage.class);
    }
}
