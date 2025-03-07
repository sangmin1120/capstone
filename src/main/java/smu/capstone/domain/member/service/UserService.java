package smu.capstone.domain.member.service;

import smu.capstone.domain.member.dto.SignInDto;
import smu.capstone.domain.member.dto.SignUpDto;

public interface UserService {

    //회원가입
    Boolean join(SignUpDto requestDto);

    //로그인
    void login(SignInDto requestDto);
}
