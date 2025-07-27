SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS tech_stack_ref;
DROP TABLE IF EXISTS tech_stack;
DROP TABLE IF EXISTS social_media;
DROP TABLE IF EXISTS social_media_type;
DROP TABLE IF EXISTS resized_image;
DROP TABLE IF EXISTS image;
DROP TABLE IF EXISTS profile_avl_time;
DROP TABLE IF EXISTS available_study_time;
DROP TABLE IF EXISTS member_interest;
DROP TABLE IF EXISTS member_profile;
DROP TABLE IF EXISTS access_permission_role;
DROP TABLE IF EXISTS access_permission;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS member_detail;
DROP TABLE IF EXISTS member;
SET FOREIGN_KEY_CHECKS = 1;


CREATE TABLE `member` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '회원 Identifier',
  `oidc_id` varchar(50) COMMENT 'Open ID Connect에 의한 ID',
  `login_id` VARCHAR(50) COMMENT '로그인할 때 사용되는 ID (자체 로그인)',
  `member_status` VARCHAR(8) NOT NULL DEFAULT 'ACTIVE' COMMENT '회원 상태',
  `created_at` timestamp NOT NULL COMMENT '생성시간',
  `updated_at` timestamp NOT NULL COMMENT '수정시간',
  `deleted_at` timestamp NULL,
  `role_id` varchar(20) NOT NULL DEFAULT 'ROLE_MEMBER' COMMENT '권한 ID'
);

CREATE TABLE `role` (
  `id` varchar(20) PRIMARY KEY COMMENT '권한 ID',
  `role_name` varchar(20) NOT NULL COMMENT '권한명'
);

CREATE TABLE `access_permission` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '접근허가 ID',
  `endpoint` VARCHAR(80) NOT NULL COMMENT '엔드포인트',
  `http_method` VARCHAR(8) NOT NULL COMMENT 'HTTP 메소드 (Upper Case)' /* CHAR로 변경 고려 */
);

CREATE TABLE `access_permission_role` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '접근허가_롤 ID',
  `permission_id` BIGINT NOT NULL COMMENT '접근허가 참조 FK',
  `role_id` VARCHAR(20) NOT NULL COMMENT '권한 참조 FK'
);

CREATE TABLE `member_profile` (
  `member_id` bigint PRIMARY KEY COMMENT '회원 ID',
  `profile_image_id` BIGINT COMMENT '프로필 이미지 ID - 이미지 테이블 참조 FK',
  `member_name` VARCHAR(15) NOT NULL COMMENT '회원 이름',
  `tel` VARCHAR(20) COMMENT '연락처',
  `self_introduction` varchar(1500) COMMENT '자기소개',
  `preferred_study_subject_id` VARCHAR(15) COMMENT '선호하는 스터디 주제 ID',
  `study_plan` VARCHAR(1500) COMMENT '공부 주제 및 계획',
  `mbti` CHAR(4),
  `birth_date` DATE COMMENT '생일',
  `simple_introduction` VARCHAR(90)
);

CREATE TABLE `member_interest` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '관심사 ID',
  name VARCHAR(20) NOT NULL COMMENT '관심사 이름',
  member_id BIGINT NOT NULL COMMENT '회원 프로필 참조 FK',
  `created_at` timestamp NOT NULL COMMENT '생성시간',
  `updated_at` timestamp NOT NULL COMMENT '수정시간'
);

CREATE TABLE `available_study_time` (
  id BIGINT PRIMARY KEY COMMENT '스터디 가능 시간대 ID',
  from_time TIME(6) COMMENT '스터디 가능 시작 시간 (날짜 빼고 시간만 사용)',
  to_time TIME(6) COMMENT '스터디 가능 끝 시간 (날짜 빼고 시간만 사용)',
  label VARCHAR(10) NOT NULL COMMENT '시간대 라벨 (오전, 오후, 저녁 등등)',
  `created_at` timestamp DEFAULT NOW() COMMENT '생성시간',
  `updated_at` timestamp DEFAULT NOW() COMMENT '수정시간'
);

CREATE TABLE `profile_avl_time` (
  id BIGINT NOT NULL COMMENT '스터디 가능 시간대 참조 FK',
  member_id BIGINT NOT NULL COMMENT '회원 프로필 참조 FK',
  PRIMARY KEY (available_study_time_id, member_id)
);

CREATE TABLE `image` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '이미지 ID',
  `location` VARCHAR(80) NOT NULL COMMENT '이미지가 저장된 장소 (도메인 URL)',
  `created_at` timestamp DEFAULT NOW() COMMENT '생성시간',
  `updated_at` timestamp DEFAULT NOW() COMMENT '수정시간',
  `deleted_at` TIMESTAMP COMMENT '삭제시간 - NULL일 경우 삭제되지 않음'
);

CREATE TABLE `resized_image` (
  `id` bigint AUTO_INCREMENT COMMENT '리사이징 이미지 ID',
  `image_id` bigint NOT NULL COMMENT '이미지 참조 FK',
  -- ENUM('THUMB','SMALL','LARGE','ETC') -> VARCHAR(10)
  `resized_image_url` varchar(255) NOT NULL COMMENT '리사이징 이미지 URL',
  `image_size_type` varchar(10) NOT NULL COMMENT '이미지 사이즈 타입',
  `deleted_at` TIMESTAMP COMMENT '삭제시간 - NULL일 경우 삭제되지 않음',
  PRIMARY KEY (resized_image_id)
);

CREATE TABLE `social_media_type` (
  `id` VARCHAR(20) PRIMARY KEY,
  `social_media_name` varchar(100) NOT NULL,
  `icon_id` BIGINT
);

CREATE TABLE `social_media` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `member_id` bigint NOT NULL,
  `social_media_type_id` VARCHAR(255) NOT NULL,
  `url` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL,
  `updated_at` timestamp NOT NULL
);

CREATE TABLE `tech_stack` (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code CHAR(3) NOT NULL UNIQUE,
  tech_stack_name VARCHAR(100) NOT NULL,
  parent_id BIGINT,
  level INT NOT NULL DEFAULT 1,
  image_id BIGINT,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `tech_stack_ref` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `tech_stack_id` BIGINT NOT NULL,
  `member_id` bigint NOT NULL,
  `type` VARCHAR(30) NOT NULL
);
