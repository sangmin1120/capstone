package smu.capstone.domain.file.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;
import software.amazon.awssdk.services.cloudfront.url.SignedUrl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Component
public class CloudFrontUtil {

    //TODO:코드 검토 및 docker 설정 - docker 설정 완료, 코드 검토 필요: 변수 이름 수정
    @Value("${cloud.aws.s3.baseurl}")
    private String baseUrl;
    @Value("${aws.cloudfront.privatekey-path}")
    private String privateKeyPath;
    @Value("${aws.cloudfront.keypair-id}")
    private String keyPairId;

    public String createGetSignedUrl(String key) throws Exception{
        String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20")
                .replaceAll("%2F", "/");

        CloudFrontUtilities cloudFrontUtilities = CloudFrontUtilities.create();
        //UTC 기반 Duration 설정
        Instant expiration = Instant.now().plus(Duration.ofMinutes(15));
        CannedSignerRequest cannedSignerRequest = CannedSignerRequest.builder()
                .keyPairId(keyPairId)
                .expirationDate(expiration)
                .privateKey(new java.io.File(privateKeyPath).toPath())
                .resourceUrl(baseUrl+"/"+encodedKey)
                .build();
        SignedUrl signedUrl = cloudFrontUtilities.getSignedUrlWithCannedPolicy(cannedSignerRequest);
        log.info("Signed url: {}", signedUrl);
        return signedUrl.url();
    }
    //TODO: 가독성 있게 코드 수정
    public String createDownloadSignedUrl(String filekey) throws Exception{
        //key를 모두 인코딩
        String encodedFilename = URLEncoder.encode(filekey, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20")
                .replaceAll("%2F", "/");

        //파일명 추출
        String[] part = encodedFilename.split("/");
        String filename = part[part.length - 1];

        //s3인코딩 문제를 위해 2번 인코딩 처리
        String dispostion = URLEncoder.encode("attachment; filename="+filename, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        log.info("filename: {}", filename);
        log.info("Download url: {}", filekey);
        log.info("encode filename: {}", encodedFilename);
        log.info("{}",new java.io.File(privateKeyPath).toPath());

        CloudFrontUtilities cloudFrontUtilities = CloudFrontUtilities.create();

        //UTC 기반 Duration 설정
        Instant expiration = Instant.now().plus(Duration.ofMinutes(15));

        CannedSignerRequest cannedSignerRequest = CannedSignerRequest.builder()
                .keyPairId(keyPairId)
                .expirationDate(expiration)
                .privateKey(new java.io.File(privateKeyPath).toPath())
                .resourceUrl(baseUrl+"/"+encodedFilename+"?response-content-disposition="+dispostion)
                .build();
        SignedUrl signedUrl = cloudFrontUtilities.getSignedUrlWithCannedPolicy(cannedSignerRequest);
        log.info("Signed url: {}", signedUrl);
        return signedUrl.url();
    }
}
