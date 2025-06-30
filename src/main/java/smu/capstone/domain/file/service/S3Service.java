package smu.capstone.domain.file.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import smu.capstone.common.errorcode.CommonStatusCode;
import smu.capstone.common.exception.RestApiException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
/// TODO: 파일 확장자 가능한 것 확인
//        파일 바이러스 검사? -> api로 가져오는 걸로 아는데 할 것인지?
public class S3Service {

    private final S3Util s3Util;

    public Map<String, String> createUploadPresignedUrl(String prefix, String filename){
        //파일 확장자 검사
        if(prefix == null || filename == null){
            throw new RestApiException(CommonStatusCode.NOT_FOUND_PARAM_S3);
        }
        if(!validationFilename(prefix, filename)){
            throw new RestApiException(CommonStatusCode.INVALID_PARAM_S3);
        }
        return s3Util.createPutPresignedUrl(prefix, filename);
    }

    public String createDownloadUrl(String key){
        if(key == null){
            throw new RestApiException(CommonStatusCode.INVALID_PARAMETER);
        }
        return s3Util.createDownloadPresignedUrl(key);
    }

    public String createGetUrl(String key){
        if(key == null){
//            throw new RestApiException(CommonStatusCode.NOT_FOUND_IMG);
            return null;
        }
        if(!validationImgFile(key)){
            log.error("file error: 이미지만 가능 {}", key);
            throw new RestApiException(CommonStatusCode.INVALID_PARAMETER);
        }
        return s3Util.createGetPresignedUrl(key);
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
        if(extension.equals("exe")) {
            log.error("file error: 실행확장자는 허용되지 않음 {}", filename);
            return false;
        } else if("board".equals(prefix) || "profile".equals(prefix)) {
            return checkImgFile(extension);
        }
        return true;
    }

    private boolean validationImgFile(String filename){
        String extension = getFileExtension(filename);
        if(extension.equals("")){
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