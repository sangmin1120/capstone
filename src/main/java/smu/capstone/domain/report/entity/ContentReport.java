package smu.capstone.domain.report.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import smu.capstone.domain.member.entity.UserEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE content_report SET deleted_at = NOW() WHERE id=?")
@Where(clause = "deleted_at IS NULL")
@Table(name = "content_report")
public class ContentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private UserEntity reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id", nullable = false)
    private UserEntity reportedUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType contentType; // BOARD or COMMENT

    @Column(name = "content_id", nullable = false)
    private Long contentId;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    private void onCreate() {
        this.reportedAt = LocalDateTime.now();
    }

    private ContentReport(UserEntity reporter, UserEntity reportedUser, ContentType contentType, Long contentId, String content) {
        this.reporter = reporter;
        this.reportedUser = reportedUser;
        this.contentType = contentType;
        this.contentId = contentId;
        this.content = content;
    }

    public static ContentReport of(UserEntity reporter, UserEntity reportedUser, ContentType contentType, Long contentId, String content) {
        return new ContentReport(reporter, reportedUser, contentType, contentId, content);
    }
}
