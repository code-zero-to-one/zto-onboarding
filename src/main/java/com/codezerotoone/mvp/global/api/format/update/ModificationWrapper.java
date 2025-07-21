package com.codezerotoone.mvp.global.api.format.update;

import lombok.*;

/**
 * 기존 Item을 삭제하려면 기존 Item의 ID와 update 데이터가 필요하다. 이 두 종류의 데이터를 묶어 주는 wrapper 클래스.
 * @param <T> Update할 DTO의 타입
 * @param <ID> modification, deletion에서 사용되는 변경/삭제할 collection item의 ID 타입
 * @author PGD
 * @see UpdateRequestFormat
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
public class ModificationWrapper<T, ID> {
    private ID id;
    private T to;
}
