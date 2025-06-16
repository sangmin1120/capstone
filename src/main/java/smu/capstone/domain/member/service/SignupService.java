package smu.capstone.domain.member.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.common.util.CertificationKeyGenerator;
import smu.capstone.domain.alarm.service.AlarmService;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.dto.AuthRequestDto;
import smu.capstone.domain.member.respository.UserRepository;
import smu.capstone.intrastructure.mail.dto.EmailType;
import smu.capstone.intrastructure.redis.domain.MailVerificationCache;
import smu.capstone.intrastructure.redis.repository.MailVerificationCacheRepository;

import static smu.capstone.common.errorcode.AuthExceptionCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignupService {

    private static final Long CERTIFICATION_KEY_EXPIRE_SECONDS = 10 * 60L;

    private final UserRepository userRepository;
    private final MailVerificationCacheRepository mailVerificationCacheRepository;

    private final PasswordEncoder passwordEncoder;

    private final AlarmService alarmService;

    @Transactional
    public void signup(AuthRequestDto.SignUp authRequestDto) {
        // 이메일 인증 처리
        MailVerificationCache mailVerificationCache = mailVerificationCacheRepository.findById(authRequestDto.getEmail())
                .orElseThrow(() -> new RestApiException(AUTHORIZATION_REQUIRED));

        if (!mailVerificationCache.getVerificationKey().equals(authRequestDto.getCertificationKey()) ||
            !mailVerificationCache.getIsVerify()) {
            throw new RestApiException(NOT_VERIFIED_MAIL);
        }

        UserEntity user;
        // 탈퇴한 정보가 남아있으면
        if (userRepository.existsByAccountIdAndIsDeleted(authRequestDto.getAccountId(), true)) {
            user = userRepository.findByAccountId(authRequestDto.getAccountId())
                    .orElseThrow(() -> new RestApiException(INVALID_ID_OR_PASSWORD));
            user.restore();
        } else if (userRepository.existsByAccountIdAndIsDeleted(authRequestDto.getAccountId(), false)) { // 이메일 중복
            throw new RestApiException(DUPLICATED_ID);
        } else {
            user = authRequestDto.toDto(passwordEncoder);
        }
        userRepository.save(user);
    }

    public void sendVerificationMail(AuthRequestDto.VerificationMail authRequestDto) {

        userRepository.findByEmailAndIsDeleted(authRequestDto.getEmail(), false).ifPresent((user) -> {
            throw new RestApiException(DUPLICATED_MAIL);
        });
        String key = CertificationKeyGenerator.generateStrongKey();
        alarmService.sendAuth(authRequestDto.getEmail(), EmailType.SIGNUP_CODE_MAIL, key);
    }

    public void verifyMail(AuthRequestDto.@Valid VerificationMail authRequestDto) {

        MailVerificationCache mailVerificationCache = mailVerificationCacheRepository.findById(authRequestDto.getEmail())
                .orElseThrow(() -> new RestApiException(INVALID_VERIFICATION_KEY));

        if (mailVerificationCache.getVerificationKey().equals(authRequestDto.getVerificationKey())) {
            mailVerificationCacheRepository.save(MailVerificationCache.builder()
                    .email(authRequestDto.getEmail())
                    .verificationKey(authRequestDto.getVerificationKey())
                    .isVerify(true)
                    .expiration(CERTIFICATION_KEY_EXPIRE_SECONDS)
                    .build());
        } else {
            throw new RestApiException(INVALID_VERIFICATION_KEY);
        }
    }

    public void dupAccountId(AuthRequestDto.DupAccountId authRequestDto) {

        userRepository.findByAccountId(authRequestDto.getAccountId())
                .ifPresent((user) -> {
                    throw new RestApiException(DUPLICATED_ID);
                });
    }
}
