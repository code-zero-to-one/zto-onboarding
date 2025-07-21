package com.codezerotoone.mvp.domain.member.memberprofile.service;

import com.codezerotoone.mvp.domain.member.member.entity.Member;
import com.codezerotoone.mvp.domain.member.memberprofile.constant.Mbti;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.request.MemberProfileUpdateRequestDto;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.response.MemberProfileUpdateResponseDto;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberInfo;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberProfile;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberProfileData;
import com.codezerotoone.mvp.domain.member.memberprofile.exception.DuplicatedMemberInterestException;
import com.codezerotoone.mvp.domain.member.memberprofile.exception.NullArgumentException;
import com.codezerotoone.mvp.domain.member.memberprofile.repository.AvailableStudyTimeRepository;
import com.codezerotoone.mvp.domain.member.memberprofile.repository.MemberInterestRepository;
import com.codezerotoone.mvp.domain.member.memberprofile.repository.MemberProfileRepository;
import com.codezerotoone.mvp.domain.member.memberprofile.repository.StudySubjectRepository;
import com.codezerotoone.mvp.global.api.format.update.UpdateRequestFormat;
import com.codezerotoone.mvp.global.file.url.FileUrlResolver;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class DefaultMemberProfileServiceTest {

    @InjectMocks
    DefaultMemberProfileService defaultMemberProfileService;

    @Mock
    MemberProfileRepository memberProfileRepository;

    @Mock
    AvailableStudyTimeRepository availableStudyTimeRepository;

    @Mock
    MemberInterestRepository memberInterestRepository;

    @Mock
    StudySubjectRepository studySubjectRepository;

    @Mock
    FileUrlResolver fileUrlResolver;

    @Test
    @DisplayName("updateProfile - ignoreNull이 true인 경우 atomic value null 반영 X / profileImageExtension이 null일 경우 업로드 URL 리턴 X")
    void updateProfile_ignoreTrue_profileImageUpdateOn() throws Exception {
        // Given
        MemberProfile memberProfile = createMemberProfile("김병지");
        ReflectionTestUtils.setField(memberProfile, "memberId", 1L);
        ReflectionTestUtils.setField(memberProfile.getMemberProfileData(), "tel", "010-1111-2222");
        ReflectionTestUtils.setField(memberProfile.getMemberProfileData(), "simpleIntroduction", "변경 전");
        ReflectionTestUtils.setField(memberProfile.getMemberProfileData(), "mbti", Mbti.INFJ);
        ReflectionTestUtils.setField(memberProfile.getMemberProfileData(), "birthDate", LocalDate.of(1997, 9, 16));

        Constructor<MemberProfileUpdateRequestDto> dtoConstructor = MemberProfileUpdateRequestDto.class.getDeclaredConstructor();
        dtoConstructor.setAccessible(true);
        MemberProfileUpdateRequestDto dto = dtoConstructor.newInstance();
        dtoConstructor.setAccessible(false);

        ReflectionTestUtils.setField(dto, "name", null);
        ReflectionTestUtils.setField(dto, "tel", "010-2222-2222");
        ReflectionTestUtils.setField(dto, "simpleIntroduction", "새로운인사");
        ReflectionTestUtils.setField(dto, "mbti", null);
        ReflectionTestUtils.setField(dto, "interests", null);

        log.info("dto={}", dto);

        // Mocking
        when(this.memberProfileRepository.findNotDeletedMemberProfileById(anyLong()))
                .thenReturn(Optional.of(memberProfile));

        // When
        MemberProfileUpdateResponseDto result =
                this.defaultMemberProfileService.updateProfile(memberProfile.getMemberId(), dto, true);

        // Then
        assertThat(result.memberId()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("김병지");
        assertThat(result.tel()).isEqualTo("010-2222-2222");
        assertThat(result.profileImageUploadUrl()).isNull();
        assertThat(result.simpleIntroduction()).isEqualTo("새로운인사");
        assertThat(result.mbti()).isEqualTo(Mbti.INFJ);
    }

    @Test
    @DisplayName("updateProfile - ignoreNull이 false인 경우 atomic value null 반영 O / profileImageExtension이 null일 경우 업로드 URL 리턴 X")
    void updateProfile_ignoreFalse_profileImageUpdateOff() throws Exception {
        // Given
        MemberProfile memberProfile = createMemberProfile("김병지");
        ReflectionTestUtils.setField(memberProfile, "memberId", 1L);
        ReflectionTestUtils.setField(memberProfile.getMemberProfileData(), "tel", "010-1111-2222");
        ReflectionTestUtils.setField(memberProfile.getMemberProfileData(), "simpleIntroduction", "변경 전");
        ReflectionTestUtils.setField(memberProfile.getMemberProfileData(), "mbti", Mbti.INFJ);
        ReflectionTestUtils.setField(memberProfile.getMemberProfileData(), "birthDate", LocalDate.of(1997, 9, 16));

        Constructor<MemberProfileUpdateRequestDto> dtoConstructor = MemberProfileUpdateRequestDto.class.getDeclaredConstructor();
        dtoConstructor.setAccessible(true);
        MemberProfileUpdateRequestDto dto = dtoConstructor.newInstance();
        dtoConstructor.setAccessible(false);

        ReflectionTestUtils.setField(dto, "name", "안장원");
        ReflectionTestUtils.setField(dto, "tel", "010-2222-2222");
        ReflectionTestUtils.setField(dto, "simpleIntroduction", "새로운인사");
        ReflectionTestUtils.setField(dto, "mbti", null);
        ReflectionTestUtils.setField(dto, "interests", null);

        log.info("dto={}", dto);

        // Mocking
        when(this.memberProfileRepository.findNotDeletedMemberProfileById(anyLong()))
                .thenReturn(Optional.of(memberProfile));

        // When
        MemberProfileUpdateResponseDto result =
                this.defaultMemberProfileService.updateProfile(memberProfile.getMemberId(), dto, false);

        // Then
        assertThat(result.memberId()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("안장원");
        assertThat(result.tel()).isEqualTo("010-2222-2222");
        assertThat(result.profileImageUploadUrl()).isNull();
        assertThat(result.simpleIntroduction()).isEqualTo("새로운인사");
        assertThat(result.mbti()).isEqualTo(null);
    }

    private MemberProfile createMemberProfile(String memberName) throws Exception {
        Constructor<MemberProfile> memberProfileConstructor = MemberProfile.class.getDeclaredConstructor();
        MemberProfileData memberProfileData = MemberProfileData.create(memberName);
        MemberInfo memberInfo = MemberInfo.createEmpty();
        memberProfileConstructor.setAccessible(true);
        MemberProfile memberProfile = memberProfileConstructor.newInstance();
        ReflectionTestUtils.setField(memberProfile, "memberProfileData", memberProfileData);
        ReflectionTestUtils.setField(memberProfile, "memberInfo", memberInfo);
        memberProfileConstructor.setAccessible(false);
        return memberProfile;
    }

    private UpdateRequestFormat<String, Long> getEmptyUpdateRequestFormat() throws Exception {
        Constructor<UpdateRequestFormat> constructor = UpdateRequestFormat.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        UpdateRequestFormat<String, Long> updateRequestFormat = constructor.newInstance();
        ReflectionTestUtils.setField(updateRequestFormat, "creations", new ArrayList<>());
        ReflectionTestUtils.setField(updateRequestFormat, "modifications", new ArrayList<>());
        ReflectionTestUtils.setField(updateRequestFormat, "deletions", new ArrayList<>());
        constructor.setAccessible(false);
        return updateRequestFormat;
    }

    @ParameterizedTest
    @DisplayName("회원 프로필 업데이트 시 관심사가 중복된 경우 DuplicatedMemberInterestException 발생")
    @ValueSource(
            strings = {
                    "운동,운동",
                    "유튜브,유튜브,유튜브",
                    "운동,유튜브,유튜브",
                    "운동,운동,유튜브,유튜브",
                    "코딩,코딩,수면,잠자기,집가기"
            }
    )
    void updateMemberProfile_duplicatedInterests(String interestsAsString) {
        MemberProfileUpdateRequestDto dto = MemberProfileUpdateRequestDto.builder()
                .name("이기상")
                .tel("010-5522-1452")
                .interests(Arrays.asList(interestsAsString.split(",")))
                .build();

        assertThatExceptionOfType(DuplicatedMemberInterestException.class)
                .isThrownBy(() -> this.defaultMemberProfileService.updateProfile(1L, dto, false));
    }

    @Test
    @DisplayName("회원 프로필 업데이트 시 ignoreNull이 false인 경우, dto.name은 not null")
    void updateMemberProfile_ignoreNull_nameIsNull() {
        MemberProfileUpdateRequestDto dto = MemberProfileUpdateRequestDto.builder()
                .tel("010-1524-5635") // 필수값인 name, tel 중 tel만 세팅
                .build();

        assertThatExceptionOfType(NullArgumentException.class)
                .isThrownBy(() -> this.defaultMemberProfileService.updateProfile(1L, dto, false));
    }

    @Test
    @DisplayName("회원 프로필 업데이트 시 ignoreNull이 false인 경우, dto.tel은 not null")
    void updateMemberProfile_ignoreNull_telIsNull() {
        MemberProfileUpdateRequestDto dto = MemberProfileUpdateRequestDto.builder()
                .name("홍진호") // 필수값인 name, tel 중 name만 세팅
                .build();

        assertThatExceptionOfType(NullArgumentException.class)
                .isThrownBy(() -> this.defaultMemberProfileService.updateProfile(1L, dto, false));
    }

    @Test
    @DisplayName("회원 프로필 업데이트 시 ignoreNull이 true인 경우, dto.name, dto.tel 둘 다 nullable")
    void updateMemberProfile_ignoreNullIsTrue() {
        // Mocking
        when(this.memberProfileRepository.findNotDeletedMemberProfileById(anyLong()))
                .thenReturn(Optional.of(Member.createGeneralMemberBySocialLogin("김가을", "51256244").getMemberProfile()));

        // 모든 필드가 null인 DTO 객체 생성
        MemberProfileUpdateRequestDto dto = MemberProfileUpdateRequestDto.builder()
                .build();

        assertThatNoException().isThrownBy(() -> this.defaultMemberProfileService.updateProfile(1L, dto, true));
    }
}