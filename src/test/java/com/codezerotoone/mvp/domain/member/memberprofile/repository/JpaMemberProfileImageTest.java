package com.codezerotoone.mvp.domain.member.memberprofile.repository;

import com.codezerotoone.mvp.domain.image.constant.ImageSizeType;
import com.codezerotoone.mvp.domain.image.entity.Image;
import com.codezerotoone.mvp.domain.image.entity.dto.ResizedImageInfo;
import com.codezerotoone.mvp.domain.image.repository.ImageRepository;
import com.codezerotoone.mvp.domain.member.auth.entity.Role;
import com.codezerotoone.mvp.domain.member.member.entity.Member;
import com.codezerotoone.mvp.domain.member.member.repository.MemberRepository;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberProfile;
import com.codezerotoone.mvp.domain.member.memberprofile.repository.jpa.JpaMemberProfileRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Slf4j
@ActiveProfiles("test")
public class JpaMemberProfileImageTest {

    @Autowired
    JpaMemberProfileRepository jpaMemberProfileRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    EntityManager em;

    @BeforeEach
    void setUp() {
        this.em.persist(Role.getMemberRole());
    }

    // TODO: DisplayName 기깔난 걸로
    @Test
    void imageTest() {
        Long generatedImageId;
        Long generatedMemberId;
        MemberProfile memberProfile;

        // Given
        Image image = Image.create("http://localhost:8080", new ResizedImageInfo("a.png", ImageSizeType.ORIGINAL));
        image = this.imageRepository.save(image);
        generatedImageId = image.getImageId();

        Member member = Member.createGeneralMember("sample@gmail.com", "GDP");
        member.getMemberProfile().updateProfileImage(Image.getReference(generatedImageId));
        member = this.memberRepository.save(member);
        generatedMemberId = member.getMemberId();

        this.em.flush();
        this.em.clear();

        // When
        memberProfile = this.jpaMemberProfileRepository.findNotDeletedMemberProfileById(generatedMemberId)
                .orElseThrow();

        log.info("memberProfile: {}", memberProfile.getClass()); // 원본 객체
        log.info("memberProfile.image: {}", memberProfile.getProfileImage().getClass()); // 프록시 객체

        // Then
        assertThat(memberProfile.getProfileImage().getImageId()).isEqualTo(generatedImageId);
        assertThat(memberProfile.getProfileImage().getResizedImages()).size().isEqualTo(1);
    }
}
