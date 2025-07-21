package com.codezerotoone.mvp.global.file.controller;

import com.codezerotoone.mvp.global.api.format.BaseResponse;
import com.codezerotoone.mvp.global.api.springdocs.annotation.ConditionalHiding;
import com.codezerotoone.mvp.global.file.exception.InvalidFileException;
import com.codezerotoone.mvp.global.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 로컬에 이미지를 저장할 때만 사용되는 파일 컨트롤러
 *
 * @author PGD
 */
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {
    private final FileService fileService;
    private final Tika tika = new Tika();

    @PutMapping(value = "/members/{memberId}/profile/image/{filename}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "회원 프로필 이미지 업로드")
    @ConditionalHiding
    public ResponseEntity<BaseResponse<Void>> uploadProfileImage(@PathVariable("memberId") Long memberId,
                                                                 @PathVariable("filename") String filename,
                                                                 @RequestParam("file") MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        if (isNotImage(bytes)) {
            throw new InvalidFileException("이미지 파일이 아닙니다.");
        }

        this.fileService.uploadMemberProfileImage(memberId, "profile-image/" + filename, bytes);
        return new ResponseEntity<>(BaseResponse.of(201, "프로필 이미지가 업로드되었습니다."),
                HttpStatus.CREATED);
    }

    private boolean isNotImage(byte[] bytes) throws IOException {
        String mimeType = this.tika.detect(bytes);

        log.debug("Mime Type: {}", mimeType);

        return !mimeType.startsWith("image");
    }
}
