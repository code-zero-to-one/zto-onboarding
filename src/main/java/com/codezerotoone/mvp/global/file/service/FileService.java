package com.codezerotoone.mvp.global.file.service;

import com.codezerotoone.mvp.domain.image.constant.ImageSizeType;
import com.codezerotoone.mvp.domain.image.entity.Image;
import com.codezerotoone.mvp.domain.image.entity.dto.ResizedImageInfo;
import com.codezerotoone.mvp.domain.image.service.ImageService;
import com.codezerotoone.mvp.domain.member.member.exception.MemberNotFoundException;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberProfile;
import com.codezerotoone.mvp.domain.member.memberprofile.repository.MemberProfileRepository;
import com.codezerotoone.mvp.global.file.constant.FileClassification;
import com.codezerotoone.mvp.global.file.uploader.FileUploader;
import com.codezerotoone.mvp.global.file.url.FileUrlResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {
    private final FileUploader fileUploader;
    private final FileUrlResolver fileUrlResolver;
    private final ImageService imageService;
    private final MemberProfileRepository memberProfileRepository;

    public void uploadMemberProfileImage(Long memberId, String filePath, byte[] data)
            throws IOException {
        // TODO: resizing
        this.fileUploader.upload(data, filePath);

        Long generatedImageId =
                this.imageService.saveImage(this.fileUrlResolver.getFileLocation(FileClassification.IMAGE),
                        List.of(new ResizedImageInfo(filePath, ImageSizeType.ORIGINAL)));

        // IOException이 발생할 경우 회원의 프로필 사진이 업데이트되지 않음
        MemberProfile memberProfile = this.memberProfileRepository.findNotDeletedMemberProfileById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        memberProfile.updateProfileImage(Image.getReference(generatedImageId));
    }
}
