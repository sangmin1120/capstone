package smu.capstone.domain.member.service;

import jakarta.servlet.http.HttpServletRequest;
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
import smu.capstone.intrastructure.jwt.service.TokenProvider;
import smu.capstone.intrastructure.jwt.service.TokenService;

import static smu.capstone.common.errorcode.CommonStatusCode.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
@Slf4j
public class InfoService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final TokenProvider tokenProvider;

    // 비밀번호 변경
    @Transactional
    public void changePassword(AuthRequestDto.Modify modifyDto) {

        //1. 검증 + userid 가져오기
        UserEntity userEntity = getCurrentUser();
        if (!passwordEncoder.matches(modifyDto.getCurrentPassword(), userEntity.getPassword())) {
            throw new RestApiException(NOT_FOUND_USER);
        }

        userEntity.setPassword(passwordEncoder.encode(modifyDto.getNewPassword()));
        userRepository.save(userEntity);
    }

    // 현재 유저 정보
    public UserEntity getCurrentUser() {
        Long userId = LoginUserUtil.getLoginMemberId();
        return userRepository.findById(userId).orElseThrow(()-> new RestApiException(NOT_FOUND_USER));
    }

    // 회원 탈퇴
    public void delete(HttpServletRequest request) {
        UserEntity user = getCurrentUser();

        // entity isDelete 값을 true 로 변경 -> 실제 DB 삭제는 안함
        user.withdraw();
        userRepository.save(user);
        // 토큰값 블랙리스트
        tokenService.blacklistAccessToken(tokenProvider.getAccessToken(request));
    }
}
