package smu.capstone.domain.member.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.capstone.common.response.BaseResponse;

@RestController
public class BasicController {

    @GetMapping("/admin")
    public BaseResponse<Void> admin() {
        return BaseResponse.ok();
    }

    @GetMapping("/hi")
    public BaseResponse<Void> hi() {
        return BaseResponse.ok();
    }
}
