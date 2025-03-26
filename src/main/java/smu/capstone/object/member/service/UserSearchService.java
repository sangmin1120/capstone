package smu.capstone.object.member.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.object.member.domain.UserEntity;
import smu.capstone.object.member.dto.AuthRequestDto;
import smu.capstone.object.member.dto.UserSearchDto;
import smu.capstone.object.member.respository.UserRepository;
import smu.capstone.object.member.service.EmailService.EmailType;
import smu.capstone.web.jwt.redisdomain.MailVerificationCache;
import smu.capstone.web.jwt.redisrepository.MailVerificationCacheRepository;

import static smu.capstone.common.errorcode.AuthExceptionCode.*;

@Service
@RequiredArgsConstructor
public class UserSearchService {

    private static final Long CERTIFICATION_KEY_EXPIRE_SECONDS = 10 * 60L;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailVerificationCacheRepository mailVerificationCacheRepository;
    private final EmailService emailService;

    //accountId 찾기
    public String searchId(UserSearchDto.SearchIdRequest searchIdRequest) {

        UserEntity userEntity = userRepository.findByEmail(searchIdRequest.getEmail())
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
        UserEntity userEntity = userRepository.findByEmail(authRequestDto.getEmail())
                .orElseThrow(() -> new RestApiException(INVALID_ID_OR_PASSWORD));

        //새로운 비밀번호 전송
        String newPassword = emailService.sendCertificationKey(authRequestDto.getEmail(), EmailType.PASSWORD_RESET);
        // 비밀번호 변경
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);
    }

    //비밀번호 찾기1 - 인증 메일 전송
    public void sendVerificationMail(AuthRequestDto.@Valid VerificationMail authRequestDto) {

        userRepository.findByEmail(authRequestDto.getEmail())
                .orElseThrow(()->new RestApiException(INVALID_ID_OR_PASSWORD));

        String certificationKey = emailService.sendCertificationKey(authRequestDto.getEmail(), EmailType.PASSWORD_CODE_MAIL);

        mailVerificationCacheRepository.deleteById(authRequestDto.getEmail());
        mailVerificationCacheRepository.save(MailVerificationCache.builder()
                .email(authRequestDto.getEmail())
                .verificationKey(certificationKey)
                .isVerify(false)
                .expiration(CERTIFICATION_KEY_EXPIRE_SECONDS)
                .build());
    }
}
