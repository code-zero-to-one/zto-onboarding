package com.codezerotoone.mvp.domain.member.memberprofile.mapper;

import com.codezerotoone.mvp.domain.member.memberprofile.dto.request.MemberProfileUpdateRequestDto;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.dto.MemberProfileAtomicUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MemberProfileMapper {
    MemberProfileMapper INSTANCE = Mappers.getMapper(MemberProfileMapper.class);

    MemberProfileAtomicUpdateDto toMemberProfileUpdateDto(MemberProfileUpdateRequestDto dto);
}
