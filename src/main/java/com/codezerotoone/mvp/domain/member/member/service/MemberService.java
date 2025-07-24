package com.codezerotoone.mvp.domain.member.member.service;

import com.codezerotoone.mvp.domain.member.member.constant.MemberStatus;
import com.codezerotoone.mvp.domain.member.member.dto.MemberCreationResponseDto;
import com.codezerotoone.mvp.domain.member.member.dto.MemberListDto;
import com.codezerotoone.mvp.domain.member.member.dto.request.MemberCreationRequestDto;
import com.codezerotoone.mvp.domain.member.member.exception.MemberNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberService {

    MemberCreationResponseDto createMember(MemberCreationRequestDto request, String oidcId);

    /**
     * 회원 정보 삭제. 삭제된 회원은 복구할 수 있다.
     *
     * @param memberId 삭제할 회원 ID
     * @throws MemberNotFoundException 해당 회원이 없을 경우.
     */
    void deleteMember(Long memberId) throws MemberNotFoundException;

    void updateStatus(Long memberId, MemberStatus status);
    Page<MemberListDto> listMember(Pageable pageable);
}
