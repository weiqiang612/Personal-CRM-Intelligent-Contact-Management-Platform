package com.weiqiang.personal_crm_backend.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 数据库平滑迁移与字段自动增量配置
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DatabaseMigrationConfig {

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void migrateDatabaseSchema() {
        try {
            log.info("Executing database schema migrations for TASK-013...");
            jdbcTemplate.execute("ALTER TABLE contact ADD COLUMN deleted TINYINT NOT NULL DEFAULT 0 COMMENT '0 normal, 1 deleted'");
        } catch (Exception e) {
            log.info("Column 'deleted' already exists in table 'contact' or alter bypassed.");
        }

        try {
            jdbcTemplate.execute("ALTER TABLE contact_todo ADD COLUMN deleted TINYINT NOT NULL DEFAULT 0 COMMENT '0 normal, 1 deleted'");
        } catch (Exception e) {
            log.info("Column 'deleted' already exists in table 'contact_todo' or alter bypassed.");
        }

        try {
            log.info("Executing database schema migrations for TASK-014 (activity_log)...");
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS activity_log (" +
                    "id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary key', " +
                    "activity_id VARCHAR(64) NOT NULL COMMENT 'Activity business id', " +
                    "user_id VARCHAR(64) NOT NULL COMMENT 'Owner user id', " +
                    "contact_id VARCHAR(64) NOT NULL COMMENT 'Contact business id', " +
                    "event_type VARCHAR(32) NOT NULL COMMENT 'Event type enum', " +
                    "title VARCHAR(128) NOT NULL COMMENT 'Event summary title', " +
                    "description TEXT DEFAULT NULL COMMENT 'Detailed event description', " +
                    "occurred_at DATETIME NOT NULL COMMENT 'Event occurrence timestamp', " +
                    "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Log record creation time', " +
                    "PRIMARY KEY (id), " +
                    "UNIQUE KEY uk_activity_id (activity_id), " +
                    "KEY idx_user_contact_occurred (user_id, contact_id, occurred_at)" +
                    ") ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Contact activity feed log'");
        } catch (Exception e) {
            log.info("Table 'activity_log' creation bypassed or failed: " + e.getMessage());
        }
    }
}
