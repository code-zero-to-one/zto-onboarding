/* 권한 목록 */
INSERT INTO role (role_id, role_name)
VALUES ('ROLE_MEMBER', 'Member')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);

INSERT INTO role (role_id, role_name)
VALUES ('ROLE_ADMIN', 'Administrator')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);

INSERT INTO role (role_id, role_name)
VALUES ('ROLE_GUEST', 'Guest')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);

/* 가능시간대 */
INSERT INTO available_study_time (available_study_time_id, from_time, to_time, label)
VALUES (1, STR_TO_DATE('09:00', '%H:%i'), STR_TO_DATE('12:00', '%H:%i'), '오전')
ON DUPLICATE KEY UPDATE from_time = VALUES(from_time),
                        to_time   = VALUES(to_time),
                        label     = VALUES(label);

INSERT INTO available_study_time (available_study_time_id, from_time, to_time, label)
VALUES (2, STR_TO_DATE('12:00', '%H:%i'), STR_TO_DATE('13:00', '%H:%i'), '점심')
ON DUPLICATE KEY UPDATE from_time = VALUES(from_time),
                        to_time   = VALUES(to_time),
                        label     = VALUES(label);

INSERT INTO available_study_time (available_study_time_id, from_time, to_time, label)
VALUES (3, STR_TO_DATE('13:00', '%H:%i'), STR_TO_DATE('18:00', '%H:%i'), '오후')
ON DUPLICATE KEY UPDATE from_time = VALUES(from_time),
                        to_time   = VALUES(to_time),
                        label     = VALUES(label);

INSERT INTO available_study_time (available_study_time_id, from_time, to_time, label)
VALUES (4, STR_TO_DATE('18:00', '%H:%i'), STR_TO_DATE('21:00', '%H:%i'), '저녁')
ON DUPLICATE KEY UPDATE from_time = VALUES(from_time),
                        to_time   = VALUES(to_time),
                        label     = VALUES(label);

INSERT INTO available_study_time (available_study_time_id, from_time, to_time, label)
VALUES (5, STR_TO_DATE('21:00', '%H:%i'), STR_TO_DATE('23:00', '%H:%i'), '심야')
ON DUPLICATE KEY UPDATE from_time = VALUES(from_time),
                        to_time   = VALUES(to_time),
                        label     = VALUES(label);

INSERT INTO available_study_time (available_study_time_id, from_time, to_time, label)
VALUES (6, NULL, NULL, '시간 협의 가능')
ON DUPLICATE KEY UPDATE from_time = VALUES(from_time),
                        to_time   = VALUES(to_time),
                        label     = VALUES(label);

/* (선호하는) 스터디 주제 */
INSERT INTO study_subject (study_subject_id, study_subject_name)
VALUES ('ALL', '전체')
ON DUPLICATE KEY UPDATE study_subject_name = VALUES(study_subject_name);

INSERT INTO study_subject (study_subject_id, study_subject_name)
VALUES ('FRONTEND_DEEP', 'Front-end Deep Dive')
ON DUPLICATE KEY UPDATE study_subject_name = VALUES(study_subject_name);

INSERT INTO study_subject (study_subject_id, study_subject_name)
VALUES ('BACKEND_DEEP', 'Back-end Deep Dive')
ON DUPLICATE KEY UPDATE study_subject_name = VALUES(study_subject_name);

INSERT INTO study_subject (study_subject_id, study_subject_name)
VALUES ('CS_DEEP', 'CS Deep Dive')
ON DUPLICATE KEY UPDATE study_subject_name = VALUES(study_subject_name);

/* 소셜 미디어 타입 */
INSERT INTO social_media_type (social_media_type_id, social_media_name, icon_id)
VALUES ('GITHUB', 'GitHub', NULL)
ON DUPLICATE KEY UPDATE social_media_name = VALUES(social_media_name),
                        icon_id           = VALUES(icon_id);

INSERT INTO social_media_type (social_media_type_id, social_media_name, icon_id)
VALUES ('BLOG_OR_SNS', '블로그/SNS', NULL)
ON DUPLICATE KEY UPDATE social_media_name = VALUES(social_media_name),
                        icon_id           = VALUES(icon_id);

/** 회원 쪽 접근권한 세팅 */
INSERT INTO access_permission (permission_id,
                               endpoint,
                               http_method)
VALUES (1,
        '/api/v1/me',
        'GET');

INSERT INTO access_permission_role (access_permission_role_id,
                                    permission_id,
                                    role_id)
VALUES (1,
        1,
        'ROLE_MEMBER');

INSERT INTO access_permission (permission_id,
                               endpoint,
                               http_method)
VALUES (2,
        '/api/v1/members',
        'POST');

INSERT INTO access_permission_role (access_permission_role_id,
                                    permission_id,
                                    role_id)
VALUES (2,
        2,
        'ROLE_GUEST');

INSERT INTO access_permission (permission_id,
                               endpoint,
                               http_method)
VALUES (3,
        '/api/v1/members',
        'DELETE');

INSERT INTO access_permission_role (access_permission_role_id,
                                    permission_id,
                                    role_id)
VALUES (3,
        3,
        'ROLE_MEMBER');

INSERT INTO access_permission (permission_id,
                               endpoint,
                               http_method)
VALUES (4,
        '/api/v1/members/*/profile',
        'PATCH');

INSERT INTO access_permission_role (access_permission_role_id,
                                    permission_id,
                                    role_id)
VALUES (4,
        4,
        'ROLE_MEMBER');

INSERT INTO access_permission (permission_id,
                               endpoint,
                               http_method)
VALUES (5,
        '/api/v1/members/*/profile/for-study',
        'GET');

