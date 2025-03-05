package smu.capstone.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smu.capstone.domain.user.dto.UserLoginRequestDto;
import smu.capstone.domain.user.dto.UserSignUpRequestDto;
import smu.capstone.domain.user.service.UserServiceImpl;
import smu.capstone.jwt.JwtToken;

/**
 * user 관한 RestController
 * todo - 회원가입 로직, 아이디 검색, 비밀번호 찾기 등등
 */
@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
@Slf4j
public class UserController {

    private final UserServiceImpl userService;

    /**
     * 회원가입 - 검증, 로직 미완성
     *
     * @param requestDto: DTO 형태로 받아와서 검증
     * @return: 성공하면 ok, 실패하면 error
     */
    @PostMapping("/signUp")
    public String signUp(@Valid @RequestBody UserSignUpRequestDto requestDto) {
        userService.signUp(requestDto);
        return "signUp successful";
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody UserLoginRequestDto requestDto) {
        JwtToken result = userService.login(requestDto);
        if (result==null) {
            return "error"; //exception 처리
        }
        return "login successful";
    }
}
