package smu.capstone.domain.user.service;

import smu.capstone.domain.user.dto.UserLoginRequestDto;
import smu.capstone.domain.user.dto.UserSignUpRequestDto;
import smu.capstone.jwt.JwtToken;

public interface UserService {

    //회원가입
    void signUp(UserSignUpRequestDto requestDto) throws Exception;

    //로그인
    JwtToken login(UserLoginRequestDto requestDto) throws Exception;
}
