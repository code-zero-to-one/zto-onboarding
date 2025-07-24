package com.codezerotoone.mvp.domain.member.member.controller;

import com.codezerotoone.mvp.domain.member.member.constant.MemberStatus;
import com.codezerotoone.mvp.domain.member.member.controller.apidocs.MemberDeletionApiDocs;
import com.codezerotoone.mvp.domain.member.member.controller.apidocs.MemberListApiDocs;
import com.codezerotoone.mvp.domain.member.member.controller.apidocs.SignUpApiDocs;
import com.codezerotoone.mvp.domain.member.member.controller.apidocs.UpdateMemberStatusApiDocs;
import com.codezerotoone.mvp.domain.member.member.dto.MemberCreationResponseDto;
import com.codezerotoone.mvp.domain.member.member.dto.MemberListDto;
import com.codezerotoone.mvp.domain.member.member.dto.request.MemberCreationRequestDto;
import com.codezerotoone.mvp.domain.member.member.service.MemberService;
import com.codezerotoone.mvp.global.api.format.BaseResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@Tag(
        name = "Member API",
        description = "회원 정보와 관련된 API"
)
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @PostMapping
    @SignUpApiDocs
    public ResponseEntity<BaseResponse<MemberCreationResponseDto>> signUp(@Valid @RequestBody MemberCreationRequestDto request,
                                                                          @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        String oidcId = principal.getAttribute("sub");
        return new ResponseEntity<>(
                BaseResponse.of(this.memberService.createMember(request, oidcId),
                        HttpStatus.CREATED,
                        "회원가입에 성공했습니다."),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/{memberId}")
    @MemberDeletionApiDocs
    public ResponseEntity<BaseResponse<Void>> deleteMember(@PathVariable("memberId") Long memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.ok(BaseResponse.of(HttpStatus.OK));
    }

    @PatchMapping("/{memberId}/status")
    @UpdateMemberStatusApiDocs
    public ResponseEntity<BaseResponse<Void>> updateMemberStatus(@PathVariable Long memberId, @RequestParam("status")
                                                                 MemberStatus status) {
        memberService.updateStatus(memberId, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @MemberListApiDocs
    public ResponseEntity<BaseResponse<Page<MemberListDto>>> listMembers(Pageable pageable) {
        Page<MemberListDto> page = memberService.listMember(pageable);

        return ResponseEntity.ok(BaseResponse.of(page, HttpStatus.OK));
    }
}
