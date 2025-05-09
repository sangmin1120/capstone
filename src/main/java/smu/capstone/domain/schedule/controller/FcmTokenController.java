package smu.capstone.domain.schedule.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.service.InfoService;
import smu.capstone.domain.schedule.service.FcmTokenService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FcmTokenController {

    private final FcmTokenService fcmTokenService;
    private final InfoService infoService;

    @PostMapping("/fcm-token")
    public ResponseEntity<Void> saveToken(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String token = body.get("token");
        UserEntity user = infoService.getCurrentUser();
        fcmTokenService.saveToken(user, token);
        return ResponseEntity.ok().build();
    }

}

