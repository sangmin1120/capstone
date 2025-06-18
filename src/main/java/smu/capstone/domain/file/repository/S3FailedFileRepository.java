package smu.capstone.domain.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.capstone.domain.file.entity.S3FailedFile;

import java.util.Collection;

public interface S3FailedFileRepository extends JpaRepository<S3FailedFile, Long> {

    void deleteAllByKeynameIn(Collection<String> keynames);
}
