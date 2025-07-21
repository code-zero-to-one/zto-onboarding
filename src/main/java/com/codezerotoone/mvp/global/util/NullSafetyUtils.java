package com.codezerotoone.mvp.global.util;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Null-safety를 위한 유틸 클래스. 자바를 쓰는 이상 null은 떼어낼 수 없는 족쇄와 같지만
 * 최대한 어떻게든 좋게 만들어 보자!
 *
 * @author PGD
 */
@Slf4j
public class NullSafetyUtils {

    /**
     * <p><code>from</code>에서 Object Graph를 통해 getter를 호출할 때, null safety하게 추출</p>
     * <p><code>from</code>에서 Object Graph를 순회하는 동안 <code>NullPointerException</code><이 발생할 경우
     * <code>null</code> 반환</p>
     * <p>
     *     ex)
     *     <pre>
     * public class Foo {
     *     private int num;
     * }
     *
     * public class Bar {
     *     private Foo foo;
     * }
     *
     * ...
     *
     * Bar bar = new Bar();
     * bar.foo = null;
     *
     * // b.foo는 null이기 때문에 Object Graph를 그냥 순회하면 NPE 발생
     * // extractSafely 메소드를 통해 NPE 대신 null 반환
     * int fooNum = extractSafely(bar, (b) -> b.getFoo().getNum());
     *     </pre>
     * </p>
     *
     * @param from      Object Graph 순회의 entrypoint
     * @param extractor Object Graph를 순회하면서
     * @return 중간에 NPE가 발생할 경우 <code>null</code>, 그렇지 않으면 정상적으로 추출
     */
    public static <F, R> R extractSafely(F from, Function<F, R> extractor) {
        try {
            return extractor.apply(from);
        } catch (NullPointerException e) {
            if (log.isTraceEnabled()) {
                log.trace("{}", e.getMessage());
            }
            return null;
        }
    }

    /**
     * <code>set</code>이 <code>null</code>인 경우, 빈 <code>Set</code> 인스턴스 반환
     *
     * @param set Null Check할 <code>Set</code> 인스턴스
     * @return empty <code>Set</code> instance if <code>set == null</code>, otherwise <code>set</code> intact
     */
    public static <T> Set<T> replaceEmptyIfNull(Set<T> set) {
        return set == null ? new HashSet<>() : set;
    }

    /**
     * <code>list</code>가 <code>null</code>인 경우, 빈 <code>List</code> 인스턴스 반환
     *
     * @param list Null Check할 <code>List</code> 인스턴스
     * @return empty <code>List</code> instance if <code>set == null</code>, otherwise <code>list</code> intact
     */
    public static <T> List<T> replaceEmptyIfNull(List<T> list) {
        return list == null ? new ArrayList<>() : list;
    }
}
