package com.codezerotoone.mvp.domain.category.techstack.repository;

import com.codezerotoone.mvp.domain.category.techstack.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TechStackRepository extends JpaRepository<TechStack, Long> {

    /** 모든 기술스택 조회
     * 기술스택은 level 오름차순, 동일 level 내에서는 id 오름차순으로 정렬합니다.
     * */
    @Query("SELECT t FROM TechStack t ORDER BY t.level ASC, t.techStackId ASC")
    List<TechStack> findAllOrderByLevelAndId();

    /** 상위 기술스택 조회 */
    @Query("SELECT t FROM TechStack t WHERE t.level = 1 ORDER BY t.techStackId ASC")
    List<TechStack> findParentTechStacks();

    /** 기술스택 검색 */
    @Query("SELECT t FROM TechStack t WHERE t.techStackName LIKE %:keyword% ORDER BY t.level ASC, t.techStackId ASC")
    List<TechStack> searchByName(@Param("keyword") String keyword);
}