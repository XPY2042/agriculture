-- ============================================================
-- 报表统计：菜单与权限（menu_id 2600-2603）
-- 默认库名取 application-druid.yml 中的 ry-vue
-- ============================================================

SET NAMES utf8mb4;
USE `ry-vue`;

DELETE FROM sys_role_menu WHERE menu_id IN (2600,2601,2602,2603);
DELETE FROM sys_menu WHERE menu_id IN (2600,2601,2602,2603);

INSERT INTO sys_menu VALUES(2600, '报表统计', '2100', '6', 'reportStatistics', 'report/statistics/index', '', 'ReportStatistics', 1, 0, 'C', '0', '0', 'report:statistics:view', 'chart', 'admin', sysdate(), '', NULL, '维修管理与告警中心统计报表');
INSERT INTO sys_menu VALUES(2601, '报表查询', '2600', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'report:statistics:view', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2602, '维修报表', '2600', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:admin:list', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2603, '告警报表', '2600', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:monitor:view', '#', 'admin', sysdate(), '', NULL, '');

INSERT IGNORE INTO sys_role_menu (role_id, menu_id) VALUES
('1','2600'), ('1','2601'), ('1','2602'), ('1','2603'),
('3','2600'), ('3','2601'), ('3','2602'), ('3','2603'),
('4','2600'), ('4','2601'), ('4','2602'), ('4','2603');
