package smu.capstone.domain.board.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import smu.capstone.domain.board.service.S3Service;

@RestController
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @GetMapping("/presigned-url")
    String getURL(@RequestParam String filename){
        var result = s3Service.createPresignedUrl("img/"+filename);
        return result;
    }
}
