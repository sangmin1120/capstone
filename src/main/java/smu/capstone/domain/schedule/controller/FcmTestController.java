package smu.capstone.domain.schedule.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FcmTestController {
    @GetMapping("/fcm-test")
    public String fcmTestPage() {
        return "fcm-test"; // templates/fcm-testv.html
    }
    @GetMapping("/testvv")
    public String testPage() {
        return "testv";
    }
}