INSERT INTO access_permission_role (access_permission_role_id,
                                    permission_id,
                                    role_id)
VALUES (5,
        5,
        'ROLE_MEMBER');

INSERT INTO access_permission (permission_id,
                               endpoint,
                               http_method)
VALUES (6,
        '/api/v1/members/*/profile/info',
        'PATCH');

INSERT INTO access_permission_role (access_permission_role_id,
                                    permission_id,
                                    role_id)
VALUES (6,
        6,
        'ROLE_MEMBER');

INSERT INTO access_permission (permission_id,
                               endpoint,
                               http_method)
VALUES (7,
        '/api/v1/members/*/auto-matching',
        'PATCH');

INSERT INTO access_permission_role (access_permission_role_id,
                                    permission_id,
                                    role_id)
VALUES (7,
        7,
        'ROLE_MEMBER');

INSERT INTO access_permission (permission_id,
                               endpoint,
                               http_method)
VALUES (8,
        '/api/v1/auth/me',
        'GET');

INSERT INTO access_permission_role (access_permission_role_id,
                                    permission_id,
                                    role_id)
VALUES (8,
        8,
        'ROLE_MEMBER');

/* 기술 스택 */
INSERT INTO tech_stack (tech_stack_id,
                        tech_stack_name,
                        parent_id,
                        level)
VALUES (1,
        'Back-end',
        NULL,
        1);

INSERT INTO tech_stack (tech_stack_id,
                        tech_stack_name,
                        parent_id,
                        level)
VALUES (2,
        'Django',
        1,
        2);

INSERT INTO tech_stack (tech_stack_id,
                        tech_stack_name,
                        parent_id,
                        level)
VALUES (3,
        'Node.js',
        1,
        2);

INSERT INTO tech_stack (tech_stack_id,
                        tech_stack_name,
                        parent_id,
                        level)
VALUES (4,
        'Spring Boot',
        1,
        2);

INSERT INTO tech_stack (tech_stack_id,
                        tech_stack_name,
                        parent_id,
                        level)
VALUES (5,
        'Database',
        1,
        2);

INSERT INTO tech_stack (tech_stack_id,
                        tech_stack_name,
                        parent_id,
                        level)
VALUES (6,
        'SQL',
        5,
        3);

INSERT INTO tech_stack (tech_stack_id,
                        tech_stack_name,
                        parent_id,
                        level)
VALUES (7,
        'MySQL',
        5,
        3);

INSERT INTO tech_stack (tech_stack_id,
                        tech_stack_name,
                        parent_id,
                        level)
VALUES (8,
        'PostgreSQL',
        5,
        3);

INSERT INTO tech_stack (tech_stack_id,
                        tech_stack_name,
                        parent_id,
                        level)
VALUES (9,
        'MongoDB',
        5,
        3);

INSERT INTO tech_stack (tech_stack_id,
                        tech_stack_name,
                        parent_id,
                        level)
VALUES (10,
        'Redis',
        5,
        3);

INSERT INTO tech_stack (tech_stack_id,
                        tech_stack_name,
                        parent_id,
                        level)
VALUES (11,
        'DevOps',
        NULL,
        1);

INSERT INTO tech_stack (tech_stack_id,
                        tech_stack_name,
                        parent_id,
                        level)
VALUES (12,
        'Docker',
        11,
        2);

INSERT INTO tech_stack (tech_stack_id,
                        tech_stack_name,
                        parent_id,
                        level)
VALUES (13,
        'Kubernetes',
        11,
        2);

INSERT INTO tech_stack (tech_stack_id,
                        tech_stack_name,
                        parent_id,
                        level)
VALUES (14,
        'Apache Kafka',
        11,
        2);

INSERT INTO tech_stack (tech_stack_id,
                        tech_stack_name,
                        parent_id,
                        level)
VALUES (15,
        'Amazon Web Services',
        11,
        2);

INSERT INTO tech_stack (tech_stack_id,
                        tech_stack_name,
                        parent_id,
                        level)
VALUES (16,
        'Google Cloud Platform',
        11,
        2);

INSERT INTO tech_stack (tech_stack_id,
                        tech_stack_name,
                        parent_id,
                        level)
VALUES (17,
        'Microsoft Azure',
        11,
        2);

INSERT INTO tech_stack (tech_stack_id,
                        tech_stack_name,
                        parent_id,
                        level)
VALUES (18,
        'Front-end',
        NULL,
        1);

INSERT INTO tech_stack (tech_stack_id,
                        tech_stack_name,
                        parent_id,
                        level)
VALUES (19,
        'JavaScript',
        18,
        2);

INSERT INTO tech_stack (tech_stack_id,
                        tech_stack_name,
                        parent_id,
                        level)
VALUES (20,
        'TypeScript',
        18,
        2);

INSERT INTO tech_stack (tech_stack_id,
                        tech_stack_name,
                        parent_id,
                        level)
VALUES (21,
        'React',
        18,
        2);

INSERT INTO tech_stack (tech_stack_id,
                        tech_stack_name,
                        parent_id,
                        level)
VALUES (22,
        'Vue.js',
        18,
        2);

INSERT INTO tech_stack (tech_stack_id,
                        tech_stack_name,
                        parent_id,
                        level)
VALUES (23,
        'Next.js',
        18,
        2);

INSERT INTO tech_stack (tech_stack_id,
                        tech_stack_name,
                        parent_id,
                        level)
VALUES (24,
        'Tailwind CSS',
        18,
        2);

INSERT INTO tech_stack (tech_stack_id,
                        tech_stack_name,
                        parent_id,
                        level)
VALUES (25,
        'Vite',
        18,
        2);
