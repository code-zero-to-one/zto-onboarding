package com.codezerotoone.mvp.domain.member.memberprofile.service;

import com.codezerotoone.mvp.domain.image.constant.ImageExtension;
import com.codezerotoone.mvp.domain.member.member.exception.MemberNotFoundException;
import com.codezerotoone.mvp.domain.member.memberprofile.constant.MemberEndpoint;
import com.codezerotoone.mvp.domain.member.memberprofile.constant.PrimarySocialMediaType;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.StudySubjectDto;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.request.MemberInfoUpdateRequestDto;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.request.MemberProfileUpdateRequestDto;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.response.*;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberInfo;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberInterests;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberProfile;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.dto.MemberInfoAtomicUpdateDto;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.dto.MemberProfileAtomicUpdateDto;
import com.codezerotoone.mvp.domain.member.memberprofile.exception.DuplicatedMemberInterestException;
import com.codezerotoone.mvp.domain.member.memberprofile.exception.NullArgumentException;
import com.codezerotoone.mvp.domain.member.memberprofile.mapper.MemberProfileMapper;
import com.codezerotoone.mvp.domain.member.memberprofile.repository.AvailableStudyTimeRepository;
import com.codezerotoone.mvp.domain.member.memberprofile.repository.MemberInterestRepository;
import com.codezerotoone.mvp.domain.member.memberprofile.repository.MemberProfileRepository;
import com.codezerotoone.mvp.domain.member.memberprofile.repository.StudySubjectRepository;
import com.codezerotoone.mvp.global.file.url.FileUrlResolver;
import com.codezerotoone.mvp.global.util.FormatValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DefaultMemberProfileService implements MemberProfileService {
    private static final MemberProfileMapper mapper = MemberProfileMapper.INSTANCE;

    private final MemberProfileRepository memberProfileRepository;
    private final AvailableStudyTimeRepository availableStudyTimeRepository;
    private final MemberInterestRepository memberInterestRepository;
    private final StudySubjectRepository studySubjectRepository;
    private final FileUrlResolver fileUrlResolver;

    @Override
    public List<AvailableStudyTimeDto> getAvailableStudyTimes() {
        return this.availableStudyTimeRepository.findAllAvailableStudyTimeOrderByTime()
                .stream()
                .map(AvailableStudyTimeDto::of)
                .toList();
    }

    @Override
    public MemberProfileForStudyDto getMemberProfileNeededForStudy(Long memberId) {
        MemberProfile memberProfile = this.memberProfileRepository.findNotDeletedMemberProfileById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        return MemberProfileForStudyDto.of(memberProfile);
    }

    @Override
    public MemberProfileUpdateResponseDto updateProfile(Long memberId,
                                                        MemberProfileUpdateRequestDto dto,
                                                        boolean ignoreNull) throws MemberNotFoundException {
        validateMemberUpdate(dto, ignoreNull);

        MemberProfile memberProfile = this.memberProfileRepository.findNotDeletedMemberProfileById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        MemberProfileAtomicUpdateDto atomicUpdateDto = mapper.toMemberProfileUpdateDto(dto);

        // 변경 사유: 서비스 컴포넌트에서 Getter로 꺼내어 수정하는 것보다, DDD 리치 모델에 입각해 MemberProfile 엔티티가 스스로의 상태를 관리하도록 하는 것이 나아 보입니다.
        memberProfile.updateAtomicValues(atomicUpdateDto, ignoreNull);

        memberInterestRepository.deleteByMemberId(memberId);

        // 변경 사유: 서비스가 DTO와 Entity의 내부 구성에 의존하고 있으며, 너무 많은 역할을 하는 것으로 보입니다.
        memberInterestRepository.saveAll(
                MemberInterests.from(dto, memberProfile)
        );

        // 변경 사유: 값이 있는지를 검사하려는 것이므로, null 체크만으로는 부족하다고 생각됩니다.
        // 서비스가 DTO의 내부 구성에 대해 너무 많이 알고 있으며, 상태를 묻지 않고 getter로 꺼내어 대신 작업하는 분량이 너무 많습니다.
        // 서비스의 역할을 최소화하고 DTO가 스스로의 상태를 관리하는 방식이 보다 캡슐화와 DDD 원칙에 부합하다고 생각됩니다.
        if (dto.hasGithubLink()) {
            memberProfile.updatePrimarySocialMediaLink(dto.getGithubLink(), PrimarySocialMediaType.GITHUB);
        }

        if (dto.hasBlogOrSnsLink()) {
            memberProfile.updatePrimarySocialMediaLink(dto.getBlogOrSnsLink(), PrimarySocialMediaType.BLOG_OR_SNS);
        }

        // 추가 사유: 파라미터에 null을 전송하는 것보다는 팩토리 메서드를 오버로딩하는 게 낫다고 생각됩니다.
        // 기존 로직은 바로 아래의 기본 확장자 조건문에서 확장자 존재 여부를 검사하고, generateUuidFileUri 메서드를 호출하기 직전에 한 번 더 검사하고 있어 비효율적입니다.
        if (!dto.hasProfileImageExtension()) {
            return MemberProfileUpdateResponseDto.from(memberProfile);
        }

        // 추가 사유: 기존 코드는 dto 인스턴스에서 getProfileImageExtension 메서드를 반복적으로 호출하고 있으며, 읽기에도 어렵습니다.
        ImageExtension extension = dto.getProfileImageExtension();

        // 변경 사유: 부정 조건식과 긍정 조건식이 혼재돼 있는 경우, 읽기에 불편합니다.
        if (extension.isDefaultImage()) {
            memberProfile.updateProfileImage(null);
            // TODO: delete Image and ResizedImage

            return MemberProfileUpdateResponseDto.from(memberProfile);
        }

        // 삭제 사유: 엔티티가 영속성 컨텍스트에서 관리되므로, 다시 조회할 필요가 없습니다.

        String profileImageUploadUrl = fileUrlResolver.generateFileUploadUrl(
                // 변경 사유: 프로필 생성 엔드포인트가 MemberService와 중복됩니다. 분리하여 재사용하는 편이 낫다고 생각됩니다.
                MemberEndpoint.generateProfileImagePath(memberId), extension
        );

        // 변경 사유: 객체 기반의 변환 생성이므로 from이 보다 적합하다고 생각됩니다.
        return MemberProfileUpdateResponseDto.from(memberProfile, profileImageUploadUrl);
    }

    private void validateMemberUpdate(MemberProfileUpdateRequestDto dto, boolean ignoreNull) {
        // 변경 사유: 부정 조건식과 긍정 조건식이 혼재돼 있는 경우, 읽기에 불편합니다. 중복된 조건식도 보입니다.
        // ignoreNull의 존재 때문에 null 체크를 서비스 레이어에서 수동으로 함
        if (!ignoreNull) {
            validate(dto.getName(), dto.getTel());
        }

        // 변경 사유: 기존의 중복 관심사 검사 로직이 길어 유효성 검사가 아닌 다른 일을 하는 것처럼 보이므로 분리했습니다.
        // 관심사 validation
        validate(dto.getInterests());
    }

    private void validate(String name, String tel) {
        if (!FormatValidator.hasValue(name)) {
            throw new NullArgumentException("name", "\"name\" should not be blank");
        }

        if (!FormatValidator.hasValue(tel)) {
            throw new NullArgumentException("tel", "\"tel\" should not be blank");
        }
    }

    private void validate(List<String> interests) {
        // 변경 사유: 기존의 null 체크 및 List를 순회하며 검사하는 방식은 다소 장황해 보입니다.
        if (FormatValidator.hasValue(interests) && interests.size() > interests.stream().distinct().count()) {
            throw new DuplicatedMemberInterestException(interests);
        }
    }

    // 삭제 사유: 기존에 memberId를 파라미터로 받아 호출 객체와의 결합도가 높으며, 재사용성이 낮습니다.
    // 유사한 로직에 private 메서드를 반복적으로 만드는 것보다는 기존의 FileUrlResolver를 호출하는 편이 나은 것 같습니다.
    // MemberProfileService의 역할에서도 벗어 났다고 생각됩니다.

    @Override
    public MemberProfileUpdateResponseDto updateProfile(Long memberId, MemberProfileUpdateRequestDto dto) throws MemberNotFoundException {
        return updateProfile(memberId, dto, false);
    }

    @Override
    public MemberInfoUpdateResponseDto updateMemberInfo(Long memberId, MemberInfoUpdateRequestDto dto) throws MemberNotFoundException {
        return updateMemberInfo(memberId, dto, false);
    }

    @Override
    public MemberInfoUpdateResponseDto updateMemberInfo(Long memberId, MemberInfoUpdateRequestDto dto, boolean ignoreNull)
            throws MemberNotFoundException {
        MemberProfile memberProfile = this.memberProfileRepository.findNotDeletedMemberProfileById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        MemberInfo memberInfo = memberProfile.getMemberInfo();

        memberInfo.updateAtomicValues(new MemberInfoAtomicUpdateDto(
                dto.getSelfIntroduction(),
                dto.getStudyPlan(),
                // TODO: 구체적인 예외 혹은 더 나은 방법으로 수정
                dto.getPreferredStudySubjectId() != null
                        ? this.studySubjectRepository.findById(dto.getPreferredStudySubjectId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디 주제: " + dto.getPreferredStudySubjectId()))
                        : null
        ), ignoreNull);

        memberInfo.replaceAvailableStudyTimes(dto.getAvailableStudyTimeIds());
        memberInfo.replaceTechStacks(dto.getTechStackIds(), memberProfile);

        MemberProfile findMemberProfile = this.memberProfileRepository.findNotDeletedMemberProfileById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        return MemberInfoUpdateResponseDto.of(findMemberProfile);
    }

    @Override
    public FullMemberProfileResponseDto getMemberProfile(Long memberId) throws MemberNotFoundException {
        MemberProfile result = this.memberProfileRepository.findNotDeletedMemberProfileById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        return FullMemberProfileResponseDto.of(result);
    }

    @Override
    public List<StudySubjectDto> findAllStudySubjects() {
        return this.studySubjectRepository.findAll()
                .stream()
                .map(StudySubjectDto::of)
                .toList();
    }
}
