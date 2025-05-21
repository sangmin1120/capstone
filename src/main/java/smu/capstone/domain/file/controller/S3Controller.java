package smu.capstone.domain.file.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import smu.capstone.common.response.BaseResponse;
import smu.capstone.domain.file.service.S3Service;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class S3Controller {

    private final S3Service s3Service;

    ///file을 domain별 관리할지 별도 관리할지 확인필요
    /// board와 profile의 경우 별도 key 저장 필요 없으며 url로 접근 가능
    @GetMapping("/upload-url")
    public BaseResponse<Map<String, String>> getPresignedUploadUrl(@RequestParam String prefix, @RequestParam String filename) {
        return BaseResponse.ok(s3Service.createUploadPresignedUrl(prefix, filename));
    }

    //파일 접근 api
    @GetMapping("/get-url")
    public BaseResponse<String> getPresignedUrl(@RequestParam String fileKey) {
        String url = s3Service.createGetUrl(fileKey);
        return BaseResponse.ok(url);
    }

    //파일 다운로드 api(header 오버라이드)
    @GetMapping("/download-url")
    public BaseResponse<String> getPresignedDownloadUrl(@RequestParam String fileKey){
        String url = s3Service.createDownloadUrl(fileKey);
        return BaseResponse.ok(url);
    }
}