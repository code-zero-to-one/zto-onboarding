package com.codezerotoone.mvp.domain.member.memberprofile.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 스터디 가능 시간대. 한 회원이 여러 개의 가능 시간대를 가질 수 있으므로
 * <code>MemberProfile</code> 엔티티와 다대다 매핑
 *
 * @author PGD
 */
@Entity
@Table(name = "available_study_time")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class AvailableStudyTime {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // AvailableStudyTime은 미리 삽입되는 데이터이기 때문에
    // Id를 생성할 필요가 없다.
    @Id
    private Long availableStudyTimeId;

    private LocalTime fromTime;
    private LocalTime toTime;
    private String label;

    // TODO: asFullLabel로 이름 변경해야 함
    public String asDisplay() {
        if (isNegotiatable()) {
            return this.label;
        }

        return this.label
                + "("
                + TIME_FORMATTER.format(fromTime)
                + "~"
                + TIME_FORMATTER.format(toTime)
                + ")";
    }

    public static AvailableStudyTime getReference(Long id) {
        AvailableStudyTime instance = new AvailableStudyTime();
        instance.availableStudyTimeId = id;
        return instance;
    }

    public boolean isNegotiatable() {
        return this.fromTime == null || this.toTime == null;
    }
}
