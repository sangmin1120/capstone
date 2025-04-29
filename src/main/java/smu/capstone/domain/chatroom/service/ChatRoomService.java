package smu.capstone.domain.chatroom.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.stereotype.Service;
import smu.capstone.common.errorcode.ChatRoomExceptionCode;
import smu.capstone.common.errorcode.CommonStatusCode;
import smu.capstone.domain.chat.domain.ChatMessage;
import smu.capstone.domain.chat.repository.ChatMessageRepository;
import smu.capstone.domain.chatroom.domain.ChatRoom;
import smu.capstone.domain.chatroom.domain.ChatRoomUser;
import smu.capstone.domain.chatroom.dto.*;
import smu.capstone.domain.chatroom.exception.ChatRoomException;
import smu.capstone.domain.chatroom.repository.ChatRoomRepository;
import smu.capstone.domain.chatroom.repository.ChatRoomUserRepository;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.entity.UserType;
import smu.capstone.domain.member.respository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public List<ChatRoomDto> getChatRoomList(Long userid) {
        if(userid == null) {
            throw new ChatRoomException(ChatRoomExceptionCode.NOT_FOUND_USER);
        }

        List<ChatRoomUser> chatRoomUserList = chatRoomUserRepository.findByUserEntity_Id(userid)
                .orElseThrow( () -> new ChatRoomException(ChatRoomExceptionCode.NOT_FOUND_USER)
        );
        try {
            List<ChatRoomDto> chatRoomList = getChatRoomsByUserId(chatRoomUserList, userid);
            return chatRoomList;
        }catch (Exception e) {
            log.error(e.getMessage());
            throw new ChatRoomException(CommonStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    ///TODO: null 처리 보강
    public ChatRoomEnterDto enterChatRoom(String roomId, Long userid) {
        if(roomId == null || userid == null) {
            throw new ChatRoomException(ChatRoomExceptionCode.NOT_FOUND_ALL);
        }
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new ChatRoomException(ChatRoomExceptionCode.NOT_FOUND_ROOM)
        );

        ChatRoomUserPair pair = ChatRoomUserPair.getPair(userid, chatRoom.getChatRoomUsers());

        //ChatMessage List 얻음
        LocalDateTime createTime = pair.getChatRoomUser().getCreatedAt();
        List<ChatMessage> chatMessageList = getMessageList(roomId, createTime);

        if(chatMessageList == null) {
            log.info("chatMessageList is null");
            chatMessageList = new ArrayList<>();
        }

        return ChatRoomEnterDto.builder()
                //다른 사람의 안 읽은 메시지수 가져옴
                .otherUserUnreadCount(pair.getOtherChatRoomUser().getNotReadCount())
                .chatMessageList(chatMessageList)
                .build();
    }

    //있다면 기존 RoomId 반환, 없다면 새로운 RoomId 생성 후 반환
    public String createChatRoom(ChatRoomCreateDto createDto) {

        Long userId = createDto.getUserId();
        Long otherUserId = createDto.getOtherUserId();

        if(userId == null || otherUserId == null) {
            throw new ChatRoomException(CommonStatusCode.INVALID_PARAMETER);
        }

        try {
            Optional<ChatRoomUser> userOps = chatRoomUserRepository.findByUserEntity_Ids(userId, otherUserId);
            //채팅방이 없는 경우 - 생성
            if (userOps.isEmpty()) {
                return createNewChatRoom(userId, otherUserId);
            }

            //채팅방이 있는 경우
            ChatRoomUser chatRoomUser = userOps.get();

            //있다면 나의 ACTIVE 여부 확인 - 활성화 여부 확인 후 설정
            setUserActive(chatRoomUser);
            //채팅방 Id 반환
            return chatRoomUser.getChatRoom().getId();
        }catch (Exception e) {
            log.error(e.getMessage());
            throw new ChatRoomException(CommonStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    /***
     * RoomId와 UserId를 가져와 chatRoom을 삭제하는 메서드
     * 1) 채팅 상대가 INACTIVE라면 삭제
     * 2) 채팅 상대가 ACTIVE라면 UserId의 chatRoomUser 상태 INACTIVE
     */
    public void deleteChatRoom(Long userId, String roomId) {
        if(roomId == null || userId == null) {
            throw new ChatRoomException(CommonStatusCode.INVALID_PARAMETER);
        }
        try {
            ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                    () -> new ChatRoomException(ChatRoomExceptionCode.NOT_FOUND_ROOM)
            );

            ChatRoomUserPair userPair = ChatRoomUserPair.getPair(userId, chatRoom.getChatRoomUsers());
            ChatRoomUser chatRoomUser = userPair.getChatRoomUser();
            ChatRoomUser chatRoomOther = userPair.getOtherChatRoomUser();

            if(chatRoomUser == null || chatRoomOther == null) {
                throw new ChatRoomException(CommonStatusCode.NOT_FOUND);
            }

            //상대 user의 채팅방이 ACTIVE 상태라면 user의 채팅방만 비활성화
            if (isUserActive(chatRoomOther)) {
                chatRoomUser.setActivation(ChatRoomUser.Activation.INACTIVE);
                chatRoomUser.setCreatedAt(LocalDateTime.now());
                chatRoomUser.setNotReadCount(0);
                chatRoomUserRepository.save(chatRoomUser);
                return;
            }

            chatMessageRepository.deleteAllByChatRoomId(chatRoom.getId());
            chatRoomUserRepository.delete(chatRoomOther);
            chatRoomUserRepository.delete(chatRoomUser);
            chatRoomRepository.delete(chatRoom);
        }catch (NullPointerException e){
            throw new ChatRoomException(CommonStatusCode.INVALID_PARAMETER);
        } catch (ChatRoomException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ChatRoomException(CommonStatusCode.INTERNAL_SERVER_ERROR);
        }
    }


    public List<ChatMessage> getMessageList(String chatRoomId, LocalDateTime time) {
        try {
            Collation collation = Collation.of("ko"); // 한국으로 로케일 설정
            Sort sort = Sort.by(Sort.Order.desc("timestamp"));
            List<ChatMessage> list = chatMessageRepository.findAllBychatRoomId(chatRoomId, time, sort, collation);
            return list;
        }
        catch (Exception e){
            throw new ChatRoomException(CommonStatusCode.INVALID_PARAMETER);
        }
    }

    /*** 같은 채팅방에 있는 상대 User가 탈퇴했는지 확인하는 메서드
     *   삭제라면 ture, 아직 존재한다면 false
     */
    public boolean isDeleteUserId(String roomId, Long userId) {
        List<ChatRoomUser> chatRoomUserList = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new ChatRoomException(ChatRoomExceptionCode.NOT_FOUND_ALL)
        ).getChatRoomUsers();

        ChatRoomUserPair userPair = ChatRoomUserPair.getPair(userId, chatRoomUserList);
        if(userPair.getOtherChatRoomUser() == null
                || userPair.getOtherChatRoomUser().getUserEntity() == null) {
            return true;
        }
        return false;
    }

    /***
     * 해당 User의 채팅방 상태가 ACTIVE인지 INACTIVE인지 확인
     * ACTIVE라면 true, 아니라면 false 반환
     */
    protected boolean isUserActive(ChatRoomUser chatRoomUser) {
        return chatRoomUser.getActivation().equals(ChatRoomUser.Activation.ACTIVE);
    }

    /**채팅방에 들어갔을 때 user의 채팅방 상태가 inactive상태라면 active로 변경**/
    protected void setUserActive(ChatRoomUser chatRoomUser) {
        //INACTIVE 상태라면 설정
        if(!isUserActive(chatRoomUser)) {
            log.info("setOtherUserActive: activation is inactive, set activation other user");
            chatRoomUser.setActivation(ChatRoomUser.Activation.ACTIVE);
            chatRoomUser.setCreatedAt(LocalDateTime.now());
            chatRoomUserRepository.save(chatRoomUser);
        }
    }

    protected String createNewChatRoom(Long userId, Long otherUserId){
        //User 정보를 가져옴
        UserEntity user = userRepository.findById(userId).orElseThrow(
                () -> new ChatRoomException(ChatRoomExceptionCode.NOT_FOUND_USER)
        );
        UserEntity otherUser = userRepository.findById(otherUserId).orElseThrow(
                () -> new ChatRoomException(ChatRoomExceptionCode.NOT_FOUND_USER)
        );

        try {
            //생성 시간으로 설정
            ChatRoom chatRoom = ChatRoom.builder()
                    .id(UUID.randomUUID().toString())
                    .lastMessageAt(LocalDateTime.now())
                    .build();

            //ChatRoomUser 중간 테이블 생성
            ChatRoomUser chatRoomUser = ChatRoomUser.builder()
                    .chatRoom(chatRoom)
                    .userEntity(user)
                    .build();
            ChatRoomUser chatRoomOtherUser = ChatRoomUser.builder()
                    .chatRoom(chatRoom)
                    .userEntity(otherUser)
                    .build();

            //Entity 모두 저장
            chatRoomRepository.save(chatRoom);
            chatRoomUserRepository.save(chatRoomUser);
            chatRoomUserRepository.save(chatRoomOtherUser);

            return chatRoom.getId();
        }catch (Exception e){
            log.error(e.getMessage());
            throw new ChatRoomException(CommonStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    protected List<ChatRoomDto> getChatRoomsByUserId(List<ChatRoomUser> chatRoomUserList, Long userid) {
        List<ChatRoomDto> chatRooms;

        chatRooms = chatRoomUserList.stream().map(
                list -> {
                    ChatRoom chatRoom = list.getChatRoom();
                    List<RoomParticipantDto> otherUsers = chatRoom.getChatRoomUsers().stream()
                            //ACTIVE 상태인 chatRoom만 포함
                            .filter(chatRoomUser -> ChatRoomUser.Activation.ACTIVE.equals(chatRoomUser.getActivation()))
                            .map(ChatRoomUser::getUserEntity)
                            .filter(user -> user == null || !userid.equals(user.getId()))
                            .map(user -> user == null ?
                                    //회원이 탈퇴했을 경우 - 기본값 null 넣음
                                    RoomParticipantDto.builder()
                                            .id(-1L)
                                            .userid("NULL")
                                            .username("NULL")
                                            .imgUrl("NULL")
                                            .userType(UserType.PATIENT)
                                            .build()
                                    //회원이 있을 경우
                                    : RoomParticipantDto.builder()
                                    .id(user.getId())
                                    .userid(user.getAccountId())
                                    .username(user.getUsername())
                                    .imgUrl(user.getImgUrl())
                                    .userType(user.getUserType())
                                    .build()
                            ).collect(Collectors.toList());
                    return ChatRoomDto.builder()
                            .roomId(chatRoom.getId())
                            .userId(userid)
                            .lastMessageAt(chatRoom.getLastMessageAt())
                            .notReadCount(list.getNotReadCount())
                            .participants(otherUsers)
                            .build();
                }).sorted(
                (o1, o2) -> o2.getLastMessageAt().compareTo(o1.getLastMessageAt()
                )).collect(Collectors.toList());
        return chatRooms;
    }

}
