package com.codezerotoone.mvp.domain.member.memberprofile.service;

import com.codezerotoone.mvp.domain.image.constant.ImageExtension;
import com.codezerotoone.mvp.domain.member.member.exception.MemberNotFoundException;
import com.codezerotoone.mvp.domain.member.memberprofile.constant.PrimarySocialMediaType;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.StudySubjectDto;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.request.MemberInfoUpdateRequestDto;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.request.MemberProfileUpdateRequestDto;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.response.*;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberInfo;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberInterest;
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
import com.codezerotoone.mvp.global.util.NullSafetyUtils;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        memberProfile.getMemberProfileData().updateAtomicValues(atomicUpdateDto, ignoreNull);

        this.memberInterestRepository.deleteByMemberId(memberId);

        this.memberInterestRepository.saveAll(
                NullSafetyUtils.replaceEmptyIfNull(dto.getInterests()).stream()
                        .map((v) -> MemberInterest.create(memberProfile, v))
                        .toList()
        );

        if (dto.getGithubLink() != null) {
            memberProfile.updatePrimarySocialMediaLink(dto.getGithubLink(), PrimarySocialMediaType.GITHUB);
        }

        if (dto.getBlogOrSnsLink() != null) {
            memberProfile.updatePrimarySocialMediaLink(dto.getBlogOrSnsLink(), PrimarySocialMediaType.BLOG_OR_SNS);
        }

        if (dto.getProfileImageExtension() != null && dto.getProfileImageExtension().isDefaultImage()) {
            memberProfile.updateProfileImage(null);
            // TODO: delete Image and ResizedImage
        }

        // TODO: 굳이 쿼리를 한 번 더 날려야 하나?
        MemberProfile findMemberProfile = this.memberProfileRepository.findNotDeletedMemberProfileById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        return MemberProfileUpdateResponseDto.of(findMemberProfile,
                generateProfileImageUploadUrl(memberId, dto.getProfileImageExtension()));
    }

    private void validateMemberUpdate(MemberProfileUpdateRequestDto dto, boolean ignoreNull) {
        // ignoreNull의 존재 때문에 null 체크를 서비스 레이어에서 수동으로 함
        if (!ignoreNull && dto.getName() == null) {
            throw new NullArgumentException("name", "\"name\" should not be null");
        }

        if (!ignoreNull && dto.getTel() == null) {
            throw new NullArgumentException("tel", "\"tel\" should not be null");
        }

        // 관심사 validation
        List<String> interests = dto.getInterests();
        if (ObjectUtils.isEmpty(interests)) {
            return;
        }

        List<String> duplicatedInterests = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        for (String interest : interests) {
            if (visited.contains(interest)) {
                duplicatedInterests.add(interest);
            }
            visited.add(interest);
        }
        if (!duplicatedInterests.isEmpty()) {
            throw new DuplicatedMemberInterestException(duplicatedInterests);
        }
    }

    private String generateProfileImageUploadUrl(Long memberId, @Nullable ImageExtension imageExtension) {
        if (imageExtension == null || imageExtension.isDefaultImage()) {
            return null;
        }

        String profileImageUri = this.fileUrlResolver.generateUuidFileUri(
                imageExtension.getExtension(),
                "members/" + memberId + "/profile/image"
        );
        return this.fileUrlResolver.generateFileUploadUrl(profileImageUri);
    }

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
