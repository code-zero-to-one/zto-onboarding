package com.codezerotoone.mvp.domain.member.memberprofile.dto;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
public class IdNameDto {
    private Long id;
    private String name;
}
