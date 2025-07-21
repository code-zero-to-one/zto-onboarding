package com.codezerotoone.mvp.domain.member.memberprofile.service;

import com.codezerotoone.mvp.domain.member.member.exception.MemberNotFoundException;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.StudySubjectDto;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.request.MemberInfoUpdateRequestDto;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.request.MemberProfileUpdateRequestDto;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.response.*;

import java.util.List;

public interface MemberProfileService {

    List<AvailableStudyTimeDto> getAvailableStudyTimes();

    MemberProfileForStudyDto getMemberProfileNeededForStudy(Long memberId);

    /**
     * <p>Profile 수정 (not Member Info)</p>
     *
     * @param memberId   수정할 프로필을 소유한 회원의 ID
     * @param dto        프로필 수정 DTO
     * @param ignoreNull <code>true</code>일 경우 <code>null</code>은 반영하지 않는다. <code>false</code>일 경우
     *                   <code>null</code> 반영
     * @return 수정 후 회원 프로필 정보
     * @throws MemberNotFoundException <code>memberId</code>에 해당하는 회원이 없을 경우
     */
    MemberProfileUpdateResponseDto updateProfile(Long memberId, MemberProfileUpdateRequestDto dto, boolean ignoreNull)
            throws MemberNotFoundException;

    /**
     * <p>회원 정보 수정 ({@link com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberInfo})</p>
     *
     * @param memberId   수정할 회원 정보를 소유한 회원의 ID
     * @param dto        프로필 수정 DTO
     * @param ignoreNull <code>true</code>일 경우 <code>null</code>은 반영하지 않는다. <code>false</code>일 경우
     *                   <code>null</code> 반영
     * @return 수정 후 회원 정보
     * @throws MemberNotFoundException <code>memberId</code>에 해당하는 회원이 없을 경우
     */
    MemberInfoUpdateResponseDto updateMemberInfo(Long memberId, MemberInfoUpdateRequestDto dto, boolean ignoreNull)
            throws MemberNotFoundException;

    /**
     * <p>회원 정보 수정 ({@link com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberInfo})</p>
     *
     * @param memberId   수정할 회원 정보를 소유한 회원의 ID
     * @param dto        프로필 수정 DTO
     * @return 수정 후 회원 정보
     * @throws MemberNotFoundException <code>memberId</code>에 해당하는 회원이 없을 경우
     */
    MemberInfoUpdateResponseDto updateMemberInfo(Long memberId, MemberInfoUpdateRequestDto dto)
            throws MemberNotFoundException;

    /**
     * <p>Profile 수정 (not Member Info. <code>null도 반영한다.</code></p>
     *
     * @param memberId 수정할 프로필을 소유한 회원의 ID
     * @param dto      프로필 수정 DTO
     * @return 수정 후 회원 프로필 정보
     * @throws MemberNotFoundException <code>memberId</code>에 해당하는 회원이 없을 경우
     */
    MemberProfileUpdateResponseDto updateProfile(Long memberId, MemberProfileUpdateRequestDto dto)
            throws MemberNotFoundException;

    FullMemberProfileResponseDto getMemberProfile(Long memberId) throws MemberNotFoundException;

    List<StudySubjectDto> findAllStudySubjects();
}
