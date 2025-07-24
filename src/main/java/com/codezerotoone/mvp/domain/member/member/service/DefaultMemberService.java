package com.codezerotoone.mvp.domain.member.member.service;

import com.codezerotoone.mvp.domain.image.constant.ImageExtension;
import com.codezerotoone.mvp.domain.member.member.constant.MemberStatus;
import com.codezerotoone.mvp.domain.member.member.dto.MemberCreationResponseDto;
import com.codezerotoone.mvp.domain.member.member.dto.request.MemberCreationRequestDto;
import com.codezerotoone.mvp.domain.member.member.entity.Member;
import com.codezerotoone.mvp.domain.member.member.exception.DuplicateMemberException;
import com.codezerotoone.mvp.domain.member.member.exception.MemberNotFoundException;
import com.codezerotoone.mvp.domain.member.member.repository.MemberRepository;
import com.codezerotoone.mvp.global.file.url.FileUrlResolver;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DefaultMemberService implements MemberService {
    private final MemberRepository memberRepository;
    private final FileUrlResolver fileUrlResolver;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE) // TODO: 환경에 따라 유연하게 락을 설정할 수 있도록 변경
    public MemberCreationResponseDto createMember(MemberCreationRequestDto request, String oidcId) {
        if (this.memberRepository.existsByOidcId(oidcId)) {
            throw new DuplicateMemberException(oidcId);
        }

        Member newMember = Member.createGeneralMemberBySocialLogin(
                request.getName(),
                oidcId
        );

        newMember = this.memberRepository.save(newMember);

        return new MemberCreationResponseDto(newMember.getMemberId(),
                getFileUploadUrl(newMember.getMemberId(), request.getImageExtension()));
    }

    private @Nullable String getFileUploadUrl(Long memberId, @Nullable ImageExtension imageExtension) {
        if (imageExtension == null) {
            return null;
        }
        String profileUri = this.fileUrlResolver.generateUuidFileUri(
                imageExtension.getExtension(),
                "members/" + memberId + "/profile/image"
        );
        return this.fileUrlResolver.generateFileUploadUrl(profileUri);
    }

    @Override
    public void deleteMember(Long memberId) throws MemberNotFoundException {
        Member member = memberRepository.findNotDeletedMemberById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        member.deleteUser();
    }

    @Override
    public void updateStatus(Long memberId, MemberStatus status) {
        Member member = memberRepository.findNotDeletedMemberById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        member.updateStatus(status);
    }
}
