package com.codezerotoone.mvp.domain.member.auth.entity;

import com.codezerotoone.mvp.domain.member.auth.constant.AuthorizedHttpMethod;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "access_permission")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AccessPermission {

    @Id
    private Long permissionId;

    private String endpoint;

    @Enumerated(EnumType.STRING)
    private AuthorizedHttpMethod httpMethod;

    @OneToMany(mappedBy = "accessPermission")
    private List<AccessPermissionRole> accessPermissionRole = new ArrayList<>();
}
