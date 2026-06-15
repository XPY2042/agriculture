-- ============================================================
-- 热力图菜单（menu_id 2104）- 在智慧农业下新增"热力地图"
-- 需先执行 03_install_agri.sql 创建智慧农业目录
-- ============================================================

SET NAMES utf8mb4;

-- 菜单
DELETE FROM sys_role_menu WHERE menu_id IN (2104, 2130, 2131);
DELETE FROM sys_menu WHERE menu_id IN (2104, 2130, 2131);

INSERT INTO sys_menu VALUES(2104, '热力地图', '2100', '4', 'agriHeatmap', 'agri/heatmap/index', '', 'AgriHeatmap', 1, 0, 'C', '0', '0', 'agri:monitor:view', 'component', 'admin', sysdate(), '', NULL, '大棚告警热力图总览（需环境监测权限）');

INSERT INTO sys_menu VALUES(2130, '热力查询', '2104', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:monitor:view', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2131, '告警确认', '2104', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:monitor:view', '#', 'admin', sysdate(), '', NULL, '');

-- 授权给角色
INSERT INTO sys_role_menu VALUES ('2', '2104');  -- 普通角色
INSERT INTO sys_role_menu VALUES ('2', '2130');
INSERT INTO sys_role_menu VALUES ('2', '2131');

INSERT INTO sys_role_menu VALUES ('3', '2104');  -- 农业管理员
INSERT INTO sys_role_menu VALUES ('3', '2130');
INSERT INTO sys_role_menu VALUES ('3', '2131');

INSERT INTO sys_role_menu VALUES ('4', '2104');  -- 维修员（如需）
INSERT INTO sys_role_menu VALUES ('4', '2130');
INSERT INTO sys_role_menu VALUES ('4', '2131');
