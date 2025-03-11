package smu.capstone.object.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.capstone.common.response.BaseResponse;
import smu.capstone.object.member.dto.AuthRequestDto;
import smu.capstone.object.member.dto.TokenResponseDto;
import smu.capstone.object.member.service.LoginService;
import smu.capstone.object.member.service.SignupService;
import smu.capstone.web.jwt.TokenService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
//@PreAuthorize("permitAll()")
public class AuthController {
    private final SignupService signupService;
    private final LoginService loginService;
    private final TokenService tokenService;

    @PostMapping("/signup")
    public BaseResponse<Void> signup(@RequestBody AuthRequestDto.SignUp authRequestDto) {
        signupService.signup(authRequestDto);
        return BaseResponse.ok();
    }

    @PostMapping("/login")
    public BaseResponse<TokenResponseDto> login(HttpServletResponse response, @Valid @RequestBody AuthRequestDto.Login authRequestDto) {
        return BaseResponse.ok(loginService.login(response, authRequestDto));
    }

    @PostMapping("/refresh")
    public BaseResponse<String> refreshAccessToken(HttpServletRequest request) {
        return BaseResponse.ok(tokenService.refreshAccessToken(request));
    }

    @PostMapping("/delete-refresh-token")
    public BaseResponse<Void> deleteRefreshToken(HttpServletRequest request) {
        tokenService.deleteRefreshToken(request);
        return BaseResponse.ok();
    }
}
