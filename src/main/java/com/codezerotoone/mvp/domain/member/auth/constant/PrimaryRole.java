package com.codezerotoone.mvp.domain.member.auth.constant;

/**
 * <p>주요 권한 ID을 정의한 <code>enum</code> 클래스. DB에 저장된 권한은 이보다 더 많을 수 있다.</p>
 * <p>코드상에서 권한 ID를 직접 참조하는 경우가 있는데, <code>String</code>으로 하드코딩하는 걸 방지하기 위해
 * <code>enum</code> 클래스 정의</p>
 *
 * @author PGD
 */
public enum PrimaryRole {
    ROLE_MEMBER,      // 일반 회원
    ROLE_GUEST,       // 게스트 (로그인은 했지만 회원가입은 하지 않은 유저)
    ROLE_ANONYMOUS,   // 로그인하지 않은 유저
    ROLE_ADMIN        // admin
}
