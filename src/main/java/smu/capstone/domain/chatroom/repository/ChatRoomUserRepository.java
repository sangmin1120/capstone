package smu.capstone.domain.chatroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import smu.capstone.domain.chatroom.domain.ChatRoomUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, String> {

    Optional<ChatRoomUser> findByChatRoom_IdAndUserEntity_Id(String chatRoomId, Long userId);

    @Query("SELECT c1 FROM ChatRoomUser c1 " +
            "JOIN FETCH c1.chatRoom cr " +
            "JOIN FETCH c1.userEntity ue " +
            "JOIN ChatRoomUser c2 ON c1.chatRoom.id = c2.chatRoom.id " +
            "WHERE (c1.userEntity.id = :userId AND c2.userEntity.email = :otherEmail)")
    Optional<ChatRoomUser> findByUserEntity_userIdAndOtherUserEmail(@Param("userId") Long userId, @Param("otherEmail") String otherEmail);

    @Query("""
        SELECT cru FROM ChatRoomUser cru
        JOIN FETCH cru.chatRoom cr
        JOIN FETCH cr.chatRoomUsers cru2
        JOIN FETCH cru.userEntity m1
        JOIN FETCH cru2.userEntity m2
        WHERE cru.userEntity.id = :userEntityId AND cru.activation = 'ACTIVE'
    """)
    List<ChatRoomUser> findByUserEntity_Id(@Param("userEntityId")Long userEntityId);

    boolean existsByChatRoom_IdAndUserEntity_accountId(String chatRoomId, String userEntityAccountId);

    List<ChatRoomUser> findByChatRoom_Id(String roomId);

    boolean existsByChatRoom_IdAndUserEntity_accountIdAndActivation(String chatRoomId, String userEntityAccountId, ChatRoomUser.Activation activation);

    //객체 순환 조회 문제 생기므로 api 반환 시 주의
    @Query("""
        SELECT cru FROM ChatRoomUser cru
        JOIN FETCH cru.chatRoom cr
        JOIN FETCH cru.userEntity m
        WHERE cru.chatRoom.id = :chatRoomId AND m.accountId <> :accountId
        """)
    Optional<ChatRoomUser> findOtherChatRoomUserByChatRoom_idAndUserEntity_AccountId(@Param("chatRoomId") String chatRoomId, @Param("accountId") String accountId);

    Optional<ChatRoomUser> findByChatRoom_IdAndUserEntity_AccountId(String chatRoomId, String userEntityAccountId);
    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE ChatRoomUser cru
            SET cru.activation = 'UNAVAILABLE'
            WHERE cru.userEntity.id = :userId
            """)
    void bulkChatRoomActivation(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE ChatRoomUser cru
            SET cru.isOpponentDeleted = true
            WHERE cru.userEntity.id <> :userId AND cru.chatRoom.id IN (
                        SELECT cru2.chatRoom.id FROM ChatRoomUser cru2
                        WHERE cru2.userEntity.id = :userId )
            """)
    void bulkChatRoomOpponentDeleted(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Query(value = """
            UPDATE ChatRoomUser cru
            SET cru.activation = 'ACTIVE'
            WHERE cru.userEntity.accountId <> :userId
                          AND cru.activation = 'INACTIVE'
                          AND cru.chatRoom.id = :roomId
                          AND cru.createdAt < :sentAt
            """)
    void afterChatRoomUpdate(@Param("userId") String userId,
                            @Param("roomId") String roomId,
                            @Param("sentAt") LocalDateTime sentAt);
}