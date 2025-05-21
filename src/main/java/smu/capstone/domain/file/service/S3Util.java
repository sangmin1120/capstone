package smu.capstone.domain.file.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Util {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final S3Presigner s3Presigner;

    /***
     * 파일 업로드 url 반환
     * @param prefix folder name
     * @param filename filename
     * @return put url
     */
    public Map<String,String> createPutPresignedUrl(String prefix, String filename){
        //Key(Path) 생성
        String key = createFilePath(prefix, filename);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        PutObjectPresignRequest putPresignedRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(3))
                .putObjectRequest(putObjectRequest)
                .build();
        String url = s3Presigner.presignPutObject(putPresignedRequest).url().toString();

        //URL과 key 반환
        Map<String, String> urlMap = new HashMap<>();
        urlMap.put("fileKey", key);
        urlMap.put("fileUrl", url);

        return urlMap;
    }

    /***
     *
     * @param key 미리 저장된 key(filePath) 사용
     * @return 30분동안 유효한 presigned Url 반환(이미지에 활용)
     */
    public String createGetPresignedUrl(String key){
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest getPresignedRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(30))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(getPresignedRequest).url().toString();
    }

    /***
     *
     * @param key 미리 저장된 key(filePath) 사용
     * @return 3분동안 유효한 다운로드 presigned Url 반환 (파일에 사용)
     */
    public String createDownloadPresignedUrl(String key){
        //한글 파일 encoding 문제로 설정
        String encodedFileName = URLEncoder.encode(key, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .responseContentDisposition("attachment; filename*=UTF-8''" + encodedFileName)
                .build();
        GetObjectPresignRequest downloadPresignedRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(3))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(downloadPresignedRequest).url().toString();
    }

    /***
     * 파일 경로 생성 메서드, Path를 key로 사용함
     * @param prefix - bucket의 folder 이름(도메인 별 관리)
     * @param filename - 원본 파일 이름
     */
    /// TODO: 추후 회의로 날짜별 분류 할 건지 결정
    //        UUID filename 생성을 client 측에서 할 건지 확인
    private String createFilePath(String prefix, String filename){
        String filePath = UUID.randomUUID() + filename;
        return String.format("%s/%s", prefix, filePath);
    }
}
