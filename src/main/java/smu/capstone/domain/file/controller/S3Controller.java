package smu.capstone.domain.file.controller;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import smu.capstone.common.errorcode.CommonStatusCode;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.common.response.BaseResponse;
import smu.capstone.domain.file.dto.UrlResponseDto;
import smu.capstone.domain.file.service.CloudFrontUtil;
import smu.capstone.domain.file.service.S3Service;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class S3Controller {

    private final S3Service s3Service;
    private final CloudFrontUtil cloudFrontUtil;

    ///file을 domain별 관리할지 별도 관리할지 확인필요
    /// board와 profile의 경우 별도 key 저장 필요 없으며 url로 접근 가능
    //board와 profile 업로드
    @GetMapping("/upload-url/{type}")
    public BaseResponse<UrlResponseDto> getPresignedUploadUrl(@PathVariable String type, @RequestParam String filename) {
        return BaseResponse.ok(s3Service.createUploadPresignedUrl(type, filename));
    }

    //채팅 관련 업로드
    @GetMapping("/upload-url/chat/{roomId}")
    public BaseResponse<UrlResponseDto> getPresignedUploadChatUrl(@PathVariable String roomId, @RequestParam String filename) {
        return BaseResponse.ok(s3Service.createUploadChatPresignedUrl(filename, roomId));
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

    //TODO: 다운로드 말고 그냥 얻는 것도 만들기
    @GetMapping("/download-signedurl")
    public BaseResponse<String> getSignedDownloadUrl(@RequestParam String fileKey){
        String url = null;
        try {
            url = cloudFrontUtil.createDownloadSignedUrl(fileKey);
        }catch (Exception e){
            log.error("다운로드 에러 {} {} {}", e.getCause(), e.getMessage(), e.getStackTrace());
            throw new RestApiException(CommonStatusCode.INTERNAL_SERVER_ERROR);
        }
        return BaseResponse.ok(url);
    }

    @GetMapping("/get-signedurl")
    public BaseResponse<String> getSignedUrl(@RequestParam String fileKey){
        String url = null;
        try{
            url = cloudFrontUtil.createGetSignedUrl(fileKey);
        }catch (Exception e){
            log.error("다운로드 에러 {} {} {}", e.getCause(), e.getMessage(), e.getStackTrace());
            throw new RestApiException(CommonStatusCode.INTERNAL_SERVER_ERROR);
        }
        return BaseResponse.ok(url);
    }
}