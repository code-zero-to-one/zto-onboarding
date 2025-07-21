package com.codezerotoone.mvp.domain.category.techstack.repository;

import com.codezerotoone.mvp.domain.category.techstack.entity.TechStackRef;
import com.codezerotoone.mvp.domain.category.techstack.entity.TechStackRefType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TechStackRefRepository extends JpaRepository<TechStackRef, Long> {

    /** 특정 멤버 및 타입(STUDY/PROFILE/PROJECT)으로 조회 */
    List<TechStackRef> findByMemberProfile_MemberIdAndType(Long memberId, TechStackRefType type);

    /** 기존 기술스택 초기화 */
    @Modifying
    void deleteByMemberProfile_MemberIdAndType(Long memberId, TechStackRefType type);
}