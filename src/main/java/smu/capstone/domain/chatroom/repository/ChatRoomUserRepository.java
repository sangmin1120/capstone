package smu.capstone.domain.chatroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import smu.capstone.domain.chatroom.domain.ChatRoomUser;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, String> {

    Optional<ChatRoomUser> findByChatRoom_IdAndUserEntity_Id(String chatRoomId, Long userId);

    @Query("SELECT c1 FROM ChatRoomUser c1 " +
            "JOIN FETCH c1.chatRoom cr " +
            "JOIN FETCH c1.userEntity ue " +
            "JOIN ChatRoomUser c2 ON c1.chatRoom.id = c2.chatRoom.id " +
            "WHERE (c1.userEntity.id = :userId AND c2.userEntity.id = :otherId)")
    Optional<ChatRoomUser> findByUserEntity_Ids(@Param("userId") Long userId, @Param("otherId") Long otherId);

    @Query("""
        SELECT cru FROM ChatRoomUser cru
        JOIN FETCH cru.chatRoom cr
        JOIN FETCH cr.chatRoomUsers cru2
        JOIN FETCH cru.userEntity m1
        JOIN FETCH cru2.userEntity m2
        WHERE cru.userEntity.id = :userEntityId
    """)
    Optional<List<ChatRoomUser>> findByUserEntity_Id(@Param("userEntityId")Long userEntityId);

    List<ChatRoomUser> findByChatRoom_Id(String roomId);
}
