package com.codezerotoone.mvp.domain.member.memberprofile.controller;

import com.codezerotoone.mvp.domain.member.memberprofile.controller.apidocs.*;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.StudySubjectDto;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.request.MemberInfoUpdateRequestDto;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.request.MemberProfileUpdateRequestDto;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.response.*;
import com.codezerotoone.mvp.domain.member.memberprofile.service.MemberProfileService;
import com.codezerotoone.mvp.global.api.format.BaseResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "회원 프로필 API", description = "회원 프로필 REST API")
@RequiredArgsConstructor
public class MemberProfileController {
    private final MemberProfileService memberProfileService;

    @GetMapping("/available-study-times")
    @GettingAvailableStudyTimeApiDocs
    public ResponseEntity<BaseResponse<List<AvailableStudyTimeDto>>> getAllAvailableStudyTimes() {
        return ResponseEntity.ok(
                BaseResponse.of(
                        this.memberProfileService.getAvailableStudyTimes(),
                        HttpStatus.OK,
                        "가능 시간대 모두 조회"
                )
        );
    }

    @PatchMapping("/members/{memberId}/profile")
    @MemberProfileUpdateApiDocs
    public ResponseEntity<BaseResponse<MemberProfileUpdateResponseDto>> updateProfile(
            @PathVariable("memberId") Long memberId,
            @Valid @RequestBody MemberProfileUpdateRequestDto requestBody,
            @RequestParam(value = "ignore-null", defaultValue = "false") boolean ignoreNull
    ) {
        return ResponseEntity.ok(
                BaseResponse.of(
                        this.memberProfileService.updateProfile(memberId, requestBody, ignoreNull),
                        HttpStatus.OK,
                        "회원 프로필 업데이트 성공"
                )
        );
    }

    @PatchMapping("/members/{memberId}/profile/info")
    @MemberInfoUpdateApiDocs
    public ResponseEntity<BaseResponse<MemberInfoUpdateResponseDto>> updateMemberInfo(
            @PathVariable("memberId") Long memberId,
            @Valid @RequestBody MemberInfoUpdateRequestDto requestBody,
            @RequestParam(value = "ignore-null", defaultValue = "false") boolean ignoreNull
    ) {
        return ResponseEntity.ok(
                BaseResponse.of(
                        this.memberProfileService.updateMemberInfo(memberId, requestBody, ignoreNull),
                        HttpStatus.OK,
                        "내 정보 업데이트 성공"
                )
        );
    }

    @GetMapping("/members/{memberId}/profile/for-study")
    @GettingMemberProfileForStudyApiDocs
    public ResponseEntity<BaseResponse<MemberProfileForStudyDto>> getMemberProfileForStudy(@PathVariable("memberId") Long memberId) {
        return ResponseEntity.ok(
                BaseResponse.of(
                        this.memberProfileService.getMemberProfileNeededForStudy(memberId),
                        HttpStatus.OK,
                        "스터디에 필요한 회원 프로필 조회 성공"
                )
        );
    }

    @GetMapping("/members/{memberId}/profile")
    @GettingMemberProfileApiDocs
    public ResponseEntity<BaseResponse<FullMemberProfileResponseDto>> getMemberProfile(@PathVariable("memberId") Long memberId) {
        return ResponseEntity.ok(
                BaseResponse.of(
                        this.memberProfileService.getMemberProfile(memberId),
                        HttpStatus.OK
                )
        );
    }

    @GetMapping("/study-subjects")
    @GettingStudySubjectsApiDocs
    public ResponseEntity<BaseResponse<List<StudySubjectDto>>> findAllStudySubjects() {
        return ResponseEntity.ok(
                BaseResponse.of(
                        this.memberProfileService.findAllStudySubjects(),
                        HttpStatus.OK,
                        "모든 스터디 주제 조회 성공"
                )
        );
    }
}
