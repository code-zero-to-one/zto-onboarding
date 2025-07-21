package com.codezerotoone.mvp.domain.member.memberprofile.mapper;

import com.codezerotoone.mvp.domain.member.memberprofile.constant.Mbti;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.request.MemberProfileUpdateRequestDto;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.dto.MemberProfileAtomicUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class MemberProfileMapperTest {

    @Test
    @DisplayName("MemberProfileUpdateDto에 있는 값 매핑")
    void testMemberProfileUpdateDtoMapping() throws Exception {
        // Given
        MemberProfileUpdateRequestDto from = instantiate(
                "예지",
                "010-1234-1234",
                "https://github.com/rudeh1253",
                "https://www.naver.com",
                "안!녕! 하세요",
                Mbti.ENTP
        );

        MemberProfileMapper mapper = MemberProfileMapper.INSTANCE;

        // When
        MemberProfileAtomicUpdateDto result = mapper.toMemberProfileUpdateDto(from);

        // Then
        log.info("result={}", result);

        assertThat(result.name()).isEqualTo(from.getName());
        assertThat(result.tel()).isEqualTo(from.getTel());
        assertThat(result.simpleIntroduction()).isEqualTo(from.getSimpleIntroduction());
        assertThat(result.mbti()).isEqualTo(from.getMbti());
    }

    private MemberProfileUpdateRequestDto instantiate(
            String name,
            String tel,
            String githubLink,
            String blogOrSnsLink,
            String simpleIntroduction,
            Mbti mbti
    ) throws Exception {
        Constructor<MemberProfileUpdateRequestDto> constructor = MemberProfileUpdateRequestDto.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        MemberProfileUpdateRequestDto instance = constructor.newInstance();
        constructor.setAccessible(false);
        ReflectionTestUtils.setField(instance, "name", name);
        ReflectionTestUtils.setField(instance, "tel", tel);
        ReflectionTestUtils.setField(instance, "githubLink", githubLink);
        ReflectionTestUtils.setField(instance, "blogOrSnsLink", blogOrSnsLink);
        ReflectionTestUtils.setField(instance, "simpleIntroduction", simpleIntroduction);
        ReflectionTestUtils.setField(instance, "mbti", mbti);
        return instance;
    }
}