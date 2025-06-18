package smu.capstone.domain.file.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.domain.file.entity.S3FailedFile;
import smu.capstone.domain.file.repository.S3FailedFileRepository;
import software.amazon.awssdk.services.s3.model.DeletedObject;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Component
public class FileSchedulingService {

    private final S3FailedFileRepository s3FailedFileRepository;
    private final S3Util s3Util;

    @Scheduled(cron = "0 0 0 1 * *")
    public void deleteErrorFile(){
        List<S3FailedFile> files = s3FailedFileRepository.findAll();
        if(files.isEmpty()){
            //파일이 없다면 종료
            return;
        }
        //파일 삭제
        List<DeletedObject> response = s3Util.deleteKeys(files);
        List<String> successKey = response.stream().map(DeletedObject::key).collect(Collectors.toList());
        //성공한 파일만 DB에서 삭제
        s3FailedFileRepository.deleteAllByKeynameIn(successKey);
        log.info("파일 삭제 종료");
    }
}
