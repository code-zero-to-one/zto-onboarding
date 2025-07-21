package com.codezerotoone.mvp.domain.member.member.service;

import com.codezerotoone.mvp.domain.image.constant.ImageExtension;
import com.codezerotoone.mvp.domain.member.member.dto.MemberCreationResponseDto;
import com.codezerotoone.mvp.domain.member.member.dto.request.MemberCreationRequestDto;
import com.codezerotoone.mvp.domain.member.member.entity.Member;
import com.codezerotoone.mvp.domain.member.member.repository.MemberRepository;
import com.codezerotoone.mvp.global.file.url.FileUrlResolver;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class DefaultMemberServiceTest {

    @InjectMocks
    DefaultMemberService memberService;

    @Mock
    FileUrlResolver fileUrlResolver;

    @Mock
    MemberRepository memberRepository;

    @Test
    @DisplayName("imageExtension이 null이 아니라면 업로드 URL이 null이 아님")
    void createMember_imageExtension_null() throws Exception {
        // Given
        MemberCreationRequestDto dto;
        String oidcId;
        final Long memberId = 3600L;
        final String fileUploadBaseUri = "http://localhost:8080/api/v1/files/";

        {
            oidcId = "hfav9vhwkdzbxc3rddf321413";

            // Parameter 세팅
            dto = getEmptyInstance();
            ReflectionTestUtils.setField(dto, "loginId", null);
            ReflectionTestUtils.setField(dto, "name", "홍길동");
            ReflectionTestUtils.setField(dto, "imageExtension", ImageExtension.PNG);

            Member generatedMember = Member.createGeneralMemberBySocialLogin(dto.getName(), oidcId);
            ReflectionTestUtils.setField(generatedMember, "memberId", memberId);
            when(this.memberRepository.save(any())).thenReturn(generatedMember);

            when(this.fileUrlResolver.generateUuidFileUri(any(), any()))
                    .thenAnswer((invocationOnMock) -> {
                        String uuid = UUID.randomUUID().toString();
                        String extension = invocationOnMock.getArgument(0);
                        String filePath = invocationOnMock.getArgument(1);
                        return filePath + "/" + uuid + "_" + System.currentTimeMillis() + "." + extension;
                    });
            when(this.fileUrlResolver.generateFileUploadUrl(any()))
                    .thenAnswer((invocationOnMock) -> {
                        String uri = invocationOnMock.getArgument(0);
                        return fileUploadBaseUri + uri;
                    });
        }

        // When
        MemberCreationResponseDto result;
        {
            result = this.memberService.createMember(dto, oidcId);
        }

        // Then
        {
            assertThat(result.generatedMemberId()).isEqualTo(memberId);
            assertThat(result.uploadUrl()).matches(
                    "^" + fileUploadBaseUri
                            + "members/3600/profile/image/[a-z0-9]{8}(-[a-z0-9]{4}){3}-[a-z0-9]{12}_\\d+\\.png$"
            );
        }
    }

    @Test
    @DisplayName("imageExtension이 null이 아니라면 업로드 URL도 null이다.")
    void createMember_null() throws Exception {
        // Given
        MemberCreationRequestDto dto;
        String oidcId;
        final Long memberId = 3600L;
        final String fileUploadBaseUri = "http://localhost:8080/api/v1/files/";

        {
            oidcId = "hfav9vhwkdzbxc3rddf321413";

            // Parameter 세팅
            dto = getEmptyInstance();
            ReflectionTestUtils.setField(dto, "loginId", null);
            ReflectionTestUtils.setField(dto, "name", "홍길동");
            ReflectionTestUtils.setField(dto, "imageExtension", null);

            Member generatedMember = Member.createGeneralMemberBySocialLogin(dto.getName(), oidcId);
            ReflectionTestUtils.setField(generatedMember, "memberId", memberId);
            when(this.memberRepository.save(any())).thenReturn(generatedMember);
        }

        // When
        MemberCreationResponseDto result;
        {
            result = this.memberService.createMember(dto, oidcId);
        }

        // Then
        {
            assertThat(result.generatedMemberId()).isEqualTo(memberId);
            assertThat(result.uploadUrl()).isNull();
        }
    }

    private MemberCreationRequestDto getEmptyInstance() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<MemberCreationRequestDto> cons = MemberCreationRequestDto.class.getDeclaredConstructor();
        cons.setAccessible(true);
        return cons.newInstance();
    }
}