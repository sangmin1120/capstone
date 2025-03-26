package smu.capstone.object.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.capstone.common.response.BaseResponse;
import smu.capstone.object.member.dto.AuthRequestDto;
import smu.capstone.object.member.dto.UserSearchDto;
import smu.capstone.object.member.service.UserSearchService;

@RestController
@RequestMapping("/api/user-search")
@RequiredArgsConstructor
@Slf4j
public class UserSearchController {

    private final UserSearchService userSearchService;

    //아이디 찾기
    @PostMapping("/search-id")
    public BaseResponse<String> searchAccountId(@RequestBody @Valid UserSearchDto.SearchIdRequest searchIdRequest) {

        log.info("Searching user with email {}", searchIdRequest.getEmail());
        return BaseResponse.ok(userSearchService.searchId(searchIdRequest));
    }

    //비밀번호 변경을 위한 인증 메일 전송
    @PostMapping("/send-verification-mail")
    public BaseResponse<Void> sendVerificationMail(@RequestBody @Valid AuthRequestDto.VerificationMail authRequestDto) {
        userSearchService.sendVerificationMail(authRequestDto);

        return BaseResponse.ok();
    }

    //인증 번호 확인 -> 새로운 비밀번호 전송 후, 비밀번호 변경
    @PostMapping("/send-new-password-mail")
    public BaseResponse<Void> verifyMailAndSendNewPassword(@RequestBody @Valid AuthRequestDto.VerificationMail authRequestDto) {
        userSearchService.verifyMailAndSendNewPassword(authRequestDto);
        return BaseResponse.ok();
    }
}
