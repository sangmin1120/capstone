package smu.capstone.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import smu.capstone.domain.member.dto.SignInDto;
import smu.capstone.domain.member.dto.SignUpDto;
import smu.capstone.domain.member.service.UserService;

/**
 * user 관한 RestController
 * todo - 회원가입 로직, 아이디 검색, 비밀번호 찾기 등등
 */
@RequiredArgsConstructor
@RestController
@RequestMapping
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * 회원가입 - 검증, 로직 미완성
     *
     * @param requestDto: DTO 형태로 받아와서 검증
     * @return: 성공하면 true, 실패하면 false
     */
    @PostMapping("/join")
    public String signUp(@Valid @RequestBody SignUpDto requestDto) {
        if (requestDto == null) {
//            log.info("requestDto null");
            return "request body is missing";
        }
//        log.info("username={}, email={}, userType={}", requestDto.getUsername(), requestDto.getEmail(), requestDto.getUserType());
        Boolean join = userService.join(requestDto);

        return join.toString();
    }

    /**
     * 로그인- 필터에서 로그인 검증 (exception 처리로 실패 확인)
     * @param requestDto: DTO 형태
     * @return: 성공하면 true, 실패하면 exception 처리
     */
    @PostMapping("/login")
    public String login(@Valid @RequestBody SignInDto requestDto) {
        userService.login(requestDto);
        return "true";
    }

    /**
     * test- ROLE_ADMIN 만 허락
     * @return: 단순 ok
     */
    @GetMapping("/admin")
    public String admin() {
        log.info("admin call");
        return "ok";
    }


}
