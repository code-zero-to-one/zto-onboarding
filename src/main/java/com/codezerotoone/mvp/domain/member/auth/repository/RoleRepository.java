package com.codezerotoone.mvp.domain.member.auth.repository;

import com.codezerotoone.mvp.domain.member.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
}
