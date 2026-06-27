-- Personal CRM schema baseline
-- MySQL 8+
-- Notes:
-- 1. This file creates tables and indexes only.
-- 2. Database name is intentionally not hardcoded.
-- 3. The first five tables satisfy the internship-guide baseline.
-- 4. The last three tables are project extensions.

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Surrogate primary key',
    user_id CHAR(10) NOT NULL COMMENT 'Guide-aligned business user id',
    username VARCHAR(50) NOT NULL COMMENT 'Login username',
    password_hash VARCHAR(255) NOT NULL COMMENT 'Password hash',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0 active, 1 disabled',
    email VARCHAR(100) DEFAULT NULL COMMENT 'Email',
    phone VARCHAR(30) DEFAULT NULL COMMENT 'Phone',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_user_user_id (user_id),
    UNIQUE KEY uk_sys_user_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'User information';

CREATE TABLE IF NOT EXISTS user_avatar (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Surrogate primary key',
    pic_id CHAR(10) NOT NULL COMMENT 'Guide-aligned picture business id',
    user_id CHAR(10) NOT NULL COMMENT 'Owner user business id',
    file_name VARCHAR(255) NOT NULL COMMENT 'Stored file name',
    file_path VARCHAR(255) DEFAULT NULL COMMENT 'Physical or relative file path',
    access_url VARCHAR(255) DEFAULT NULL COMMENT 'Public access url',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Upload time',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_avatar_pic_id (pic_id),
    UNIQUE KEY uk_user_avatar_user_id (user_id),
    KEY idx_user_avatar_user_id (user_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'User avatar information';

CREATE TABLE IF NOT EXISTS contact (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Surrogate primary key',
    user_id CHAR(10) NOT NULL COMMENT 'Owner user business id',
    ct_id CHAR(10) NOT NULL COMMENT 'Guide-aligned contact business id',
    name VARCHAR(50) NOT NULL COMMENT 'Contact name',
    address VARCHAR(255) DEFAULT NULL COMMENT 'Address',
    postcode VARCHAR(20) DEFAULT NULL COMMENT 'Postcode',
    qq VARCHAR(30) DEFAULT NULL COMMENT 'QQ',
    wechat VARCHAR(50) DEFAULT NULL COMMENT 'WeChat',
    email VARCHAR(100) DEFAULT NULL COMMENT 'Email',
    gender TINYINT DEFAULT NULL COMMENT '0 unknown, 1 male, 2 female',
    birthday DATE DEFAULT NULL COMMENT 'Birthday',
    phone VARCHAR(30) DEFAULT NULL COMMENT 'Phone',
    remarks VARCHAR(500) DEFAULT NULL COMMENT 'Remarks',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0 normal, 1 blacklist',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    PRIMARY KEY (id),
    UNIQUE KEY uk_contact_ct_id (ct_id),
    KEY idx_contact_user_status (user_id, status),
    KEY idx_contact_user_name (user_id, name),
    KEY idx_contact_user_phone (user_id, phone),
    KEY idx_contact_user_birthday (user_id, birthday),
    KEY idx_contact_user_created_at (user_id, created_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Contact information';

CREATE TABLE IF NOT EXISTS contact_avatar (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Surrogate primary key',
    pic_id CHAR(10) NOT NULL COMMENT 'Guide-aligned picture business id',
    contact_id CHAR(10) NOT NULL COMMENT 'Owner contact business id',
    file_name VARCHAR(255) NOT NULL COMMENT 'Stored file name',
    file_path VARCHAR(255) DEFAULT NULL COMMENT 'Physical or relative file path',
    access_url VARCHAR(255) DEFAULT NULL COMMENT 'Public access url',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Upload time',
    PRIMARY KEY (id),
    UNIQUE KEY uk_contact_avatar_pic_id (pic_id),
    UNIQUE KEY uk_contact_avatar_contact_id (contact_id),
    KEY idx_contact_avatar_contact_id (contact_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Contact avatar information';

CREATE TABLE IF NOT EXISTS contact_todo (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Surrogate primary key',
    matter_id CHAR(10) NOT NULL COMMENT 'Guide-aligned todo business id',
    user_id CHAR(10) NOT NULL COMMENT 'Owner user business id',
    contact_id CHAR(10) NOT NULL COMMENT 'Owner contact business id',
    todo_time DATETIME NOT NULL COMMENT 'Todo scheduled time',
    content VARCHAR(500) NOT NULL COMMENT 'Todo content',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0 pending, 1 cancelled, 2 completed',
    priority TINYINT NOT NULL DEFAULT 0 COMMENT '0 normal, 1 important, 2 urgent',
    completed_at DATETIME DEFAULT NULL COMMENT 'Completed time',
    cancelled_at DATETIME DEFAULT NULL COMMENT 'Cancelled time',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    PRIMARY KEY (id),
    UNIQUE KEY uk_contact_todo_matter_id (matter_id),
    KEY idx_contact_todo_user_status_time (user_id, status, todo_time),
    KEY idx_contact_todo_user_contact (user_id, contact_id),
    KEY idx_contact_todo_user_time (user_id, todo_time),
    KEY idx_contact_todo_contact_status (contact_id, status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Contact todo information';

CREATE TABLE IF NOT EXISTS tag (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    user_id CHAR(10) NOT NULL COMMENT 'Owner user business id',
    name VARCHAR(50) NOT NULL COMMENT 'Tag name',
    color VARCHAR(20) DEFAULT NULL COMMENT 'Display color',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    PRIMARY KEY (id),
    UNIQUE KEY uk_tag_user_name (user_id, name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Contact tags';

CREATE TABLE IF NOT EXISTS contact_tag (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    user_id CHAR(10) NOT NULL COMMENT 'Owner user business id',
    contact_id CHAR(10) NOT NULL COMMENT 'Contact business id',
    tag_id BIGINT NOT NULL COMMENT 'Tag primary key',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    PRIMARY KEY (id),
    UNIQUE KEY uk_contact_tag_contact_tag (contact_id, tag_id),
    KEY idx_contact_tag_tag_contact (tag_id, contact_id),
    KEY idx_contact_tag_user_tag (user_id, tag_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Contact-tag mapping';

CREATE TABLE IF NOT EXISTS agent_operation_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    user_id CHAR(10) NOT NULL COMMENT 'Owner user business id',
    user_input TEXT NOT NULL COMMENT 'Original user input',
    intent VARCHAR(100) DEFAULT NULL COMMENT 'Detected intent',
    parsed_params JSON DEFAULT NULL COMMENT 'Structured parsed params',
    need_confirm TINYINT NOT NULL DEFAULT 0 COMMENT '0 no, 1 yes',
    confirmed TINYINT NOT NULL DEFAULT 0 COMMENT '0 no, 1 yes',
    action_type VARCHAR(100) DEFAULT NULL COMMENT 'Action type',
    execution_status TINYINT NOT NULL DEFAULT 0 COMMENT '0 pending, 1 success, 2 failed, 3 cancelled',
    execution_result JSON DEFAULT NULL COMMENT 'Execution result payload',
    error_message VARCHAR(500) DEFAULT NULL COMMENT 'Error message',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    PRIMARY KEY (id),
    KEY idx_agent_operation_log_user_created_at (user_id, created_at),
    KEY idx_agent_operation_log_user_intent (user_id, intent)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Agent operation audit log';

SET FOREIGN_KEY_CHECKS = 1;
