package com.codezerotoone.mvp.domain.category.techstack.entity;

import com.codezerotoone.mvp.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

/**
 * 기술스택 정보를 나타내는 엔티티
 *
 * <p>기술스택은 계층 구조(부모-자식 관계)를 가질 수 있으며,
 * 이를 통해 기술 분류 체계를 유연하게 표현할 수 있습니다.</p>
 */
@Entity
@Table(name = "tech_stack")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TechStack extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long techStackId;

    /** 기술스택 이름 (ex. Java, Spring 등) */
    @Column(nullable = false)
    private String techStackName;

    /** 부모 기술스택 (최상위 기술스택인 경우 null) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private TechStack parent;

    /** 자식 기술스택 목록 (계층 구조 표현) */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TechStack> children = new ArrayList<>();

    /** 계층 레벨 (최상위는 1, 부모가 있을 경우 부모 레벨 + 1) */
    @Column(nullable = false)
    @ColumnDefault("1")
    private Integer level = 1;

    /** 이미지 ID (기술스택 아이콘, 추후 Image 엔티티와 연동 예정) */
    private Long imageId; // TODO: Image entity 정의 후...

    public static TechStack getReference(Long techStackId) {
        TechStack reference = new TechStack();
        reference.techStackId = techStackId;
        return reference;
    }

    @Builder
    public TechStack(String name, Integer level, TechStack parent, Long imageId) {
        this.techStackName = name;
        this.level = (level != null) ? level : 1;
        this.parent = parent;
        this.imageId = imageId;
    }

    /**
     * 부모 기술스택을 설정합니다.
     *
     * <p>기존 부모와의 관계를 해제하고, 새 부모와의 관계를 설정합니다.
     * 이후 계층 레벨(level)을 새로 계산하며, 자식 기술스택들도 재귀적으로 레벨을 업데이트합니다.</p>
     *
     * @param newParent 새로 설정할 부모 기술스택 (없으면 null)
     */
    public void setParent(TechStack newParent) {
        // 기존 부모와의 관계 해제
        if (this.parent != null) {
            this.parent.children.remove(this);
        }

        this.parent = newParent;

        // 새 부모와의 관계 설정
        if (newParent != null) {
            newParent.children.add(this);
        }

        updateLevel();
    }

    /**
     * 현재 기술스택과 모든 자식 기술스택의 계층 레벨을 재귀적으로 업데이트합니다.
     *
     * <p>부모가 있으면 부모 레벨 + 1, 없으면 최상위로 간주하여 레벨 1로 설정됩니다.</p>
     */
    private void updateLevel() {
        this.level = (this.parent != null) ? this.parent.getLevel() + 1 : 1;

        for (TechStack child : children) {
            child.updateLevel();
        }
    }
}
