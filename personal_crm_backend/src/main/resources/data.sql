-- Personal CRM local auth seed data
-- Notes:
-- 1. This file is only for local development bootstrap.
-- 2. Password hash is seeded for username 'ethan' with password '123456'

INSERT INTO sys_user (user_id, username, password_hash, status, email_verified)
VALUES ('U000000001', 'ethan', '$2a$10$z94Fnk1mXqA42RabilFEwu29iGVNVe15pMYLv9BaxBeHwTUDbbLI2', 0, 1)
ON DUPLICATE KEY UPDATE password_hash = VALUES(password_hash), status = 0, email_verified = 1;

-- 2. 初始化 8 条高保真联系人种子数据 (归属 ethan 'U000000001')
INSERT INTO contact (user_id, ct_id, name, gender, phone, email, birthday, status)
VALUES 
('U000000001', 'C000000001', '张雨薇', 2, '13800138000', 'zhang.yw@example.com', '1998-04-12', 0),
('U000000001', 'C000000002', '李明轩', 1, '13912345678', 'limx@example.com', '1997-08-23', 0),
('U000000001', 'C000000003', '王思颖', 2, '15698765432', 'wang.sy@example.com', '1999-11-05', 0),
('U000000001', 'C000000004', '陈泽宇', 1, '18755558899', 'chen.zy@example.com', '2000-02-17', 0),
('U000000001', 'C000000005', '赵志强', 1, '13511112222', 'zhao.zq@example.com', '1995-09-15', 0),
('U000000001', 'C000000006', '孙雅馨', 2, '13733334444', 'sun.yx@example.com', '1996-05-30', 0),
('U000000001', 'C000000007', '周建国', 1, '13655556666', 'zhou.jg@example.com', '1990-12-01', 0),
('U000000001', 'C000000008', '林小优', 0, '13377778888', 'lin.xy@example.com', '2001-07-20', 1)
ON DUPLICATE KEY UPDATE name = VALUES(name), phone = VALUES(phone), email = VALUES(email), birthday = VALUES(birthday), gender = VALUES(gender), status = VALUES(status);

-- 3. 初始化对应联系人的高拟真 Unsplash 演示头像资源映射
INSERT INTO contact_avatar (pic_id, contact_id, file_name, access_url)
VALUES
('P000000001', 'C000000001', 'zhangyw.jpg', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=80&auto=format&fit=crop&q=80'),
('P000000002', 'C000000002', 'limingxuan.jpg', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=80&auto=format&fit=crop&q=80'),
('P000000003', 'C000000003', 'wangsiying.jpg', 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=80&auto=format&fit=crop&q=80'),
('P000000004', 'C000000004', 'chenzeyu.jpg', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=80&auto=format&fit=crop&q=80'),
('P000000005', 'C000000005', 'zhaozhiqiang.jpg', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=80&auto=format&fit=crop&q=80'),
('P000000006', 'C000000006', 'sunyaxin.jpg', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=80&auto=format&fit=crop&q=80'),
('P000000007', 'C000000007', 'zhoujianguo.jpg', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=80&auto=format&fit=crop&q=80'),
('P000000008', 'C000000008', 'linxiaoyou.jpg', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=80&auto=format&fit=crop&q=80')
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
('U000000001', 'C000000004', 4), -- 陈泽宇 -> 实习
('U000000001', 'C000000005', 1), -- 赵志强 -> 同学
('U000000001', 'C000000006', 2), -- 孙雅馨 -> 朋友
('U000000001', 'C000000006', 3), -- 孙雅馨 -> 重要
('U000000001', 'C000000007', 4), -- 周建国 -> 实习
('U000000001', 'C000000008', 4)  -- 林小优 -> 实习
ON DUPLICATE KEY UPDATE contact_id = VALUES(contact_id);

-- 6. 初始化事项待办测试数据 (contact_todo)
-- 使用 MySQL 的动态时间函数生成相对时间，防日期过期导致趋势和今日卡片无数据
INSERT INTO contact_todo (matter_id, user_id, contact_id, todo_time, content, status, priority, completed_at) VALUES
-- 逾期未完成事项
('M000000001', 'U000000001', 'C000000001', DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 1 DAY), INTERVAL 10 HOUR), '电话跟进合同细节', 0, 1, NULL),
('M000000002', 'U000000001', 'C000000002', DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 2 DAY), INTERVAL 14 HOUR), '发送产品报价单并答疑', 0, 0, NULL),
-- 今日待办事项
('M000000003', 'U000000001', 'C000000003', DATE_ADD(CURDATE(), INTERVAL 15 HOUR), '下午3点视频会议沟通定制需求', 0, 2, NULL),
('M000000004', 'U000000001', 'C000000004', DATE_ADD(CURDATE(), INTERVAL 19 HOUR), '聚餐并赠送纪念礼品', 0, 1, NULL),
-- 未来几天的事项，覆盖未来趋势折线图
('M000000005', 'U000000001', 'C000000001', DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 1 DAY), INTERVAL 10 HOUR), '明天上午回访客户使用反馈', 0, 0, NULL),
('M000000006', 'U000000001', 'C000000002', DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 2 DAY), INTERVAL 14 HOUR), '后天下午确定下季度合作意向', 0, 2, NULL),
('M000000007', 'U000000001', 'C000000003', DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 2 DAY), INTERVAL 16 HOUR), '后天下午发送生日祝福短信', 0, 0, NULL),
('M000000008', 'U000000001', 'C000000004', DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 3 DAY), INTERVAL 9 HOUR), '大后天准备工作汇报PPT草稿', 0, 1, NULL),
('M000000009', 'U000000001', 'C000000001', DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 5 DAY), INTERVAL 11 HOUR), '第5天上午核对发票及财务打款进度', 0, 1, NULL),
('M000000010', 'U000000001', 'C000000002', DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 5 DAY), INTERVAL 15 HOUR), '第5天下午签署框架采购协议', 0, 2, NULL),
('M000000011', 'U000000001', 'C000000003', DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 6 DAY), INTERVAL 10 HOUR), '第6天上午进行技术答疑沙龙', 0, 0, NULL),
-- 已完成的历史事项
('M000000012', 'U000000001', 'C000000001', DATE_SUB(NOW(), INTERVAL 5 DAY), '初步对接产品规格说明书', 2, 0, DATE_SUB(NOW(), INTERVAL 5 DAY)),
('M000000013', 'U000000001', 'C000000002', DATE_SUB(NOW(), INTERVAL 4 DAY), '发送项目第一版Demo视频', 2, 1, DATE_SUB(NOW(), INTERVAL 4 DAY))
ON DUPLICATE KEY UPDATE content = VALUES(content), todo_time = VALUES(todo_time), status = VALUES(status), priority = VALUES(priority);
