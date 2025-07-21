/**
 * 이 전체 코드는
 * <ul>
 *     <li>ErrorCodeSpec을 구현하는 에러 코드 Enum 들을 패키지에서 찾아내고</li>
 *     <li>그걸 문서화 가능한 DTO로 변환하여</li>
 *     <li>각 에러 코드의 status, code, name, message, description을 포함한 문서화 결과를 만들기 위한 유틸리티 모듈이에요.</li>
 * </ul>
 * <p>문서화 시스템이 잘 설계돼 있어서 스프링 REST API 문서 생성 도구(Swagger, Spring REST Docs 등)와도 자연스럽게 연동 가능할 것 같네요.</p>
 *
 * <p>주석 달기는 GPT한테 부탁했음. 고마워요 GPT!</p>
 *
 * @author PGD
 */
package com.codezerotoone.mvp.global.api.error.docs;