package com.codezerotoone.mvp.domain.member.memberprofile.repository;

import com.codezerotoone.mvp.domain.member.memberprofile.entity.StudySubject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudySubjectRepository extends JpaRepository<StudySubject, String> {
}
