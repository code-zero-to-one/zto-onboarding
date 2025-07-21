package com.codezerotoone.mvp.domain.member.memberprofile.dto;

import com.codezerotoone.mvp.domain.member.memberprofile.entity.AvailableStudyTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class AvailableStudyTimeDto {

    @Schema(description = "가능 시간대 ID")
    private Long id;

    @Schema(description = "시작 시간", example = "09:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm")
    private LocalTime fromTime;

    @Schema(description = "종료 시간", example = "12:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm")
    private LocalTime toTime;

    @Schema(description = "해당 시간대를 지칭하는 명사 - 오전, 오후, 저녁 등",
            examples = {
                "오전", "점심", "오후", "저녁", "심야", "시간 협의 가능"
            })
    private String label;

    @Schema(description = "label과 시간이 붙은 명칭", examples = { "오전(09:00~12:00)", "시간 협의 가능" })
    private String fullLabel;

    public static AvailableStudyTimeDto of(AvailableStudyTime entity) {
        return AvailableStudyTimeDto.builder()
                .id(entity.getAvailableStudyTimeId())
                .fromTime(entity.getFromTime())
                .toTime(entity.getToTime())
                .label(entity.getLabel())
                .fullLabel(entity.asDisplay())
                .build();
    }
}
