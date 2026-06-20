-- Personal CRM local auth seed data
-- Notes:
-- 1. This file is only for local development bootstrap.
-- 2. Password hash is seeded for username 'ethan' with password '123456'

INSERT INTO sys_user (user_id, username, password_hash, status)
VALUES ('U000000001', 'ethan', '$2a$10$z94Fnk1mXqA42RabilFEwu29iGVNVe15pMYLv9BaxBeHwTUDbbLI2', 0)
ON DUPLICATE KEY UPDATE password_hash = VALUES(password_hash);

-- 2. 初始化 4 条高保真联系人种子数据 (归属 ethan 'U000000001')
INSERT INTO contact (user_id, ct_id, name, gender, phone, email, birthday, status)
VALUES 
('U000000001', 'C000000001', '张雨薇', 2, '13800138000', 'zhang.yw@example.com', '1998-04-12', 0),
('U000000001', 'C000000002', '李明轩', 1, '13912345678', 'limx@example.com', '1997-08-23', 0),
('U000000001', 'C000000003', '王思颖', 2, '15698765432', 'wang.sy@example.com', '1999-11-05', 0),
('U000000001', 'C000000004', '陈泽宇', 1, '18755558899', 'chen.zy@example.com', '2000-02-17', 0)
ON DUPLICATE KEY UPDATE name = VALUES(name), phone = VALUES(phone), email = VALUES(email), birthday = VALUES(birthday), gender = VALUES(gender), status = VALUES(status);

-- 3. 初始化对应联系人的高拟真 Unsplash 演示头像资源映射
INSERT INTO contact_avatar (pic_id, contact_id, file_name, access_url)
VALUES
('P000000001', 'C000000001', 'zhangyw.jpg', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=80&auto=format&fit=crop&q=80'),
('P000000002', 'C000000002', 'limingxuan.jpg', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=80&auto=format&fit=crop&q=80'),
('P000000003', 'C000000003', 'wangsiying.jpg', 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=80&auto=format&fit=crop&q=80'),
('P000000004', 'C000000004', 'chenzeyu.jpg', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=80&auto=format&fit=crop&q=80')
ON DUPLICATE KEY UPDATE access_url = VALUES(access_url);

-- 4. 初始化标签种子数据 (归属 ethan 'U000000001')
INSERT INTO tag (id, user_id, name, color) VALUES
(1, 'U000000001', '同学', '#a855f7'),
(2, 'U000000001', '朋友', '#22c55e'),
(3, 'U000000001', '重要', '#eab308'),
(4, 'U000000001', '实习', '#3b82f6')
ON DUPLICATE KEY UPDATE name = VALUES(name), color = VALUES(color);

-- 5. 初始化联系人-标签关联数据
INSERT INTO contact_tag (user_id, contact_id, tag_id) VALUES
('U000000001', 'C000000001', 1), -- 张雨薇 -> 同学
('U000000001', 'C000000001', 2), -- 张雨薇 -> 朋友
('U000000001', 'C000000002', 1), -- 李明轩 -> 同学
('U000000001', 'C000000003', 2), -- 王思颖 -> 朋友
('U000000001', 'C000000003', 3), -- 王思颖 -> 重要
('U000000001', 'C000000004', 3), -- 陈泽宇 -> 重要
('U000000001', 'C000000004', 4)  -- 陈泽宇 -> 实习
ON DUPLICATE KEY UPDATE contact_id = VALUES(contact_id);
