-- ============================================================
-- Repair worker role: role_id=4 + menus (2303/2330-2332) + test users
-- ASCII-only SQL (Chinese via UNHEX, safe on Windows). Run after 05_install_repair.sql
-- After run: re-login or flush Redis cache
-- ============================================================

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

DELETE FROM sys_role_menu WHERE role_id = 4;
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
    (4, 2300), (4, 2303), (4, 2330), (4, 2331), (4, 2332);

SET @pwd_123456 = '$2a$10$byDA8t8ArsucoMR7dyS0ue6LUPPECFSqLgK.LQeF/OsyWbd/EMlDy';

INSERT INTO sys_user (
    dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar,
    password, status, del_flag, login_ip, pwd_update_date, create_by, create_time, remark
)
SELECT * FROM (
    SELECT 100 AS dept_id, 'repair01' AS user_name, UNHEX('E7BBB4E4BFAEE59198E794B2') AS nick_name, '00' AS user_type,
           'repair01@local' AS email, '15800000021' AS phonenumber, '0' AS sex, '' AS avatar,
           @pwd_123456 AS password, '0' AS status, '0' AS del_flag, '' AS login_ip,
           sysdate() AS pwd_update_date, 'admin' AS create_by, sysdate() AS create_time,
           UNHEX('E7BBB4E4BFAEE59198E6B58BE8AF95E8B4A6E58FB7') AS remark
    UNION ALL
    SELECT 100, 'repair02', UNHEX('E7BBB4E4BFAEE59198E4B999'), '00', 'repair02@local', '15800000022', '1', '',
           @pwd_123456, '0', '0', '', sysdate(), 'admin', sysdate(),
           UNHEX('E7BBB4E4BFAEE59198E6B58BE8AF95E8B4A6E58FB7')
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM sys_user u WHERE u.user_name = seed.user_name);

UPDATE sys_user
SET nick_name = UNHEX('E7BBB4E4BFAEE59198E794B2'),
    remark = UNHEX('E7BBB4E4BFAEE59198E6B58BE8AF95E8B4A6E58FB7'),
    password = @pwd_123456, status = '0', del_flag = '0',
    pwd_update_date = sysdate(), update_time = sysdate()
WHERE user_name = 'repair01' AND del_flag = '0';

UPDATE sys_user
SET nick_name = UNHEX('E7BBB4E4BFAEE59198E4B999'),
    remark = UNHEX('E7BBB4E4BFAEE59198E6B58BE8AF95E8B4A6E58FB7'),
    password = @pwd_123456, status = '0', del_flag = '0',
    pwd_update_date = sysdate(), update_time = sysdate()
WHERE user_name = 'repair02' AND del_flag = '0';

DELETE ur FROM sys_user_role ur
INNER JOIN sys_user u ON u.user_id = ur.user_id
WHERE u.user_name IN ('repair01', 'repair02');

INSERT INTO sys_user_role (user_id, role_id)
SELECT u.user_id, 4
FROM sys_user u
WHERE u.user_name IN ('repair01', 'repair02');

DELETE up FROM sys_user_post up
INNER JOIN sys_user u ON u.user_id = up.user_id
WHERE u.user_name IN ('repair01', 'repair02');

INSERT INTO sys_user_post (user_id, post_id)
SELECT u.user_id, 4
FROM sys_user u
WHERE u.user_name IN ('repair01', 'repair02');
