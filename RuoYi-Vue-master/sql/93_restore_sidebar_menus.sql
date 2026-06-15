-- ============================================================
-- RESTORE sidebar menus (run when sidebar empty or garbled)
-- Uses UNHEX (ASCII-only SQL file, safe on Windows)
-- Includes: agri + alarm + remote + user mgmt + repair + news
-- After run: re-login or flush Redis cache
-- ============================================================

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

ALTER TABLE sys_menu CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

DELETE FROM sys_role_menu WHERE menu_id IN (2100,2101,2102,2103,2110,2111,2112,2113,2114,2120,2121,2122,2130,2131,2132,2133,2134,2135,2200,100,1000,1001,1002,1003,1004,1005,1006,2300,2301,2302,2303,2310,2311,2312,2320,2321,2322,2330,2331,2332,2400,2401,2410,2411);
DELETE FROM sys_menu WHERE menu_id IN (2100,2101,2102,2103,2110,2111,2112,2113,2114,2120,2121,2122,2130,2131,2132,2133,2134,2135,2200,100,1000,1001,1002,1003,1004,1005,1006,2300,2301,2302,2303,2310,2311,2312,2320,2321,2322,2330,2331,2332,2400,2401,2410,2411);

INSERT INTO sys_menu VALUES(2100, UNHEX('E699BAE685A7E5869CE4B89A'), '0', '5', 'agri', NULL, '', '', 1, 0, 'M', '0', '0', '', 'international', 'admin', sysdate(), '', NULL, UNHEX('E699BAE685A7E5869CE4B89AE79BAEE5BD95'));
INSERT INTO sys_menu VALUES(2101, UNHEX('E78EAFE5A283E79B91E6B58B'), '2100', '1', 'agriEnv', 'agri/monitor/index', '', 'AgriEnvMonitor', 1, 0, 'C', '0', '0', 'agri:monitor:view', 'chart', 'admin', sysdate(), '', NULL, UNHEX('E5AE9EE697B6E8AFBBE695B0E4B88EE8B68BE58ABF'));
INSERT INTO sys_menu VALUES(2102, UNHEX('E4BCA0E6849FE88A82E782B9'), '2100', '2', 'agriSensor', 'agri/node/index', '', 'AgriSensorNode', 1, 0, 'C', '0', '0', 'agri:node:list', 'server', 'admin', sysdate(), '', NULL, UNHEX('E8AEBEE5A487E6A1A3E6A188E7BBB4E68AA4'));
INSERT INTO sys_menu VALUES(2103, UNHEX('E5918AE8ADA6E4B8ADE5BF83'), '2100', '3', 'agriAlarm', 'agri/alarm/index', '', 'AgriAlarmCenter', 1, 0, 'C', '0', '0', 'agri:monitor:view', 'message', 'admin', sysdate(), '', NULL, UNHEX('E4BCA0E6849FE99888E580BCE8B685E99990E5918AE8ADA6'));
INSERT INTO sys_menu VALUES(2110, UNHEX('E88A82E782B9E69FA5E8AFA2'), '2102', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:node:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2111, UNHEX('E88A82E782B9E696B0E5A29E'), '2102', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:node:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2112, UNHEX('E88A82E782B9E4BFAEE694B9'), '2102', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:node:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2113, UNHEX('E88A82E782B9E588A0E999A4'), '2102', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:node:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2114, UNHEX('E695B0E68DAEE4B88AE68AA5'), '2101', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:monitor:ingest', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2120, UNHEX('E5918AE8ADA6E69FA5E8AFA2'), '2103', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:monitor:view', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2121, UNHEX('E5918AE8ADA6E7A1AEE8AEA4'), '2103', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:monitor:view', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2122, UNHEX('E5918AE8ADA6E689ABE68F8F'), '2103', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:monitor:view', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2130, UNHEX('E8BF9CE7A88BE4BA92E88194'), '2100', '4', 'agriRemote', 'agri/remote/index', '', 'AgriRemoteLink', 1, 0, 'C', '0', '0', 'agri:remote:view', 'link', 'admin', sysdate(), '', NULL, UNHEX('E8BF9CE7A88BE7BD91E7BB9CE6A380E6B58BE4B88EE8AEBEE5A487E6938DE4BD9C'));
INSERT INTO sys_menu VALUES(2131, UNHEX('E4BA92E88194E69FA5E8AFA2'), '2130', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:remote:view', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2132, UNHEX('E8BF9CE7A88BE6938DE4BD9C'), '2130', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:remote:operate', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2133, UNHEX('E587BAE7BD91E6A380E6B58B'), '2130', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:remote:view', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2134, UNHEX('E8BF9CE7A88BE68B89E58F96'), '2130', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:remote:operate', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2135, UNHEX('E6A8A1E68B9FE4B88AE68AA5'), '2130', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:remote:operate', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2200, UNHEX('E7B3BBE7BB9FE7AEA1E79086'), '0', '5', 'system', NULL, '', '', 1, 0, 'M', '0', '0', '', 'system', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(100, UNHEX('E794A8E688B7E7AEA1E79086'), '2200', '1', 'user', 'system/user/index', '', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'admin', sysdate(), '', NULL, UNHEX('E7B3BBE7BB9FE794A8E688B7E7BBB4E68AA4'));
INSERT INTO sys_menu VALUES(1000, UNHEX('E794A8E688B7E69FA5E8AFA2'), '100', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(1001, UNHEX('E794A8E688B7E696B0E5A29E'), '100', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(1002, UNHEX('E794A8E688B7E4BFAEE694B9'), '100', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(1003, UNHEX('E794A8E688B7E588A0E999A4'), '100', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(1004, UNHEX('E794A8E688B7E5AFBCE587BA'), '100', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(1005, UNHEX('E794A8E688B7E5AFBCE585A5'), '100', '6', '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(1006, UNHEX('E9878DE7BDAEE5AF86E7A081'), '100', '7', '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2300, UNHEX('E68AA5E4BFAEE69C8DE58AA1'), '0', '6', 'repair', NULL, '', '', 1, 0, 'M', '0', '0', '', 'tool', 'admin', sysdate(), '', NULL, UNHEX('E68AA5E4BFAEE69C8DE58AA1E79BAEE5BD95'));
INSERT INTO sys_menu VALUES(2301, UNHEX('E68891E79A84E68AA5E4BFAE'), '2300', '1', 'repairApply', 'repair/apply/index', '', 'RepairApply', 1, 0, 'C', '0', '0', 'repair:apply:list', 'form', 'admin', sysdate(), '', NULL, UNHEX('E794A8E688B7E68F90E4BAA4E68AA5E4BFAE'));
INSERT INTO sys_menu VALUES(2302, UNHEX('E68AA5E4BFAEE7AEA1E79086'), '2300', '2', 'repairAdmin', 'repair/admin/index', '', 'RepairAdmin', 1, 0, 'C', '0', '0', 'repair:admin:list', 'list', 'admin', sysdate(), '', NULL, UNHEX('E7AEA1E79086E59198E5A484E79086E68AA5E4BFAE'));
INSERT INTO sys_menu VALUES(2310, UNHEX('E68AA5E4BFAEE69FA5E8AFA2'), '2301', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:apply:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2311, UNHEX('E68F90E4BAA4E68AA5E4BFAE'), '2301', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:apply:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2312, UNHEX('E58F96E6B688E68AA5E4BFAE'), '2301', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:apply:cancel', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2320, UNHEX('E7AEA1E79086E69FA5E8AFA2'), '2302', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:admin:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2321, UNHEX('E5A484E79086E68AA5E4BFAE'), '2302', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:admin:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2322, UNHEX('E588A0E999A4E68AA5E4BFAE'), '2302', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:admin:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2303, UNHEX('E7BBB4E4BFAEE4BBBBE58AA1'), '2300', '3', 'repairWorker', 'repair/worker/index', '', 'RepairWorker', 1, 0, 'C', '0', '0', 'repair:worker:list', 'job', 'admin', sysdate(), '', NULL, UNHEX('E7BBB4E4BFAEE59198E697A5E79C8BE5B9B6E5A484E79086E68AA5E4BFAE'));
INSERT INTO sys_menu VALUES(2330, UNHEX('E4BBBBE58AA1E69FA5E8AFA2'), '2303', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:worker:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2331, UNHEX('E58F97E79086E68AA5E4BFAE'), '2303', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:worker:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2332, UNHEX('E5BE85E58F97E79086E7BB9FE8AEA1'), '2303', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:worker:count', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2400, UNHEX('E5869CE4B89AE5BFABE8AEAF'), '0', '7', 'agriFlash', NULL, '', '', 1, 0, 'M', '0', '0', '', 'message', 'admin', sysdate(), '', NULL, UNHEX('E5869CE4B89AE8B584E8AEAFE5BFABE8AEAFE79BAEE5BD95'));
INSERT INTO sys_menu VALUES(2401, UNHEX('E5869CE4B89AE696B0E997BB'), '2400', '1', 'newsFeed', 'agri/news/index', '', 'AgriNewsFeed', 1, 0, 'C', '0', '0', 'agri:news:list', 'documentation', 'admin', sysdate(), '', NULL, UNHEX('525353E68A93E58F96E5869CE4B89AE696B0E997BB'));
INSERT INTO sys_menu VALUES(2410, UNHEX('E696B0E997BBE69FA5E8AFA2'), '2401', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:news:list', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2411, UNHEX('E9878DE696B0E68A93E58F96'), '2401', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:news:refresh', '#', 'admin', sysdate(), '', NULL, '');

-- role_id=2 monitor: view menus
INSERT IGNORE INTO sys_role_menu VALUES
('2','2100'),('2','2101'),('2','2102'),('2','2103'),
('2','2110'),('2','2111'),('2','2112'),('2','2113'),('2','2114'),
('2','2120'),('2','2121'),('2','2122'),
('2','2130'),('2','2131'),('2','2132'),('2','2133'),('2','2134'),('2','2135'),
('2','2300'),('2','2301'),('2','2310'),('2','2311'),('2','2312'),
('2','2400'),('2','2401'),('2','2410');

-- role_id=3 agri_admin
INSERT IGNORE INTO sys_role_menu VALUES
('3','2200'),('3','100'),('3','1000'),('3','1001'),('3','1002'),('3','1003'),('3','1004'),('3','1005'),('3','1006'),
('3','2100'),('3','2101'),('3','2102'),('3','2103'),
('3','2110'),('3','2111'),('3','2112'),('3','2113'),('3','2114'),
('3','2120'),('3','2121'),('3','2122'),
('3','2130'),('3','2131'),('3','2132'),('3','2133'),('3','2134'),('3','2135'),
('3','2300'),('3','2302'),('3','2320'),('3','2321'),('3','2322'),
('3','2400'),('3','2401'),('3','2410'),('3','2411');

-- role_id=1 admin (super admin bypasses RBAC; keep grants in sync)
INSERT IGNORE INTO sys_role_menu VALUES
('1','2200'),('1','100'),('1','1000'),('1','1001'),('1','1002'),('1','1003'),('1','1004'),('1','1005'),('1','1006'),
('1','2100'),('1','2101'),('1','2102'),('1','2103'),
('1','2110'),('1','2111'),('1','2112'),('1','2113'),('1','2114'),
('1','2120'),('1','2121'),('1','2122'),
('1','2130'),('1','2131'),('1','2132'),('1','2133'),('1','2134'),('1','2135'),
('1','2300'),('1','2301'),('1','2302'),('1','2310'),('1','2311'),('1','2312'),('1','2320'),('1','2321'),('1','2322'),
('1','2400'),('1','2401'),('1','2410'),('1','2411');

-- role_id=4 repair_worker
INSERT INTO sys_role (
    role_id, role_name, role_key, role_sort, data_scope,
    menu_check_strictly, dept_check_strictly, status, del_flag,
    create_by, create_time, remark
)
SELECT
    4, UNHEX('E7BBB4E4BFAEE59198'), 'repair_worker', 4, 1,
    1, 1, '0', '0',
    'admin', sysdate(), UNHEX('E68EA5E694B6E5B9B6E5A484E79086E794A8E688B7E68AA5E4BFAEE4BBBBE58AA1')
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_id = 4);

INSERT IGNORE INTO sys_role_menu VALUES
('4','2300'),('4','2303'),('4','2330'),('4','2331'),('4','2332');
