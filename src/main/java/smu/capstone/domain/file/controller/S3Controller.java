package smu.capstone.domain.file.controller;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import smu.capstone.common.errorcode.CommonStatusCode;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.common.response.BaseResponse;
import smu.capstone.domain.file.dto.UrlResponseDto;
import smu.capstone.domain.file.service.S3Service;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class S3Controller {

    private final S3Service s3Service;

    ///file을 domain별 관리할지 별도 관리할지 확인필요
    /// board와 profile의 경우 별도 key 저장 필요 없으며 url로 접근 가능
    @GetMapping("/upload-url/{type}")
    public BaseResponse<UrlResponseDto> getPresignedUploadUrl(@PathVariable String type, @RequestParam String filename) {
        return BaseResponse.ok(s3Service.createUploadPresignedUrl(type, filename));
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