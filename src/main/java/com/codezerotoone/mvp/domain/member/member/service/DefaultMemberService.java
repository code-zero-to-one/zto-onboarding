package com.codezerotoone.mvp.domain.member.member.service;

import com.codezerotoone.mvp.domain.image.constant.ImageExtension;
import com.codezerotoone.mvp.domain.member.member.dto.MemberCreationResponseDto;
import com.codezerotoone.mvp.domain.member.member.dto.request.MemberCreationRequestDto;
import com.codezerotoone.mvp.domain.member.member.entity.Member;
import com.codezerotoone.mvp.domain.member.member.exception.DuplicateMemberException;
import com.codezerotoone.mvp.domain.member.member.exception.MemberIdNoValueException;
import com.codezerotoone.mvp.domain.member.member.exception.MemberNotFoundException;
import com.codezerotoone.mvp.domain.member.member.repository.MemberRepository;
import com.codezerotoone.mvp.domain.member.memberprofile.constant.MemberEndpoint;
import com.codezerotoone.mvp.global.file.url.FileUrlResolver;
import com.codezerotoone.mvp.global.util.FormatValidator;
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

    private static final String MEMBER_ID_NO_VALUE_EXCEPTION_MESSAGE = "생성된 회원 ID를 받아 오지 못했습니다. memberId: %s";

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE) // TODO: 환경에 따라 유연하게 락을 설정할 수 있도록 변경
    public MemberCreationResponseDto createMember(MemberCreationRequestDto request, String oidcId) {
        if (this.memberRepository.existsByOidcId(oidcId)) {
            throw new DuplicateMemberException(oidcId);
        }

        // 변경 사유: 엔티티를 바로 저장한다면 저장되지 않은 엔티티를 먼저 선언하고 다시 메모리 참조를 변경할 필요는 없어 보입니다.
        Member newMember = memberRepository.save(
                Member.createGeneralMemberBySocialLogin(
                        request.getName(),
                        oidcId
                )
        );

        // 변경 사유: newMember의 getMemberId 메서드를 여러 번 참조하는 것보다는 변수에 할당하는 게 좋아 보입니다.
        Long memberId = newMember.getMemberId();

        // 제로원 운영 페이지를 방문했을 때 최초 회원 가입 시 members/{memberId}/profile 경로의 memberId가 NaN으로 뜨는 치명적인 버그가 있었는데,
        // 현재는 newMember 인스턴스의 메모리 재참조를 추가함으로써 해결한 것으로 보이나 버그 발생 이력이 있는 만큼 memberId에 대한 유효성 검사 로직이 필요하다고 생각됩니다.
        if (!FormatValidator.hasValue(memberId)) {
            throw new MemberIdNoValueException(String.format(MEMBER_ID_NO_VALUE_EXCEPTION_MESSAGE, memberId));
        }

        ImageExtension extension = request.getImageExtension();

        // 추가 사유: 파라미터에 null을 전송하는 것보다는 팩토리 메서드를 추가하는 게 낫다고 생각됩니다.
        if (!FormatValidator.hasValue(extension)) {
            return MemberCreationResponseDto.of(memberId);
        }

        // 변경 사유: MemberService에서 ImageExtention의 유효성을 검사하는 것보다는, FileUrlResolver에서 검사하는     게 낫다고 생각됩니다.
        String profileImageUploadUrl = fileUrlResolver.generateFileUploadUrl(
                // 프로필 생성 엔드포인트가 MemberProfileService와 중복됩니다. 분리하여 재사용하는 편이 낫다고 생각됩니다.
                MemberEndpoint.generateProfileImagePath(memberId), extension
        );

        return new MemberCreationResponseDto(memberId, profileImageUploadUrl);
    }

    // 삭제 사유: 기존에 memberId를 파라미터로 받아 호출 객체와의 결합도가 높으며, 재사용성이 낮습니다.
    // extension과 path를 받도록 하고, private 메서드를 여러 개 만드는 것보다는 기존의 FileUrlResolver를 호출하는 편이 나은 것 같습니다.
    // MemberService의 역할에서도 벗어 났다고 생각됩니다.

    @Override
    public void deleteMember(Long memberId) throws MemberNotFoundException {
        throw new UnsupportedOperationException();
    }
}
