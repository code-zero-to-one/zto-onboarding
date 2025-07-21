package com.codezerotoone.mvp.global.api.format.update;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * <p>Update API에서 collection item update 정보를 래핑하는 포맷.</p>
 * <p>Collection을 수정할 경우, 1) 새로운 item 추가, 2) 기존 item 수정, 3) 기존 item 삭제, 이렇게 세 가지 연산을 수행할 수
 * 있다. 이 객체에서 1, 2, 3 각각의 정보를 래핑한다.</p>
 * @param <T> Update할 DTO의 타입
 * @param <ID> modification, deletion에서 사용되는 변경/삭제할 collection item의 ID 타입
 * @author PGD
 * @see ModificationWrapper
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
public class UpdateRequestFormat<T, ID> {

    @Schema(description = "새로운 item 생성")
    private List<T> creations;

    @Schema(description = "기존 item 수정")
    private List<ModificationWrapper<T, ID>> modifications;

    @Schema(description = "삭제할 item id 목록")
    private List<ID> deletions;

    public static <T, ID> UpdateRequestFormat<T, ID> empty() {
        return new UpdateRequestFormat<>(List.of(), List.of(), List.of());
    }
}
