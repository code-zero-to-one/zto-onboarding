package com.codezerotoone.mvp.domain.member.memberprofile.repository;

import com.codezerotoone.mvp.domain.member.memberprofile.entity.AvailableStudyTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AvailableStudyTimeRepository extends JpaRepository<AvailableStudyTime, Long> {

    /**
     * 가능 시간대 목록 조회.
     *
     * @return 가능 시간대 목록. 시간순 (이른 시간 -> 늦은 시간)으로 정렬
     */
    @Query("""
            SELECT ast
            FROM AvailableStudyTime ast
            ORDER BY ast.fromTime, ast.toTime NULLS LAST
            """)
    List<AvailableStudyTime> findAllAvailableStudyTimeOrderByTime();
}
