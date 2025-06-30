package smu.capstone.domain.file.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import smu.capstone.domain.file.entity.S3FailedFile;
import smu.capstone.domain.file.repository.S3FailedFileRepository;
import software.amazon.awssdk.services.s3.model.S3Error;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Async
@Component
public class S3FailedUtil {

    private final S3FailedFileRepository s3FailedFileRepository;

    //단건 저장
    public void saveFailedFile(String key) {
        S3FailedFile errorLog = S3FailedFile.builder()
                .keyname(key)
                .build();
        s3FailedFileRepository.save(errorLog);
    }

    //리스트 저장
    public void saveFailedFiles(List<S3Error> errors){
        List<S3FailedFile> errorLogs = errors.stream()
                .map(error -> {
                    return S3FailedFile.builder()
                            .keyname(error.key())
                            .code(error.code())
                            .message(error.message())
                            .build();
                }).toList();
        s3FailedFileRepository.saveAll(errorLogs);
    }
}
