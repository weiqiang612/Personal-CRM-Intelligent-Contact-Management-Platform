-- Personal CRM local auth seed data
-- Notes:
-- 1. This file is only for local development bootstrap.
-- 2. Password hash is seeded for username 'ethan' with password '123456'

INSERT INTO sys_user (user_id, username, password_hash, status)
VALUES ('U000000001', 'ethan', '$2a$10$z94Fnk1mXqA42RabilFEwu29iGVNVe15pMYLv9BaxBeHwTUDbbLI2', 0)
ON DUPLICATE KEY UPDATE password_hash = VALUES(password_hash);
