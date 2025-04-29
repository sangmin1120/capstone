package smu.capstone.domain.chatroom.dto;

import lombok.Getter;
import lombok.Setter;
import smu.capstone.domain.chatroom.domain.ChatRoomUser;
import smu.capstone.domain.member.entity.UserEntity;

import java.util.List;

@Getter
@Setter
public class ChatRoomUserPair {
    ChatRoomUser chatRoomUser;
    ChatRoomUser otherChatRoomUser;

    public static ChatRoomUserPair getPair(List<ChatRoomUser> chatRoomUsers){
        ChatRoomUserPair pair = new ChatRoomUserPair();
        pair.chatRoomUser = chatRoomUsers.get(0);
        pair.otherChatRoomUser = chatRoomUsers.get(1);
        return pair;
    }

    public static ChatRoomUserPair getPair(Long userId, List<ChatRoomUser> chatRoomUsers){
        ChatRoomUserPair pair = new ChatRoomUserPair();
        for(ChatRoomUser chatRoomUser : chatRoomUsers){
            UserEntity user = chatRoomUser.getUserEntity();
            if(user != null && userId.equals(user.getId())){
                pair.chatRoomUser = chatRoomUser;
            } else {
                //null 허용
                pair.otherChatRoomUser = chatRoomUser;
            }
        }
        return pair;
    }
}