package smu.capstone.domain.chatroom.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import smu.capstone.common.errorcode.ChatRoomExceptionCode;
import smu.capstone.common.errorcode.CommonStatusCode;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.domain.chat.domain.ChatMessage;
import smu.capstone.domain.chat.repository.ChatMessageRepository;
import smu.capstone.domain.chatroom.domain.ChatRoom;
import smu.capstone.domain.chatroom.domain.ChatRoomUser;
import smu.capstone.domain.chatroom.dto.*;
import smu.capstone.domain.chatroom.event.ChatMessageFileEvent;
import smu.capstone.domain.chatroom.exception.ChatRoomException;
import smu.capstone.domain.chatroom.repository.ChatRoomRepository;
import smu.capstone.domain.chatroom.repository.ChatRoomUserRepository;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.respository.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static smu.capstone.domain.member.util.LoginUserUtil.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher publisher;
    private final RedisTemplate<String, String> redisTemplete;

    private static final String TIME_CACHE_KEY = "createAtCache";

    public List<ChatRoomDto> getChatRoomList() {
        //중복 - board 서비스와 반환값은 같은데 다른 함수를 사용 - ?
        Long userId = getLoginMemberId();
        if(userId == null) {
            throw new ChatRoomException(ChatRoomExceptionCode.NOT_FOUND_USER);
        }

        List<ChatRoomUser> chatRoomUserList = chatRoomUserRepository.findByUserEntity_Id(userId);
        for(ChatRoomUser cru : chatRoomUserList ) {
            log.info("ID: {}",cru.getUserEntity().getAccountId());
            log.info("roomId: {} ", cru.getChatRoom().getId());
            log.info("activation: {}", cru.getActivation().toString());
        }
        try {
            return getChatRoomsByUserId(chatRoomUserList, userId);
        }catch (Exception e) {
            log.error(e.getMessage());
            throw new ChatRoomException(CommonStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    //입장 시 User와 방 참여자 정보 반환
    public ChatRoomEnterDto enterChatRoom(String roomId) {
        Long userId = getLoginMemberId();
//        log.info("userId: {}", userId);
        if(roomId == null || userId == null) {
            throw new ChatRoomException(ChatRoomExceptionCode.NOT_FOUND_ALL);
        }
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new ChatRoomException(ChatRoomExceptionCode.NOT_FOUND_ROOM)
        );

        ChatRoomUserPair pair = ChatRoomUserPair.getPair(userId, chatRoom.getChatRoomUsers());
        RoomParticipantDto participant = getParticipateInfo(pair.getOtherChatRoomUser());

        //User가 나가고 다시 들어왔을 때 메시지가 없음에도 상대의 안 읽은 cnt 값이 남아있으므로 보정해줘야 함
        int cnt = 0;
        if(pair.getChatRoomUser().getCreatedAt().isBefore(chatRoom.getLastMessageAt())){
            cnt = pair.getOtherChatRoomUser().getNotReadCount();
        }

        return ChatRoomEnterDto.builder()
                .userId(pair.getChatRoomUser().getUserEntity().getAccountId()) // entitiy의 id가 아닌 accountId가 사용됨
                .participant(participant)
                //다른 사람의 안 읽은 메시지수 가져옴
                .otherUserUnreadCount(cnt)
                .build();
    }

    public MessageScrollResponseDto getMessageHistory(String roomId, String lastMessageId, String lastTime, int size) {
        //밀리초 사용 위해 변환
        try {
            Long userId = getLoginMemberId();
            log.info("time {}", lastTime);
            LocalDateTime lastSentAt = (lastTime != null) ? LocalDateTime.parse(lastTime) : null;
            log.info("lastSentAt {}", lastSentAt);

            //캐싱
            String time = redisTemplete.opsForValue().get(TIME_CACHE_KEY+roomId+userId);
            log.info("time {}", time);
            if (time == null) {
                ChatRoomUser cru = chatRoomUserRepository.findByChatRoom_IdAndUserEntity_Id(roomId, userId).orElseThrow(
                        () -> new ChatRoomException(ChatRoomExceptionCode.NOT_FOUND_ROOM));
                time = cru.getCreatedAt().toString();
                redisTemplete.opsForValue().set(TIME_CACHE_KEY+roomId+userId, time);
                redisTemplete.expire(TIME_CACHE_KEY+roomId+userId, Duration.ofMinutes(10));
            }
            LocalDateTime createAt = LocalDateTime.parse(time);
            log.info("createAt {}", createAt);
            List<ChatMessage> messages;
            if(lastMessageId == null ||  lastSentAt == null){
                //첫 요청 시
                log.info("첫번째 요청");
                messages = chatMessageRepository.findRecentMessage(roomId, createAt, size+1);
            }
            else {
                log.info("다음 요청");
                messages = chatMessageRepository.findRecentMessage(roomId, createAt, lastMessageId, lastSentAt, size + 1);
            }

            //0인지 확인 메시지 있으면 Cursor 구성, 0이면 다르게 구성, null 넣기
            boolean hasNext = messages.size() > size;
            int idx = 0;

            //채팅메시지가 있다면 idx 설정
            if(!messages.isEmpty()) {
                log.info("채팅 메시지 존재  {} {}", idx, messages.size());
                idx = messages.size() - 1;
                if (hasNext) {
                    log.info("채팅 메시지 Next 존재,  {} {}", idx, messages.size());
                    messages.remove(idx);
                    idx = messages.size() - 1;
                }
            }
            return MessageScrollResponseDto.builder()
                    .messages(messages)
                    .nextCursor(getMessageCursor(idx, hasNext, messages))
                    .build();
        }catch (IllegalArgumentException e){
            log.error(e.getMessage(), e.getCause(), e.getStackTrace());
            throw new RestApiException(CommonStatusCode.INVALID_PARAMETER);
        }
        catch (RuntimeException e) {
            log.error(e.getMessage(), e.getCause(), e.getStackTrace());
            throw new ChatRoomException(CommonStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    protected MessageScrollResponseDto.MessageCursor getMessageCursor(int idx, boolean hasNext, List<ChatMessage> messages) {
        if(messages.isEmpty()) {
            return MessageScrollResponseDto.MessageCursor.builder()
                    .hasNext(hasNext)
                    .lastMessageId(null)
                    .lastSentAt(null)
                    .build();
        }
        return MessageScrollResponseDto.MessageCursor.builder()
                .hasNext(hasNext)
                .lastSentAt(messages.get(idx).getSentAt())
                .lastMessageId(messages.get(idx).getId())
                .build();
    }

    //있다면 기존 RoomId 반환, 없다면 새로운 RoomId 생성 후 반환 - 유저 삭제 시 이벤트 리스너 필요
    public String createChatRoom(ChatRoomCreateDto createDto) {
        Long userId = getLoginMemberId();
        String otherUserEmail = createDto.getOtherUserEmail();

        if(userId == null || otherUserEmail == null) {
            throw new ChatRoomException(CommonStatusCode.INVALID_PARAMETER);
        }

        //탈퇴 처리된 회원이라면 생성 불가
        if(userRepository.existsByEmailAndIsDeleted(otherUserEmail, true)) {
            throw new ChatRoomException(ChatRoomExceptionCode.USER_DEACTIVATED);
        }
        Optional<ChatRoomUser> userOps = chatRoomUserRepository
                .findByUserEntity_userIdAndOtherUserEmail(userId, otherUserEmail);

        //채팅방이 없는 경우 생성
        if (userOps.isEmpty()) {
            return createNewChatRoom(userId, otherUserEmail);
        }
        //채팅방이 있는 경우
        ChatRoomUser chatRoomUser = userOps.get();

        //사용불가한 채팅방인 경우 생성
        if(chatRoomUser.isOpponentDeleted()
                || ChatRoomUser.Activation.UNAVAILABLE.equals(chatRoomUser.getActivation())){
            return createNewChatRoom(userId, otherUserEmail);
        }
        try{
            //있다면 나의 ACTIVE 여부 확인 - 활성화 여부 확인 후 설정
            setUserActive(chatRoomUser);
            //채팅방 Id 반환
            return chatRoomUser.getChatRoom().getId();
        }catch (Exception e) {
            log.error("{} {} {}",e.getMessage(),e.getCause(), e.getStackTrace());
            throw new ChatRoomException(CommonStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    /***
     * RoomId와 UserId를 가져와 chatRoom을 삭제하는 메서드
     * isOpponentDeleted가 T : unavailable 설정
     *                    F : inactive 설정
     */
    public void deleteChatRoom(String roomId) {
        Long userId = getLoginMemberId();
        if(roomId == null || userId == null) {
            throw new ChatRoomException(CommonStatusCode.INVALID_PARAMETER);
        }
        try {
//            하드 삭제 도입 시 chatRoom으로 조회해 모두 삭제 처리할 것
//            ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
//                    () -> new ChatRoomException(ChatRoomExceptionCode.NOT_FOUND_ROOM)
//            );

            ChatRoomUser chatRoomUser = chatRoomUserRepository
                    .findByChatRoom_IdAndUserEntity_Id(roomId, userId).orElseThrow(
                            () -> new ChatRoomException(ChatRoomExceptionCode.NOT_FOUND_ROOM)
            );

            //상대 채팅방이 ACTIVE 상태라면 user의 채팅방만 비활성화
            if (!chatRoomUser.isOpponentDeleted()) {
                chatRoomUser.setActivation(ChatRoomUser.Activation.INACTIVE);
                chatRoomUser.setCreatedAt(LocalDateTime.now());
                chatRoomUser.setNotReadCount(0);
                chatRoomUserRepository.save(chatRoomUser);
                return;
            }

            ChatMessageFileEvent event = new ChatMessageFileEvent(roomId);

            //비활성화 상태 체크
            chatRoomUser.setActivation(ChatRoomUser.Activation.UNAVAILABLE);
            chatRoomUserRepository.save(chatRoomUser);

            //채팅방에서 파일 모두 삭제
            chatMessageRepository.findAllFileMessagesByChatRoomId(roomId);
            //개인정보 보호 위해 S3 파일은 삭제
            publisher.publishEvent(event);
/***
            //삭제 전 이벤트 생성
            ChatMessageFileEvent event = new ChatMessageFileEvent(roomId);
            //hard 삭제 처리
            chatMessageRepository.deleteAllByChatRoomId(chatRoom.getId());
            chatRoomUserRepository.delete(chatRoomOther);
            chatRoomUserRepository.delete(chatRoomUser);
            chatRoomRepository.delete(chatRoom);
            //삭제 commit 성공 이벤트 발행
            publisher.publishEvent(event);
*/
        }catch (NullPointerException e){
            throw new ChatRoomException(CommonStatusCode.INVALID_PARAMETER);
        } catch (ChatRoomException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ChatRoomException(CommonStatusCode.INTERNAL_SERVER_ERROR);
        }
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
            log.info("[ChatRoomService]: setUserActive - activation is inactive, set activation user");
            chatRoomUser.setActivation(ChatRoomUser.Activation.ACTIVE);
            chatRoomUser.setCreatedAt(LocalDateTime.now());
            chatRoomUserRepository.save(chatRoomUser);
        }
    }

    protected RoomParticipantDto getParticipateInfo(ChatRoomUser other){
        if(other == null
                || other.getActivation().equals(ChatRoomUser.Activation.UNAVAILABLE)
                || other.getUserEntity() == null) {
            return RoomParticipantDto.builder()
                    .id(-1L)
                    .imgUrl(null)
                    .userId(null)
                    .username(null)
                    .userType(null).build();
        }
        return RoomParticipantDto.builder()
                .userType(other.getUserEntity().getUserType())
                .imgUrl(other.getUserEntity().getImgUrl())
                .username(other.getUserEntity().getUsername())
                .userId(other.getUserEntity().getAccountId()) // entitiy의 id가 아닌 accountId가 사용됨
                .id(other.getUserEntity().getId())
                .build();
    }

    protected String createNewChatRoom(Long userId, String otherUserEmail){
        //User 정보를 가져옴
        UserEntity user = userRepository.findById(userId).orElseThrow(
                () -> new ChatRoomException(ChatRoomExceptionCode.NOT_FOUND_USER)
        );
        UserEntity otherUser = userRepository.findByEmail(otherUserEmail).orElseThrow(
                () -> new ChatRoomException(ChatRoomExceptionCode.NOT_FOUND_USER)
        );
        if(user.getId().equals(otherUser.getId())) {
            throw new ChatRoomException(ChatRoomExceptionCode.EQUAL_USER);
        }
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
        //User입장에서 ACTIVE 상태인 chatRoom만 포함되어있음
        chatRooms = chatRoomUserList.stream()
                .map(
                list -> {
                    ChatRoom chatRoom = list.getChatRoom();
                    List<RoomParticipantDto> otherUsers = chatRoom.getChatRoomUsers().stream()
                            .filter( cru -> !userid.equals(cru.getUserEntity().getId()))
                            .map(other -> ChatRoomUser.Activation.UNAVAILABLE.equals(other.getActivation())
                                    || other.getUserEntity() == null ?
                            //.map(ChatRoomUser::getUserEntity)
                            //.filter(user -> user == null || !userid.equals(user.getId()))
                            //.map(user -> user == null || user.isDeleted() ?
                                    //회원이 탈퇴했을 경우 - 기본값 null 넣음
                                    RoomParticipantDto.builder()
                                            .id(-1L)
                                            .userId(null)
                                            .username(null)
                                            .imgUrl(null)
                                            .userType(null)
                                            .build()
                                    //회원이 있을 경우
                                    : RoomParticipantDto.builder()
                                    .id(other.getUserEntity().getId())
                                    .userId(other.getUserEntity().getAccountId()) // entitiy의 id가 아닌 accountId가 사용됨
                                    .username(other.getUserEntity().getUsername())
                                    .imgUrl(other.getUserEntity().getImgUrl())
                                    .userType(other.getUserEntity().getUserType())
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
