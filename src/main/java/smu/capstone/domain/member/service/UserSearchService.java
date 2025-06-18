package smu.capstone.domain.member.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.common.util.CertificationKeyGenerator;
import smu.capstone.domain.alarm.service.AlarmService;
import smu.capstone.domain.member.dto.AuthRequestDto;
import smu.capstone.domain.member.dto.UserSearchDto;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.respository.UserRepository;
import smu.capstone.intrastructure.mail.dto.EmailType;
import smu.capstone.intrastructure.redis.domain.MailVerificationCache;
import smu.capstone.intrastructure.redis.repository.MailVerificationCacheRepository;

import static smu.capstone.common.errorcode.AuthExceptionCode.INVALID_ID_OR_PASSWORD;
import static smu.capstone.common.errorcode.AuthExceptionCode.NOT_VERIFIED_MAIL;

@Service
@RequiredArgsConstructor
public class UserSearchService {

    private static final Long CERTIFICATION_KEY_EXPIRE_SECONDS = 10 * 60L;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailVerificationCacheRepository mailVerificationCacheRepository;

    private final AlarmService alarmService;

    //accountId 찾기
    public String searchId(UserSearchDto.SearchIdRequest searchIdRequest) {

        UserEntity userEntity = userRepository.findByEmailAndIsDeleted(searchIdRequest.getEmail(), false)
                .orElseThrow(() -> new RestApiException(INVALID_ID_OR_PASSWORD));

        return userEntity.getAccountId();
    }

    //비밀번호 찾기2 - 인증번호 검사
    public void checkedVerifyMail(AuthRequestDto.VerificationMail authRequestDto) {

        MailVerificationCache mailVerificationCache = mailVerificationCacheRepository.findById(authRequestDto.getEmail())
                .orElseThrow(() -> new RestApiException(INVALID_ID_OR_PASSWORD));

        if (!mailVerificationCache.getVerificationKey().equals(authRequestDto.getVerificationKey())) {
            throw new RestApiException(NOT_VERIFIED_MAIL);
        }
        mailVerificationCacheRepository.save(MailVerificationCache.builder()
                .email(authRequestDto.getEmail())
                .verificationKey(authRequestDto.getVerificationKey())
                .isVerify(true)
                .expiration(CERTIFICATION_KEY_EXPIRE_SECONDS)
                .build());
    }

    //비밀번호 찾기3 - 변경된 비밀번호 전송 후, DB 변경
    public void verifyMailAndSendNewPassword(AuthRequestDto.@Valid VerificationMail authRequestDto) {
        checkedVerifyMail(authRequestDto);
        UserEntity userEntity = userRepository.findByEmailAndIsDeleted(authRequestDto.getEmail(), false)
                .orElseThrow(() -> new RestApiException(INVALID_ID_OR_PASSWORD));

        //새로운 비밀번호 전송
        String newPassword = CertificationKeyGenerator.generateStrongKey();
        alarmService.sendAuth(authRequestDto.getEmail(), EmailType.PASSWORD_RESET, newPassword);
        // 비밀번호 변경
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);
    }

    //비밀번호 찾기1 - 인증 메일 전송
    public void sendVerificationMail(AuthRequestDto.@Valid VerificationMail authRequestDto) {

        userRepository.findByEmailAndIsDeleted(authRequestDto.getEmail(), false) // 탈퇴 회원은 제외해야됨
                .orElseThrow(() -> new RestApiException(INVALID_ID_OR_PASSWORD));

        String key = CertificationKeyGenerator.generateStrongKey();
        alarmService.sendAuth(authRequestDto.getEmail(), EmailType.PASSWORD_CODE_MAIL, key);
    }
}
