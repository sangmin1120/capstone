package smu.capstone.object.member.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.object.member.domain.UserEntity;
import smu.capstone.object.member.dto.AuthRequestDto;
import smu.capstone.object.member.respository.UserRepository;
import smu.capstone.object.member.service.EmailService.EmailType;
import smu.capstone.web.jwt.redisdomain.MailVerificationCache;
import smu.capstone.web.jwt.redisrepository.MailVerificationCacheRepository;

import static smu.capstone.common.errorcode.AuthExceptionCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignupService {

    private static final Long CERTIFICATION_KEY_EXPIRE_SECONDS = 10 * 60L;

    private final UserRepository userRepository;
    private final MailVerificationCacheRepository mailVerificationCacheRepository;

    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    @Transactional
    public void signup(AuthRequestDto.SignUp authRequestDto) {
        if (userRepository.existsByAccountId(authRequestDto.getAccountId())) {
            throw new RestApiException(DUPLICATED_ID);
        }

        MailVerificationCache mailVerificationCache = mailVerificationCacheRepository.findById(authRequestDto.getEmail())
                .orElseThrow(() -> new RestApiException(AUTHORIZATION_REQUIRED));

        if (!mailVerificationCache.getVerificationKey().equals(authRequestDto.getCertificationKey()) ||
            !mailVerificationCache.getIsVerify()) {
            throw new RestApiException(NOT_VERIFIED_MAIL);
        }

        UserEntity user = authRequestDto.toDto(passwordEncoder);
        userRepository.save(user);
    }

    public void sendVerificationMail(AuthRequestDto.VerificationMail authRequestDto) {

        userRepository.findByEmail(authRequestDto.getEmail()).ifPresent((user) -> {
            throw new RestApiException(DUPLICATED_MAIL);
        });
        String certificationKey = emailService.sendCertificationKey(authRequestDto.getEmail(), EmailType.SIGNUP_CODE_MAIL);
        mailVerificationCacheRepository.save(MailVerificationCache.builder()
                .email(authRequestDto.getEmail())
                .verificationKey(certificationKey)
                .isVerify(false)
                .expiration(CERTIFICATION_KEY_EXPIRE_SECONDS)
                .build());
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
}
