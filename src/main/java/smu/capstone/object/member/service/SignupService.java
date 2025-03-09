package smu.capstone.object.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.object.member.dto.AuthRequestDto;
import smu.capstone.object.member.domain.UserEntity;
import smu.capstone.object.member.respository.UserRepository;

@Service
@RequiredArgsConstructor
public class SignupService {
    private static final Long CERTIFICATION_KEY_EXPIRE_SECONDS = 10 * 60L;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    @Transactional
    public void signup(AuthRequestDto.SignUp authRequestDto) {
        UserEntity user = authRequestDto.toDto(passwordEncoder);

        userRepository.save(user);
    }
}
