package smu.capstone.domain.file.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import smu.capstone.domain.file.dto.UrlResponseDto;
import smu.capstone.domain.file.entity.S3FailedFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
/// 최대 5GB 올릴 수 있음. 추후 필요하다면 multipart 업로드/다운로드/삭제 구현
/// TODO: 웹 도메인으로 url 치환 필요
@Slf4j
@RequiredArgsConstructor
@Component
public class S3Util {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.s3.baseurl}")
    private String baseUrl;
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;
    private final S3FailedUtil s3FailedUtil;
    /***
     * 파일 업로드 url 반환
     * @param prefix folder name
     * @param filename filename
     * @return put url
     */
    public UrlResponseDto createPutPresignedUrl(String prefix, String filename){
        //Key(Path) 생성
        String key = createFilePath(prefix, filename);
        //한글 파일 이름을 utf-8로 인코딩, url 생성 key에는 encoding한 key를 넣지 않음
        String encodedKey = Arrays.stream(key.split("/"))   // "/"를 기준으로 key를 배열화
                .map(part -> URLEncoder.encode(part, StandardCharsets.UTF_8).replace("+", "%20"))
                .collect(Collectors.joining("/"));        //string 조합 시 "/"를 다시 넣어 둠

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
        UrlResponseDto urlResponseDto = new UrlResponseDto().builder()
                .presignedUrl(url)
                .accessUrl(String.format("%s/%s",baseUrl, encodedKey))
                .key(key)
                .build();
        return urlResponseDto;
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
        //다운 시에도 UTF-8로 Key 변환 필요
        String encodedFilename = URLEncoder.encode(extractFilename(key), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .responseContentDisposition("attachment; filename*=UTF-8''" + encodedFilename)
                .build();
        GetObjectPresignRequest downloadPresignedRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(3))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(downloadPresignedRequest).url().toString();
    }

    public DeleteObjectResponse deleteObject(String filename){
        //해당 값이 URL 값이라면 key 값 추출 후 decoding
        if(filename.contains(baseUrl)){
            filename = extractKey(filename);
        }
        String key = filename;

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .key(key)
                        .bucket(bucket)
                        .build();
        DeleteObjectResponse response = s3Client.deleteObject(deleteObjectRequest);
        if(!response.sdkHttpResponse().isSuccessful()){
            log.warn("삭제 실패 {}", filename);
            s3FailedUtil.saveFailedFile(key);
        }
        return response;
    }

    //클라이언트측에서 chat/{roomId}/{filename} 방식으로 업로드 시
    //"chat/{roomId}/" prefix를 가져와 해당 prefix의 key를 가진 객체 모두 삭제 처리
    public void deleteObjects(String prefix){
        //어디를 몇개의 키를 조회할 것인지 정함
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(prefix)         //chat/{chatRoomId}
                .maxKeys(1000)   //최대 1000개의 키를 한번에 조회
                .build();
        //페이징을 통해 나눠 조회
        ListObjectsV2Iterable listResponse = s3Client.listObjectsV2Paginator(listRequest);

        //각 페이징된 리스트를 조회
        for(ListObjectsV2Response filelistres : listResponse){
            //filelist를 조회해 key를 얻어 key리스트 만듦
            //ObjectIdentifier로 변환, 삭제마커 생성하도록 함(정책 따라 실제 삭제는 일주일 뒤)
            List<ObjectIdentifier> deleteObjectList = filelistres.contents().stream()
                    .map(
                            file -> ObjectIdentifier.builder()
                                    .key(file.key())
                                    .build()
                    ).collect(Collectors.toList());

            DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                    .bucket(bucket)
                    .delete(Delete.builder().objects(deleteObjectList).build())
                    .build();

            DeleteObjectsResponse response = s3Client.deleteObjects(deleteObjectsRequest);

            //삭제 실패 로그
            if (response.hasErrors()) {
                log.warn("파일 삭제 실패 리스트: {}", response.errors());
                s3FailedUtil.saveFailedFiles(response.errors());
            }else{
                log.info("삭제 성공");
            }
        }
    }

    public List<DeletedObject> deleteKeys(List<S3FailedFile> keys){
        S3Client s3 = S3Client.create();
        List<DeletedObject> deletedObjects = new ArrayList<>();
        for(int i =0; i<keys.size(); i+=1000) {
            int end = Math.min(i+1000, keys.size());

            //ObjectIdentifier로 조회
            List<ObjectIdentifier> objectsToDelete = keys.subList(i, end).stream()
                    .map(key -> ObjectIdentifier.builder().key(key.getKeyname()).build())
                    .collect(Collectors.toList());

            //삭제 객체 생성
            Delete delete = Delete.builder().objects(objectsToDelete).build();

            //삭제
            DeleteObjectsRequest request = DeleteObjectsRequest.builder()
                    .bucket(bucket)
                    .delete(delete)
                    .build();
            DeleteObjectsResponse response = s3.deleteObjects(request);
            deletedObjects.addAll(response.deleted());
            if (response.hasErrors()) {
                log.warn("파일 삭제 재시도 실패 리스트: {}", response.errors());
            }else{
                log.info("삭제 성공");
            }
        }
        //삭제 성공한 리스트 반환
        return deletedObjects;
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

    private String extractFilename(String key){
        return key.substring(key.lastIndexOf('/') + 1);
    }

    private String extractKey(String filename){
        filename = filename.substring(baseUrl.length()+1);
        log.info("file subString: {}", filename);
        return URLDecoder.decode(filename, StandardCharsets.UTF_8);
    }
}