package smu.capstone.domain.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import smu.capstone.common.response.BaseResponse;
import smu.capstone.domain.member.dto.AuthRequestDto;
import smu.capstone.domain.member.service.InfoService;

import java.util.Map;

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
    
    // 회원 탈퇴 - 프론트 단에서 refresh 토큰 값 삭제, 백엔드 단에서 user DB 삭제
    @PostMapping("/delete")
    public BaseResponse<Void> userDelete(HttpServletRequest request) {
        infoService.delete(request);
        return BaseResponse.ok();
    }

    @PostMapping("/upload-profileImg")
    public BaseResponse<Map<String,String>> uploadProfile(@RequestBody AuthRequestDto.ProfileFile profileFile) {
        Map<String, String> profileFileMap = infoService.uploadProfileFile(profileFile);

        return BaseResponse.ok(profileFileMap); // signedUrl, key 반환
    }

    @GetMapping("/get-profileImg")
    public BaseResponse<String> getProfileImg() {
        String profileImgUrl = infoService.getProfileImg();

        return BaseResponse.ok(profileImgUrl); // signedUrl 반환
    }
}
