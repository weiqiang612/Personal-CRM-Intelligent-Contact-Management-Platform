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
    }
}
