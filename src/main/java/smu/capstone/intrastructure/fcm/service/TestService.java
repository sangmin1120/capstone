package smu.capstone.intrastructure.fcm.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.intrastructure.fcm.dto.MessageNotification;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.respository.UserRepository;

import static smu.capstone.common.errorcode.AuthExceptionCode.INVALID_ID_OR_PASSWORD;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TestService {

    private final UserRepository userRepository;
    private final FCMService FCMService;

    public void pushTest(long memberId) {
        // 메시지를 만드는 코드 수정
        val member = findMember(memberId);
        val title = "푸시알림 제목";
        val body = "푸시알림 내용";
        FCMService.sendMessage(MessageNotification.of(member.getFcmToken(), title, body));
    }

    private UserEntity findMember(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RestApiException(INVALID_ID_OR_PASSWORD));
    }
}
