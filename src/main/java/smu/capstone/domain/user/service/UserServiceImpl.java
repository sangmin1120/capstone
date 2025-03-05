package smu.capstone.domain.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import smu.capstone.domain.user.dto.UserLoginRequestDto;
import smu.capstone.domain.user.dto.UserSignUpRequestDto;
import smu.capstone.domain.user.entity.UserEntity;
import smu.capstone.domain.user.respository.UserRepository;
import smu.capstone.jwt.JwtToken;
import smu.capstone.jwt.JwtTokenProvider;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Transactional
    @Override
    public void signUp(UserSignUpRequestDto requestDto) {

        Optional<UserEntity> foundMember = userRepository.findByEmail(requestDto.getEmail());
        if (foundMember.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 회원 입니다.");
        }
        //password encode
        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        UserEntity member = requestDto.toEntity();
        log.info("member={} {} {}", member.getUsername(), member.getEmail(), member.getCreatedAt());
        userRepository.save(member);
    }

    @Override
    public JwtToken login(UserLoginRequestDto requestDto) {

        //1. username(email) + password 기반 Authentication 객체 생성
        //authentication 은 인증 여부 확인하는 authenticated 값 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword());

        //2. 실제 검증. authenticated() 메서드를 통해 요청된 User 대한 검증 진행
        //authenticate 메서드 실행시, CustomUserDetailsService 만든 loadUserByUsername 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        //3. 인증 정보 기반으로 JWT 토큰 생성
        return jwtTokenProvider.generateToken(authentication);
    }
}
