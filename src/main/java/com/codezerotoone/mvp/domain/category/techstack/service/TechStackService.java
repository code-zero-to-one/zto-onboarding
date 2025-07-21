package com.codezerotoone.mvp.domain.category.techstack.service;

import com.codezerotoone.mvp.domain.category.techstack.dto.response.TechStackResponse;
import com.codezerotoone.mvp.domain.category.techstack.entity.TechStack;
import com.codezerotoone.mvp.domain.category.techstack.repository.TechStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 기술스택 관련 비즈니스 로직을 담당하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class TechStackService {

    private final TechStackRepository techStackRepository;

    /**
     * 선택 가능한 전체 기술스택 목록을 조회합니다.
     *
     * <p>레벨(계층)과 ID 순으로 정렬된 기술스택 목록을 반환합니다.
     * 사용자에게 제공 가능한 모든 기술스택을 표시하는 용도로 사용됩니다.
     * 예를 들어 기술스택 선택 화면에서 전체 목록을 출력할 때 활용됩니다.</p>
     *
     * @return 전체 기술스택 목록 (정렬된 상태)
     */
    public List<TechStackResponse> getTechStacks() {
        List<TechStack> techStacks = techStackRepository.findAllOrderByLevelAndId();
        return toResponse(techStacks);
    }

    /**
     * 상위 기술스택에 해당하는 목록만 조회합니다.
     *
     * <p>부모 기술스택(Parent)이 존재하지 않는 최상위 기술스택만 반환합니다.
     * 기술 카테고리 분류용으로 사용됩니다.</p>
     *
     * @return 상위 기술스택 목록
     */
    public List<TechStackResponse> getParentTechStacks() {
        List<TechStack> parentTechStacks = techStackRepository.findParentTechStacks();
        return toResponse(parentTechStacks);
    }

    /**
     * 입력한 키워드와 이름이 일치하는 기술스택 목록을 조회합니다.
     *
     * <p>사용자가 키워드를 입력했을 때 관련 기술스택을 검색하는 기능입니다.
     * 기술스택 검색 API 또는 UI 검색창 등에서 사용됩니다.</p>
     *
     * @param keyword 검색할 키워드 (기술스택 이름의 일부)
     * @return 키워드와 일치하는 기술스택 목록
     */
    public List<TechStackResponse> searchTechStacks(String keyword) {
        List<TechStack> techStacks = techStackRepository.searchByName(keyword);
        return toResponse(techStacks);
    }

    /**
     * TechStack 엔티티 목록을 TechStackResponse DTO 목록으로 변환합니다.
     *
     * <p>엔티티의 내부 정보를 외부로 노출하지 않기 위해 DTO로 변환하는 메서드입니다.
     * 부모 기술스택이 있는 경우 해당 ID를 함께 포함시켜 계층 정보를 제공합니다.</p>
     *
     * @param techStacks 변환할 기술스택 엔티티 목록
     * @return 변환된 DTO 목록
     */
    private List<TechStackResponse> toResponse(List<TechStack> techStacks) {
        return techStacks.stream()
                .map(stack -> new TechStackResponse(
                        stack.getTechStackId(),
                        stack.getTechStackName(),
                        stack.getParent() != null ? stack.getParent().getTechStackId() : null,
                        stack.getLevel()
                ))
                .collect(Collectors.toList());
    }
}
