package smu.capstone.object.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.object.member.domain.UserEntity;
import smu.capstone.object.member.dto.AuthRequestDto;
import smu.capstone.object.member.respository.UserRepository;
import smu.capstone.object.member.util.LoginUserUtil;

import static smu.capstone.common.errorcode.CommonStatusCode.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
@Slf4j
public class InfoService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void changePassword(AuthRequestDto.Modify modifyDto) {

        //1. 검증 + userid 가져오기
        UserEntity userEntity = getCurrentUser();
        userEntity.setPassword(passwordEncoder.encode(modifyDto.getNewPassword()));
        userRepository.save(userEntity);
    }

    public UserEntity getCurrentUser() {
        Long userId = LoginUserUtil.getLoginMemberId();
        return userRepository.findById(userId).orElseThrow(()-> new RestApiException(NOT_FOUND_USER));
    }
}
