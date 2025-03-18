package smu.capstone.object.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.capstone.common.response.BaseResponse;
import smu.capstone.object.member.dto.AuthRequestDto;
import smu.capstone.object.member.service.InfoService;

@RestController
@RequestMapping("/api/user-info")
@RequiredArgsConstructor
@Slf4j
public class InfoController {

    private final InfoService infoService;

    // 비밀번호 변경
    @PostMapping("/password-modify")
    public BaseResponse<Void> modifyPassword(HttpServletRequest request, @RequestBody AuthRequestDto.Modify modifyDto) {
        log.info("Modify password");
        infoService.modifyPassword(request, modifyDto);

        return BaseResponse.ok();
    }

    //
}
