package com.codezerotoone.mvp.config;

import com.codezerotoone.mvp.domain.member.auth.service.RoleService;
import com.codezerotoone.mvp.domain.member.member.repository.MemberRepository;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;

@TestConfiguration
public class DontNeedMemberMockConfig {

    @Bean
    MemberRepository memberRepository() {
        return Mockito.mock(MemberRepository.class);
    }

    @Bean
    RoleService roleService() {
        return Mockito.mock(RoleService.class);
    }

    @Bean(name = "jpaMappingContext")
    JpaMetamodelMappingContext jpaMappingContext() {
        return Mockito.mock(JpaMetamodelMappingContext.class);
    }
}