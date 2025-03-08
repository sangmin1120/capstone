package smu.capstone.domain.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import smu.capstone.domain.member.dto.SignInDto;
import smu.capstone.domain.member.dto.SignUpDto;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.respository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean isExistEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 검증 후 회원가입
     * @param requestDto: DTO 형태
     * @return: 성공하면 treu, 실패하면 false
     */
    @Transactional
    @Override
    public Boolean join(SignUpDto requestDto) {

        String email = requestDto.getEmail();
        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        //검증
        Boolean isExist = userRepository.existsByEmail(email);
        if (isExist) {
            return false;
        }

        //저장
        UserEntity data = UserEntity.toEntity(requestDto);
        userRepository.save(data);
        return true;
    }

    /**
     * 필터에서 로그인 검증 처리 - service 에서는?
     * @param requestDto: DTO 형태
     */
    @Transactional
    @Override
    public void login(SignInDto requestDto) {

    }
}
