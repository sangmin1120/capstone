package smu.capstone.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.dto.AuthRequestDto;
import smu.capstone.domain.member.respository.UserRepository;
import smu.capstone.domain.member.util.LoginUserUtil;

import static smu.capstone.common.errorcode.CommonStatusCode.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
@Slf4j
public class InfoService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 비밀번호 변경
    @Transactional
    public void changePassword(AuthRequestDto.Modify modifyDto) {

        //1. 검증 + userid 가져오기
        UserEntity userEntity = getCurrentUser();
        userEntity.setPassword(passwordEncoder.encode(modifyDto.getNewPassword()));
        userRepository.save(userEntity);
    }

    // 현재 유저 정보
    public UserEntity getCurrentUser() {
        Long userId = LoginUserUtil.getLoginMemberId();
        return userRepository.findById(userId).orElseThrow(()-> new RestApiException(NOT_FOUND_USER));
    }
}
