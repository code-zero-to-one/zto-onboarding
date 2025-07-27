package com.codezerotoone.mvp.domain.common;

import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static com.codezerotoone.mvp.domain.common.EntityConstant.BOOLEAN_DEFAULT_FALSE;
import static jakarta.persistence.GenerationType.IDENTITY;

// 추가 사유: 일반적으로 공통되는 컬럼들은 엔티티가 추가될 때마다 일일이 설정하기보다는 공용 멤버로 묶는 게 나아 보입니다.
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseGeneralEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = BOOLEAN_DEFAULT_FALSE)
    private boolean deleteYn;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Long getId() {
        return id;
    }

    protected boolean isDeleted() {
        return deleteYn;
    }

    protected void updateId(Long id) {
        this.id = id;
    }

    protected void deleteEntity() {
        deleteYn = true;
        deletedAt = LocalDateTime.now();
    }

    protected void undeleteEntity() {
        deleteYn = false;
    }
}
