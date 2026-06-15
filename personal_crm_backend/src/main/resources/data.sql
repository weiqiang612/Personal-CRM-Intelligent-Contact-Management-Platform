-- Personal CRM demo seed data
-- Notes:
-- 1. This file is only for local development bootstrap.
-- 2. Password hash is intentionally not seeded yet because auth implementation is not finalized.
-- 3. Seed contacts and todos can be enabled after inserting a real test user.

-- Recommended first local test account after auth module is implemented:
-- INSERT INTO sys_user (user_id, username, password_hash, status)
-- VALUES ('U000000001', 'ethan', '<replace-with-bcrypt-hash>', 0);

-- Recommended follow-up demo data:
-- INSERT INTO contact (user_id, ct_id, name, phone, wechat, email, gender, birthday, status)
-- VALUES
-- ('U000000001', 'C000000001', '张三', '13800000000', 'zhangsan', 'zhangsan@example.com', 1, '2001-05-01', 0),
-- ('U000000001', 'C000000002', '李四', '13900000000', 'lisi', 'lisi@example.com', 2, '2000-08-16', 1);
--
-- INSERT INTO contact_todo (matter_id, user_id, contact_id, todo_time, content, status, priority)
-- VALUES
-- ('T000000001', 'U000000001', 'C000000001', '2026-06-16 09:00:00', '回访项目进展', 0, 1),
-- ('T000000002', 'U000000001', 'C000000002', '2026-06-17 18:00:00', '发送生日祝福', 0, 0);

-- Placeholder statement to keep Spring SQL initialization stable.
SELECT 1;
