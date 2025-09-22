package smu.capstone.common.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
@Setter
public class BaseEntity {

    @Column(updatable=false)
    protected LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now(); // 값이 없을 때만 자동 세팅
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now(); // 혹시 null이면 보정
        }
        this.updatedAt=LocalDateTime.now();
    }
}
