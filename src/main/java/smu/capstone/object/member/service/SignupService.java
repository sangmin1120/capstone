package smu.capstone.object.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.object.member.dto.AuthRequestDto;
import smu.capstone.object.member.domain.UserEntity;
import smu.capstone.object.member.respository.UserRepository;

import static smu.capstone.common.errorcode.AuthExceptionCode.*;

@Service
@RequiredArgsConstructor
public class SignupService {
    private static final Long CERTIFICATION_KEY_EXPIRE_SECONDS = 10 * 60L;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    @Transactional
    public void signup(AuthRequestDto.SignUp authRequestDto) {

        if (userRepository.existsByUserid(authRequestDto.getUserid())) {
            throw new RestApiException(DUPLICATED_ID);
        }

        UserEntity user = authRequestDto.toDto(passwordEncoder);

        userRepository.save(user);
    }
}
