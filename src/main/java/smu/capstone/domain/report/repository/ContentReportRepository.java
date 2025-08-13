package smu.capstone.domain.report.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.report.entity.ContentReport;
import smu.capstone.domain.report.entity.ContentType;

public interface ContentReportRepository extends JpaRepository<ContentReport, Long> {
    Page<ContentReport> findAll(Pageable pageable);

    // 선택적 확장: 타입별 조회
    Page<ContentReport> findByContentType(ContentType contentType, Pageable pageable);

    boolean existsByReporterAndContentTypeAndContentId(UserEntity reporter, ContentType contentType, Long contentId);
}

