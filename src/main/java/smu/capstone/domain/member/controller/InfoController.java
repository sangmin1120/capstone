package smu.capstone.domain.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.capstone.common.response.BaseResponse;
import smu.capstone.domain.member.dto.AuthRequestDto;
import smu.capstone.domain.member.service.InfoService;

@RestController
@RequestMapping("/api/user-info")
@RequiredArgsConstructor
@Slf4j
public class InfoController {

    private final InfoService infoService;

    // 비밀번호 변경
    @PostMapping("/password-modify")
    public BaseResponse<Void> modifyPassword(@RequestBody AuthRequestDto.Modify modifyDto) {
        log.info("Modify password");
        infoService.changePassword(modifyDto);

        return BaseResponse.ok();
    }

    // 내 정보 반환
    @PostMapping("/my-info")
    public BaseResponse<AuthRequestDto.UserInfo> myInfo() {
        AuthRequestDto.UserInfo userInfo = new AuthRequestDto.UserInfo();
        return BaseResponse.ok(userInfo.toInfo(infoService.getCurrentUser()));
    }
}
