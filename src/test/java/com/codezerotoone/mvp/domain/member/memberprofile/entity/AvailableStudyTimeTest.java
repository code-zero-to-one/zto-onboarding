package com.codezerotoone.mvp.domain.member.memberprofile.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class AvailableStudyTimeTest {

    @Test
    @DisplayName("AvailableStudyTimeType에 정의된 시간 범위를 AvailableStudyTime에 넣을 경우, " +
            "asLabel 메소드 호출 시 <label> + \"(\" + <from> + \"~\" + <to> + \")\" 리턴")
    void asDisplay_success() throws Exception {
        // Given
        final LocalTime from = LocalTime.of(18, 0);
        final LocalTime to = LocalTime.of(21, 0);
        final String label = "저녁";

        Constructor<AvailableStudyTime> constructor = AvailableStudyTime.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        AvailableStudyTime availableStudyTime = constructor.newInstance();
        ReflectionTestUtils.setField(availableStudyTime, "fromTime", from);
        ReflectionTestUtils.setField(availableStudyTime, "toTime", to);
        ReflectionTestUtils.setField(availableStudyTime, "label", label);

        // When
        String result = availableStudyTime.asDisplay();

        // Then
        assertThat(result).isEqualTo("저녁(18:00~21:00)");
    }
}