package smu.capstone.web.jwt.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.capstone.web.jwt.service.ReissueService;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final ReissueService reissueService;

    // refresh 이용해 access token, refresh token 갱신
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        String result = reissueService.reissueRefresh(request, response);

        if (!result.equals("OK")) {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
