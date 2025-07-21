package com.codezerotoone.mvp.domain.category.techstack.service;

import com.codezerotoone.mvp.domain.category.techstack.dto.response.TechStackResponse;
import com.codezerotoone.mvp.domain.category.techstack.entity.TechStack;
import com.codezerotoone.mvp.domain.category.techstack.entity.TechStackRef;
import com.codezerotoone.mvp.domain.category.techstack.entity.TechStackRefType;
import com.codezerotoone.mvp.domain.category.techstack.repository.TechStackRefRepository;
import com.codezerotoone.mvp.domain.category.techstack.repository.TechStackRepository;
import com.codezerotoone.mvp.domain.member.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 스터디 신청 시 기술스택을 관리하는 서비스
 *
 * <p>사용자의 스터디 신청에 필요한 기술스택 정보를 저장하고 조회하는 기능을 제공합니다.
 * TechStackRef 엔티티를 통해 회원과 기술스택 간의 관계를 관리합니다.</p>
 */
@Service
@RequiredArgsConstructor
public class StudyTechStackService {

    private final TechStackRefRepository techStackRefRepository;
    private final TechStackRepository techStackRepository;

    /**
     * 사용자의 스터디 신청 기술스택 정보를 저장합니다.
     *
     * <p>기존에 등록된 스터디 신청 기술스택은 모두 삭제한 후, 새로 입력된 기술스택 ID 목록을 저장합니다.
     * 회원(Member)과 기술스택(TechStack)을 연결하는 TechStackRef 엔티티를 생성 후 저장합니다.</p>
     *
     * @param memberId 스터디를 신청하는 회원의 ID
     * @param techStackIds 사용자가 선택한 기술스택 ID 목록
     */
    @Transactional
    public void saveStudyTechStacks(Long memberId, List<Long> techStackIds) {
        // 기존 스터디 신청 기술스택 삭제
        techStackRefRepository.deleteByMemberProfile_MemberIdAndType(memberId, TechStackRefType.STUDY);

        Member member = Member.getReference(memberId);

        // 새로 저장
        List<TechStack> techStacks = techStackRepository.findAllById(techStackIds);

        List<TechStackRef> refs = techStacks.stream()
                .map(stack -> TechStackRef.create(stack, member.getMemberProfile(), TechStackRefType.STUDY))
                .collect(Collectors.toList());

        techStackRefRepository.saveAll(refs);
    }

    /**
     * 사용자가 신청한 스터디 기술스택 목록을 조회합니다.
     *
     * <p>회원 ID를 기준으로 등록된 TechStackRef를 조회하고,
     * 연결된 TechStack 엔티티를 TechStackResponse DTO로 변환하여 반환합니다.</p>
     *
     * @param memberId 스터디 신청 회원의 ID
     * @return 신청한 기술스택 정보를 담은 DTO 목록
     */
    public List<TechStackResponse> getStudyTechStacks(Long memberId) {
        return techStackRefRepository.findByMemberProfile_MemberIdAndType(memberId, TechStackRefType.STUDY).stream()
                .map(ref -> {
                    TechStack stack = ref.getTechStack();
                    return new TechStackResponse(
                            stack.getTechStackId(),
                            stack.getTechStackName(),
                            stack.getParent() != null ? stack.getParent().getTechStackId() : null,
                            stack.getLevel()
                    );
                })
                .collect(Collectors.toList());
    }
}
