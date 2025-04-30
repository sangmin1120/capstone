package smu.capstone.domain.chatroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smu.capstone.domain.chatroom.domain.ChatRoom;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

    @Override
    Optional<ChatRoom> findById(String roomId);
}
