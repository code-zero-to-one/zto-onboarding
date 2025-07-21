package com.codezerotoone.mvp.domain.member.member.integration;

import com.codezerotoone.mvp.domain.image.entity.Image;
import com.codezerotoone.mvp.domain.image.entity.ResizedImage;
import com.codezerotoone.mvp.domain.member.auth.entity.Role;
import com.codezerotoone.mvp.domain.member.auth.repository.RoleRepository;
import com.codezerotoone.mvp.domain.member.member.entity.Member;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberProfile;
import com.codezerotoone.mvp.domain.member.memberprofile.repository.MemberProfileRepository;
import com.codezerotoone.mvp.global.file.uploader.LocalFileUploader;
import com.codezerotoone.mvp.global.file.url.FileUrlResolver;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({ "test", "no-auth" })
@Import(MemberIntegrationTest.TestContextConfiguration.class)
@Slf4j
public class MemberIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    RestTemplate restTemplate;

    String baseUrl;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    MemberProfileRepository memberProfileRepository;

    @Autowired
    FileUrlResolver fileUrlResolver;

    @Autowired
    EntityManager em;

    @Autowired
    PlatformTransactionManager txManager;

    @TestConfiguration
    static class TestContextConfiguration {

        @Bean
        @Primary
        public LocalFileUploader testLocalFileUploader() {
            String directoryRoot = ClassLoader.getSystemClassLoader().getResource("application-test.yml").getPath();
            directoryRoot = directoryRoot.substring(0, directoryRoot.lastIndexOf("/"));
            directoryRoot = directoryRoot.startsWith("/") ? directoryRoot.substring(1) : directoryRoot;
            directoryRoot += "/static";
            log.info("localFileUploader - directoryRoot={}", directoryRoot);
            return new LocalFileUploader(directoryRoot);
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        this.baseUrl = "http://localhost:" + port + "/api/v1";

        Role role = Role.getMemberRole();
        this.roleRepository.saveAndFlush(role);
    }

    @AfterEach
    void tearDown() {
        TransactionStatus txStatus = this.txManager.getTransaction(new DefaultTransactionAttribute());

        // 이미지 파일 클리어
        List<ResizedImage> rImages = this.em.createQuery("""
                        SELECT ri
                        FROM ResizedImage ri
                        """, ResizedImage.class)
                .getResultList();
        rImages.forEach((ri) -> {
            String resizedImageUrl = ri.getResizedImageUrl();
            if (resizedImageUrl != null) {
                URL resourceUrl = ClassLoader.getSystemClassLoader().getResource("static/" + resizedImageUrl);
                if (resourceUrl != null) {
                    new File(resourceUrl.getFile()).delete();
                }
            }
        });

        // ResizedImage는 Cascade에 의해 같이 삭제
        List<Image> images = this.em.createQuery("""
                        SELECT i
                        FROM Image i
                        """, Image.class)
                .getResultList();
        images.forEach(this.em::remove);

        // MemberProfile은 Cascade에 의해 같이 삭제
        List<Member> allMembers = this.em.createQuery("""
                        SELECT m
                        FROM Member m
                        """, Member.class)
                .getResultList();
        allMembers.forEach(this.em::remove);
        Role role = this.em.find(Role.class, "ROLE_MEMBER");
        this.em.remove(role);

        this.txManager.commit(txStatus);
    }

    @Test
    @DisplayName("프로필 사진 extension 전송 -> 업로드 URL 응답 -> 프로필 사진 업로드")
    void createMember_uploadProfileImage() throws IOException {
        String imageUploadUri;
        String accessToken;
        Long generatedMemberId;
        {
            // Access Token for Authorization
            accessToken = "{\"id\": \"123\"}";

            // 회원가입 API 요청
            RequestEntity<String> signUpRequestEntity = RequestEntity.post(this.baseUrl + "/members")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .body("""
                        {
                            "name": "아이묭",
                            "imageExtension": "jpg"
                        }
                        """);
            ResponseEntity<String> responseEntity = this.restTemplate.exchange(signUpRequestEntity, String.class);

            // 회원가입 API 응답값 검증
            // Expected status code: 201
            // Expected to be wrapped with BaseResponse
            // "generatedMemberId" should not be null
            // "uploadUrl" should not be null, since imageExtension in request is not null
            // example of "uploadUrl":
            //     http://localhost:8080/files/images/93ur1344-12ra-5g23-5529f-9284jv13hh34_17428482311.jpg
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
            String responseBodyAsString = responseEntity.getBody();
            assert responseBodyAsString != null;
            JSONObject jsonResponseBody = new JSONObject(responseBodyAsString);
            JSONObject content = jsonResponseBody.getJSONObject("content");
            log.info("content={}", content);

            // 회원 생성 API assertion
            imageUploadUri = content.getString("uploadUrl");
            generatedMemberId = content.getLong("generatedMemberId");
            assertThat(generatedMemberId).isNotNull();
            assertThat(imageUploadUri).matches(
                    "^http://localhost:8080/api/v1/files/members/\\d+/profile/image/[a-z0-9\\-_]+\\.jpg$"
            );
        }

        {
            // 이미지 업로드 API 요청
            // 랜덤 생성된 port로 치환 후 요청 전송
            String uploadUri = replacePort(imageUploadUri);

            // 테스트 이미지 읽기 (test resource/image/test-image.png)
            byte[] data = null;
            try (InputStream testImageStream = ClassLoader.getSystemClassLoader().getResourceAsStream("image/test-image.png")) {
                assert testImageStream != null;
                data = new byte[testImageStream.available()];
                int read;
                int idx = 0;
                while ((read = testImageStream.read()) != -1) {
                    data[idx++] = (byte) read;
                }
            }

            Resource resource = new ByteArrayResource(data) {

                @Override
                public String getFilename() {
                    return "test-image.png"; // 여기서 파일명은 의미없음. 파일명은 Path Variable로 전달됨
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", resource);

            // RequestEntity 생성
            // body에 이미지 데이터 넣기
            RequestEntity<?> requestEntity = RequestEntity.put(uploadUri)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body);

            // 요청 및 응답
            ResponseEntity<String> responseEntity = this.restTemplate.exchange(requestEntity, String.class);

            // Assertion
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));

            // 제대로 이미지가 생성되었는지 확인
            TransactionStatus txStatus = this.txManager.getTransaction(new DefaultTransactionAttribute());
            MemberProfile memberProfile = this.memberProfileRepository.findNotDeletedMemberProfileById(generatedMemberId).orElseThrow();
            List<ResizedImage> resizedImages = memberProfile.getProfileImage().getResizedImages();

            // Resized Image is not empty
            assertThat(resizedImages).isNotEmpty();

            // 각 Resized Image이 가진 이미지 파일 URL에 요청을 보내면
            // 제대로 이미지 데이터 응답이 오는지 확인
            resizedImages.forEach((rImage) -> {
                String imageUrl = rImage.getFullResizedImageUrl();
                RequestEntity<Void> imageRequest = RequestEntity.get(replacePort(imageUrl))
                        .build();

                ResponseEntity<byte[]> result = this.restTemplate.exchange(imageRequest, byte[].class);
                assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
            });
            this.txManager.commit(txStatus);
        }
    }

    private String replacePort(String url) {
        return "http://localhost:" + this.port + url.substring("http://localhost:8080".length());
    }
}
