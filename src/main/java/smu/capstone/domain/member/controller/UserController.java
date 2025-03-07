package smu.capstone.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
     * @return: 성공하면 ok, 실패하면 error
     */
    @PostMapping("/join")
    public String signUp(@Valid @RequestBody SignUpDto requestDto) {
        if (requestDto==null) {
            log.info("requestDto null");
            return "request body is missing";
        }
        log.info("username={}, email={}, userType={}", requestDto.getUsername(), requestDto.getEmail(), requestDto.getUserType());
        Boolean join = userService.join(requestDto);

        return join.toString();
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody SignInDto requestDto) {
        userService.login(requestDto);
        return "ok";
    }

    @GetMapping("/admin")
    public String admin() {
        log.info("admin call");
        return "ok";
    }

    @GetMapping("/")
    public String mainp() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        return "UserController" + name;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("🚨 Validation error: {}", ex.getMessage());
        return ResponseEntity.badRequest().body("Invalid input data");
    }
}
