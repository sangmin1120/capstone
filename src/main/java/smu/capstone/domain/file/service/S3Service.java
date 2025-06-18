package smu.capstone.domain.file.service;

import io.awspring.cloud.s3.S3Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import smu.capstone.common.errorcode.CommonStatusCode;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.domain.file.dto.UrlResponseDto;
import software.amazon.awssdk.core.exception.SdkClientException;

@Slf4j
@RequiredArgsConstructor
@Service
/// TODO: 에러 세분화
public class S3Service {

    private final S3Util s3Util;

    public UrlResponseDto createUploadPresignedUrl(String type, String filename) {
        String prefix = switch (type){
            case "board" -> "board";
            case "profile" -> "profile";
            default -> throw new RestApiException(CommonStatusCode.INVALID_PARAMETER);
        };

        //파일 확장자 검사
        if(filename == null){
            throw new RestApiException(CommonStatusCode.INVALID_PARAMETER);
        }
        if(!validationFilename(prefix, filename)){
            throw new RestApiException(CommonStatusCode.INVALID_PARAMETER);
        }
        try {
            return s3Util.createPutPresignedUrl(prefix, filename);
        } catch (Exception e) {
            log.error("서버 내부 에러: {} {} {}", e.getCause(), e.getMessage(), e.getStackTrace());
            throw new RestApiException(CommonStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    public UrlResponseDto createUploadChatPresignedUrl(String filename, String roomId) {
        String type = "chat";

        if(roomId == null || filename == null){
            log.warn("잘못된 파라미터 roomId: {}, filename: {}", roomId, filename);
            throw new RestApiException(CommonStatusCode.INVALID_PARAMETER);
        }
        if(!validationFilename(type, filename)){
            log.warn("잘못된 파일 이름 type:{}, filename: {}", type, filename);
            throw new RestApiException(CommonStatusCode.INVALID_PARAMETER);
        }
        try {
            String prefix = String.format("%s/%s", type, roomId);
            return s3Util.createPutPresignedUrl(prefix, filename);
        }catch (Exception e){
            log.error("서버 내부 에러: {} {} {}", e.getCause(), e.getMessage(), e.getStackTrace());
            throw new RestApiException(CommonStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    public String createDownloadUrl(String key){
        if(key == null){
            throw new RestApiException(CommonStatusCode.INVALID_PARAMETER);
        }
        try {
            return s3Util.createDownloadPresignedUrl(key);
        } catch (Exception e) {
            log.error("서버 내부 에러: {} {} {}", e.getCause(), e.getMessage(), e.getStackTrace());
            throw new RestApiException(CommonStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    public String createGetUrl(String key){
        if(key == null){
            throw new RestApiException(CommonStatusCode.INVALID_PARAMETER);
        }
        if(!validationImgFile(key)){
            log.error("file error: 이미지만 가능 {}", key);
            throw new RestApiException(CommonStatusCode.INVALID_PARAMETER);
        }
        try {
            return s3Util.createGetPresignedUrl(key);
        }catch (Exception e){
            log.error("서버 내부 에러: {} {} {}", e.getCause(), e.getMessage(), e.getStackTrace());
            throw new RestApiException(CommonStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    private String getFileExtension(String filename){
        int dotIdx = filename.lastIndexOf('.');
        if(dotIdx > 0)
            return filename.substring(dotIdx+1);
        return "";
    }

    //윈도우 실행확장자 외 모두 허용(board와 profile prefix의 경우 이미지만 허용)
    private boolean validationFilename(String prefix, String filename){
        String extension = getFileExtension(filename);
        if(extension.isBlank()){
            return false;
        }
        if(extension.equals("exe")) {
            log.error("file error: 실행확장자는 허용되지 않음 {}", filename);
            return false;
        } else if("board".equals(prefix) || "profile".equals(prefix)) {
            return checkImgFile(extension);
        }
        return true;
    }

    //삭제 시도 실패해도 클라이언트에게는 삭제된 걸로 보여야함. log만 남기기
    public void deleteObject(String filename){
        if(filename == null || filename.isEmpty()){
            log.warn("FILE 이름이 유효하지 않습니다. : {}", filename);
            return;
        }
        try {
            s3Util.deleteObject(filename);
        }catch (IllegalArgumentException e){
            log.warn("파일 삭제 실패: 잘못된 URL이나 Key입니다. {}", e.getMessage());
        }catch (S3Exception e){
            log.warn("파일 삭제 실패: S3 요청 실패 {}", e.getMessage());
        }catch (SdkClientException e){
            log.warn("파일 삭제 실패: 잘못된 클라이언트 환경 {}", e.getMessage());
        }catch (Exception e){
            log.warn("파일 삭제 실패: 기타 오류 발생 {}, {}",e.getCause(), e.getMessage());
        }
    }

    /***
     *  채팅방의 메시지 삭제 시 객체 삭제하는 메서드
     *  prefix: roomId
     */
    public void deleteChatObjects(String roomId){
       try {
           if(roomId==null || roomId.isBlank()){
               log.info("잘못된 roomId: {}", roomId);
               throw new RestApiException(CommonStatusCode.INVALID_PARAMETER);
           }
           s3Util.deleteObjects("chat/"+roomId+"/");
       }catch (S3Exception e){
           log.warn("파일 삭제 실패: S3 요청 실패 {}", e.getMessage());

       }catch (SdkClientException e){
           log.warn("파일 삭제 실패: 잘못된 클라이언트 환경 {}", e.getMessage());
       }catch (Exception e){
           log.warn("파일 삭제 실패: 기타 오류 발생 {}, {}",e.getCause(), e.getMessage());
       }
    }

    private boolean validationImgFile(String filename){
        String extension = getFileExtension(filename);
        if(extension.isEmpty()){
            return false;
        }
        return checkImgFile(extension);
    }

    private boolean checkImgFile(String extension){
        String[] imageExtensions = { "jpg", "jpeg", "png", "gif", "bmp", "webp" };
        for(String imageExtension : imageExtensions){
            if(extension.equals(imageExtension))
                return true;
        }
        return false;
    }
}