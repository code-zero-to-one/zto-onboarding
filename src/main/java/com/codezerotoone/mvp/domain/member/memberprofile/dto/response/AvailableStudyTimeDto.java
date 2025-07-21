package com.codezerotoone.mvp.domain.member.memberprofile.dto.response;

import com.codezerotoone.mvp.domain.member.memberprofile.entity.AvailableStudyTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class AvailableStudyTimeDto {

    @Schema(description = "가능 시간대 ID")
    private final Long availableTimeId;

    @Schema(description = "가능 시간대 표시. ex) 오전(09:00~12:00)")
    private final String display;

    public static AvailableStudyTimeDto of(AvailableStudyTime availableStudyTime) {
        return AvailableStudyTimeDto.builder()
                .availableTimeId(availableStudyTime.getAvailableStudyTimeId())
                .display(availableStudyTime.asDisplay())
                .build();
    }
}
