-- ============================================================
-- 07_install_repair_technician.sql
-- 维修人员角色、菜单、权限 + repair_request 表扩展
-- 执行: mysql -u root -p --default-character-set=utf8mb4 -D ry-vue < sql/07_install_repair_technician.sql
-- ============================================================
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
USE `ry-vue`;

-- ---------- 1. 表结构扩展 ----------
-- 新增维修人员相关字段（首次执行，重复执行会跳过已存在的列）
-- MySQL 8.0 不支持 ADD COLUMN IF NOT EXISTS，使用存储过程安全添加
DROP PROCEDURE IF EXISTS add_repair_column;
DELIMITER $$
CREATE PROCEDURE add_repair_column(
  IN col_name VARCHAR(64),
  IN col_def  VARCHAR(256),
  IN after_col VARCHAR(64)
)
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = 'ry-vue' AND TABLE_NAME = 'repair_request' AND COLUMN_NAME = col_name
  ) THEN
    SET @sql = CONCAT('ALTER TABLE repair_request ADD COLUMN ', col_name, ' ', col_def, ' AFTER ', after_col);
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;
END$$
DELIMITER ;

CALL add_repair_column('technician_id',    'bigint(20)    DEFAULT NULL         COMMENT ''受理维修人员ID''', 'admin_reply');
CALL add_repair_column('accepted_at',      'datetime      DEFAULT NULL         COMMENT ''受理时间''',         'technician_id');
CALL add_repair_column('repair_start_at',  'datetime      DEFAULT NULL         COMMENT ''维修开始时间''',     'accepted_at');
CALL add_repair_column('repair_finish_at', 'datetime      DEFAULT NULL         COMMENT ''维修完成时间''',     'repair_start_at');
CALL add_repair_column('repair_log',       'text          DEFAULT NULL         COMMENT ''维修日志/工作记录''', 'repair_finish_at');
CALL add_repair_column('parts_used',       'varchar(500)  DEFAULT ''''          COMMENT ''使用配件''',         'repair_log');
CALL add_repair_column('repair_cost',      'decimal(10,2) DEFAULT NULL         COMMENT ''维修费用''',         'parts_used');

DROP PROCEDURE IF EXISTS add_repair_column;

-- ---------- 2. 创建维修人员角色 ----------
INSERT INTO sys_role
  (role_id, role_name, role_key, role_sort, data_scope,
   menu_check_strictly, dept_check_strictly, status, del_flag,
   create_by, create_time, remark)
SELECT 4, UNHEX('E7BBB4E4BFAEE4BABAE59398'), 'repair', 4, 1, 1, 1, '0', '0',
       'admin', sysdate(), UNHEX('E68AA5E4BFAEE5B7A5E58D95E58F97E79086E4B88EE7BBB4E4BFAEE7AEA1E79086E8A792E889B2')
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_id = 4);

-- ---------- 3. 清理旧菜单（幂等安全） ----------
DELETE FROM sys_role_menu WHERE menu_id IN (2500, 2501, 2502, 2510, 2511, 2512, 2513, 2514);
DELETE FROM sys_menu WHERE menu_id IN (2500, 2501, 2502, 2510, 2511, 2512, 2513, 2514);

-- ---------- 4. 插入维修人员菜单 ----------
-- 2500: 工单池 (C)
INSERT INTO sys_menu VALUES(2500, UNHEX('E5B7A5E58D95E6B1A0'), '2300', '3', 'repairPool', 'repair/technician/ticket-pool/index', '', 'RepairTicketPool', 1, 0, 'C', '0', '0', 'repair:tech:pool', 'guide', 'admin', sysdate(), '', NULL, UNHEX('E5BE85E58F97E79086E68AA5E4BFAEE5B7A5E58D95E6B1A0'));

-- 2501: 我的工单 (C)
INSERT INTO sys_menu VALUES(2501, UNHEX('E68891E79A84E5B7A5E58D95'), '2300', '4', 'repairMyTickets', 'repair/technician/my-tickets/index', '', 'RepairMyTickets', 1, 0, 'C', '0', '0', 'repair:tech:list', 'example', 'admin', sysdate(), '', NULL, UNHEX('E5B7B2E58F97E79086E7BBB4E4BFAEE5B7A5E58D95'));

-- 2502: 维修记录 (C)
INSERT INTO sys_menu VALUES(2502, UNHEX('E7BBB4E4BFAEE8AEB0E5BD95'), '2300', '5', 'repairHistory', 'repair/technician/history/index', '', 'RepairHistory', 1, 0, 'C', '0', '0', 'repair:tech:history', 'log', 'admin', sysdate(), '', NULL, UNHEX('E7BBB4E4BFAEE5AE8CE68890E58E86E58FB2'));

-- 2510: 查询工单 (F) - 工单池下
INSERT INTO sys_menu VALUES(2510, UNHEX('E69FA5E8AFA2E5B7A5E58D95'), '2500', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:tech:query', '#', 'admin', sysdate(), '', NULL, '');

-- 2511: 受理工单 (F) - 工单池下
INSERT INTO sys_menu VALUES(2511, UNHEX('E58F97E79086E5B7A5E58D95'), '2500', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:tech:accept', '#', 'admin', sysdate(), '', NULL, '');

-- 2512: 查询工单 (F) - 我的工单下
INSERT INTO sys_menu VALUES(2512, UNHEX('E69FA5E8AFA2E5B7A5E58D95'), '2501', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:tech:query', '#', 'admin', sysdate(), '', NULL, '');

-- 2513: 开始维修 (F) - 我的工单下
INSERT INTO sys_menu VALUES(2513, UNHEX('E5BC80E5A78BE7BBB4E4BFAA'), '2501', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:tech:start', '#', 'admin', sysdate(), '', NULL, '');

-- 2514: 完成维修 (F) - 我的工单下
INSERT INTO sys_menu VALUES(2514, UNHEX('E5AE8CE68890E7BBB4E4BFAA'), '2501', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:tech:complete', '#', 'admin', sysdate(), '', NULL, '');

-- ---------- 5. 角色-菜单关联 ----------
-- 维修人员核心菜单
INSERT INTO sys_role_menu VALUES
('4','2300'), ('4','2500'), ('4','2501'), ('4','2502'),
('4','2510'), ('4','2511'), ('4','2512'), ('4','2513'), ('4','2514');

-- 共享只读：智慧农业
INSERT INTO sys_role_menu VALUES
('4','2100'), ('4','2101'), ('4','2103');

-- 共享只读：农业快讯（不授予 2411 重新抓取）
INSERT INTO sys_role_menu VALUES
('4','2400'), ('4','2401'), ('4','2410');

-- ---------- 6. 角色-部门关联 ----------
INSERT IGNORE INTO sys_role_dept (role_id, dept_id) VALUES (4, 100);

-- ============================================================
-- 完成
-- ============================================================
