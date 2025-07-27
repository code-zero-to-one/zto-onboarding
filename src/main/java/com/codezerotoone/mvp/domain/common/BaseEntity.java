package com.codezerotoone.mvp.domain.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// TODO: What is this?
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // @Version
    // @Column(name = "version", nullable = false)
    // private Long version;

    protected BaseEntity() {
    }

    // 변경 사유: 자신의 날짜는 여기저기서 꺼내 볼 수 있게 하는 것보다 스스로가 관리하는 것이 객체지향의 원칙에도, DDD의 지향점에도 부합한다고 생각됩니다.
    protected LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 변경 사유: 자신의 날짜는 여기저기서 꺼내 볼 수 있게 하는 것보다 스스로가 관리하는 것이 객체지향의 원칙에도, DDD의 지향점에도 부합한다고 생각됩니다.
    protected LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // public Long getVersion() {
    //     return version;
    // }
}
