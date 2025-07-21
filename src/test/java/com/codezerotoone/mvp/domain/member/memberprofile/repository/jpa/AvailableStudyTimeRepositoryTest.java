package com.codezerotoone.mvp.domain.member.memberprofile.repository.jpa;

import com.codezerotoone.mvp.domain.member.memberprofile.entity.AvailableStudyTime;
import com.codezerotoone.mvp.domain.member.memberprofile.repository.AvailableStudyTimeRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Slf4j
@ActiveProfiles("test")
class AvailableStudyTimeRepositoryTest {

    @Autowired
    AvailableStudyTimeRepository availableStudyTimeRepository;

    @Test
    @DisplayName("시간순으로 조회된다.")
    void findAllAvailableStudyTimeOrderByTime_ordered() throws Exception {
        // Given
        List<AvailableStudyTime> entities = List.of(
                instantiate(1L, LocalTime.of(9, 0), LocalTime.of(12, 0), "오전"),
                instantiate(2L, LocalTime.of(21, 0), LocalTime.of(23, 0), "심야"),
                instantiate(3L, LocalTime.of(15, 0), LocalTime.of(18, 0), "오후"),
                instantiate(4L, null, null, "시간 협의 가능"),
                instantiate(5L, LocalTime.of(12, 0), LocalTime.of(13, 0), "점심"),
                instantiate(6L, LocalTime.of(18, 0), LocalTime.of(21, 0), "저녁")
        );

        entities = this.availableStudyTimeRepository.saveAll(entities);

        Long[] expectedIdsInOrder = entities.stream()
                .sorted((a1, a2) -> {
                    if (a1.getFromTime() == null) {
                        return -1;
                    } else if (a2.getFromTime() == null) {
                        return 1;
                    } else {
                        return a1.getFromTime().compareTo(a2.getFromTime());
                    }
                })
                .map(AvailableStudyTime::getAvailableStudyTimeId)
                .toArray(Long[]::new);

        // When
        List<AvailableStudyTime> result = this.availableStudyTimeRepository.findAllAvailableStudyTimeOrderByTime();

        // Then
        result.forEach((r) ->
                log.info("id: {}, from: {}, to: {}, label: {}", r.getAvailableStudyTimeId(), r.getFromTime(), r.getToTime(), r.getLabel()));
        log.info("expected: {}", Arrays.toString(expectedIdsInOrder));

        assertThat(result)
                .extracting("availableStudyTimeId")
                .containsExactly(expectedIdsInOrder);
    }

    private AvailableStudyTime instantiate(Long id, LocalTime from, LocalTime to, String label) throws Exception {
        Constructor<AvailableStudyTime> constructor = AvailableStudyTime.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        AvailableStudyTime instance = constructor.newInstance();
        ReflectionTestUtils.setField(instance, "availableStudyTimeId", id);
        ReflectionTestUtils.setField(instance, "fromTime", from);
        ReflectionTestUtils.setField(instance, "toTime", to);
        ReflectionTestUtils.setField(instance, "label", label);
        constructor.setAccessible(false);
        return instance;
    }
}