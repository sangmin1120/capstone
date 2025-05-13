package smu.capstone.intrastructure.fcm.controller;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.capstone.common.response.BaseResponse;
import smu.capstone.intrastructure.fcm.service.TestService;

import java.security.Principal;

import static smu.capstone.domain.member.util.LoginUserUtil.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm/test")
public class TestController {

    private final TestService testService;

    @GetMapping("/alarm")
    public BaseResponse<Void> alarmTest(Principal principal) {
        val memberId = getLoginMemberId();
        testService.pushTest(memberId);
        return BaseResponse.ok();
    }

}
