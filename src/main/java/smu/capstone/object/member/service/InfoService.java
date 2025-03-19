package smu.capstone.object.member.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.object.member.domain.UserEntity;
import smu.capstone.object.member.dto.AuthRequestDto;
import smu.capstone.object.member.respository.UserRepository;
import smu.capstone.web.jwt.TokenProvider;
import smu.capstone.web.jwt.TokenService;

import static smu.capstone.common.errorcode.AuthExceptionCode.INVALID_ID_OR_PASSWORD;

@Service
@RequiredArgsConstructor
@Slf4j
public class InfoService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void modifyPassword(HttpServletRequest request, AuthRequestDto.Modify modifyDto) {

        //1. 검증 + userid 가져오기
        String accessToken = tokenProvider.getAccessToken(request);

        String userid = tokenService.getUserid(accessToken);
        log.info("userid={}", userid);
        userRepository.findByUserid(userid).ifPresentOrElse(user -> {
            user.setPassword(passwordEncoder.encode(modifyDto.getNewPassword()));
            userRepository.save(user);
        }, () -> {
            throw new RestApiException(INVALID_ID_OR_PASSWORD);
        });
    }

    public UserEntity getCurrentUser(HttpServletRequest request) {
        String accessToken = tokenProvider.getAccessToken(request);
        String userid = tokenService.getUserid(accessToken);
        UserEntity userEntity = userRepository.findByUserid(userid).orElseThrow(() -> new RestApiException(INVALID_ID_OR_PASSWORD));
        return userEntity;
    }
}
