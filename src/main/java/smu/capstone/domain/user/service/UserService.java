package smu.capstone.domain.user.service;

import smu.capstone.domain.user.dto.UserLoginRequestDto;
import smu.capstone.domain.user.dto.UserSignUpRequestDto;
import smu.capstone.jwt.JwtToken;

import java.util.Map;

public interface UserService {

    //회원가입
    public void signUp(UserSignUpRequestDto requestDto) throws Exception;
    //로그인
    public JwtToken login(UserLoginRequestDto requestDto) throws Exception;
}
