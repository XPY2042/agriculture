-- Fix garbled repair worker role / menu / user text (ASCII-only, safe on Windows)
-- If sidebar is empty run 93_restore_sidebar_menus.sql instead
-- After run: re-login or flush Redis cache
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

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

UPDATE sys_role
SET role_name = UNHEX('E7BBB4E4BFAEE59198'),
    remark = UNHEX('E68EA5E694B6E5B9B6E5A484E79086E794A8E688B7E68AA5E4BFAEE4BBBBE58AA1')
WHERE role_id = 4 OR role_key = 'repair_worker';

DELETE FROM sys_role_menu WHERE menu_id IN (2303, 2330, 2331, 2332);
DELETE FROM sys_menu WHERE menu_id IN (2303, 2330, 2331, 2332);

INSERT INTO sys_menu VALUES(2303, UNHEX('E7BBB4E4BFAEE4BBBBE58AA1'), '2300', '3', 'repairWorker', 'repair/worker/index', '', 'RepairWorker', 1, 0, 'C', '0', '0', 'repair:worker:list', 'job', 'admin', sysdate(), '', NULL, UNHEX('E7BBB4E4BFAEE59198E697A5E79C8BE5B9B6E5A484E79086E68AA5E4BFAE'));
INSERT INTO sys_menu VALUES(2330, UNHEX('E4BBBBE58AA1E69FA5E8AFA2'), '2303', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:worker:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2331, UNHEX('E58F97E79086E68AA5E4BFAE'), '2303', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:worker:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2332, UNHEX('E5BE85E58F97E79086E7BB9FE8AEA1'), '2303', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:worker:count', '#', 'admin', sysdate(), '', NULL, '');

INSERT IGNORE INTO sys_role_menu VALUES
('4','2300'),('4','2303'),('4','2330'),('4','2331'),('4','2332');

UPDATE sys_user
SET nick_name = UNHEX('E7BBB4E4BFAEE59198E794B2'),
    remark = UNHEX('E7BBB4E4BFAEE59198E6B58BE8AF95E8B4A6E58FB7')
WHERE user_name = 'repair01' AND del_flag = '0';

UPDATE sys_user
SET nick_name = UNHEX('E7BBB4E4BFAEE59198E4B999'),
    remark = UNHEX('E7BBB4E4BFAEE59198E6B58BE8AF95E8B4A6E58FB7')
WHERE user_name = 'repair02' AND del_flag = '0';
