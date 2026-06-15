-- ============================================================
-- ??????????????????????? + ?????????
-- ??? ry-vue ????��?mysql -u root -p ry-vue < sql/90_seed_multi_users.sql
-- ??��????????????????? Redis ?��????????��
--
-- ????????? 123456??BCrypt???? Spring Security ????
-- ???????????? admin??user_id=1??????????????
-- ============================================================

SET NAMES utf8mb4;
USE `ry-vue`;

-- BCrypt(123456)
SET @pwd_123456 = '$2a$10$byDA8t8ArsucoMR7dyS0ue6LUPPECFSqLgK.LQeF/OsyWbd/EMlDy';

-- ------------------------------------------------------------
-- 1. ??????????????role_id=3???????????????????????
--    ????????role_id=2???????????????????????????????????
-- ------------------------------------------------------------
INSERT INTO sys_role (
    role_id, role_name, role_key, role_sort, data_scope,
    menu_check_strictly, dept_check_strictly, status, del_flag,
    create_by, create_time, remark
)
SELECT
    3, '???????', 'agri_admin', 3, 1,
    1, 1, '0', '0',
    'admin', sysdate(), '??????????????????????��??????????'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_id = 3);

DELETE FROM sys_role_menu WHERE role_id = 3;
INSERT IGNORE INTO sys_role_menu (role_id, menu_id) VALUES
    (3, 100), (3, 1000), (3, 1001), (3, 1002), (3, 1003), (3, 1004), (3, 1005), (3, 1006),
    (3, 2100), (3, 2101), (3, 2102),
    (3, 2110), (3, 2111), (3, 2112), (3, 2113), (3, 2114),
    (3, 2300), (3, 2302), (3, 2320), (3, 2321), (3, 2322);

INSERT IGNORE INTO sys_role_dept (role_id, dept_id) VALUES
    (3, 100);

-- ????????????????????????��?
DELETE FROM sys_role_menu WHERE role_id = 2;
INSERT IGNORE INTO sys_role_menu (role_id, menu_id) VALUES
    (2, 2100), (2, 2101), (2, 2102), (2, 2110);

-- ------------------------------------------------------------
-- 2. ??????????????????????????????????????
--    user_name | nick_name   | dept | role | post | ???
--    agri_admin| ???????  | 107  | 3    | 2    | ???????
--    manager01 | ???????    | 103  | 3    | 2    | ???????
--    monitor01 | ??????    | 105  | 2    | 4    | ??????
--    monitor02 | ??????    | 105  | 2    | 4    | ??????
--    monitor03 | ??????    | 108  | 2    | 4    | ??????
-- ------------------------------------------------------------
INSERT INTO sys_user (
    dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar,
    password, status, del_flag, login_ip, pwd_update_date, create_by, create_time, remark
)
SELECT * FROM (
    SELECT 100 AS dept_id, 'agri_admin' AS user_name, '???????' AS nick_name, '00' AS user_type,
           'agri_admin@local' AS email, '15800000001' AS phonenumber, '0' AS sex, '' AS avatar,
           @pwd_123456 AS password, '0' AS status, '0' AS del_flag, '' AS login_ip,
           sysdate() AS pwd_update_date, 'admin' AS create_by, sysdate() AS create_time,
           '?????????' AS remark
    UNION ALL
    SELECT 100, 'manager01', '???????', '00', 'manager01@local', '15800000002', '0', '',
           @pwd_123456, '0', '0', '', sysdate(), 'admin', sysdate(), '?????????'
    UNION ALL
    SELECT 100, 'monitor01', '??????', '00', 'monitor01@local', '15800000011', '1', '',
           @pwd_123456, '0', '0', '', sysdate(), 'admin', sysdate(), '?????????'
    UNION ALL
    SELECT 100, 'monitor02', '??????', '00', 'monitor02@local', '15800000012', '1', '',
           @pwd_123456, '0', '0', '', sysdate(), 'admin', sysdate(), '?????????'
    UNION ALL
    SELECT 100, 'monitor03', '??????', '00', 'monitor03@local', '15800000013', '0', '',
           @pwd_123456, '0', '0', '', sysdate(), 'admin', sysdate(), '?????????'
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM sys_user u WHERE u.user_name = seed.user_name);

UPDATE sys_user
SET password = @pwd_123456,
    status = '0',
    del_flag = '0',
    pwd_update_date = sysdate(),
    update_time = sysdate()
WHERE user_name IN ('agri_admin', 'manager01', 'monitor01', 'monitor02', 'monitor03')
  AND del_flag = '0';

-- ???��?????? ry?????????????????? 123456
UPDATE sys_user
SET password = @pwd_123456,
    status = '0',
    del_flag = '0',
    pwd_update_date = sysdate(),
    update_time = sysdate()
WHERE user_name = 'ry'
  AND del_flag = '0';

INSERT INTO sys_user (
    dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar,
    password, status, del_flag, login_ip, pwd_update_date, create_by, create_time, remark
)
SELECT
    100, 'ry', '????', '00', 'ry@local', '15666666666', '1', '',
    @pwd_123456, '0', '0', '', sysdate(), 'admin', sysdate(), '?????????'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE user_name = 'ry');

-- ------------------------------------------------------------
-- 3. ???-????????-??��
-- ------------------------------------------------------------
DELETE ur FROM sys_user_role ur
INNER JOIN sys_user u ON u.user_id = ur.user_id
WHERE u.user_name IN ('agri_admin', 'manager01', 'monitor01', 'monitor02', 'monitor03', 'ry');

INSERT INTO sys_user_role (user_id, role_id)
SELECT u.user_id, r.role_id
FROM sys_user u
JOIN (
    SELECT 'agri_admin' AS user_name, 3 AS role_id
    UNION ALL SELECT 'manager01', 3
    UNION ALL SELECT 'monitor01', 2
    UNION ALL SELECT 'monitor02', 2
    UNION ALL SELECT 'monitor03', 2
    UNION ALL SELECT 'ry', 2
) AS r ON r.user_name = u.user_name;

DELETE up FROM sys_user_post up
INNER JOIN sys_user u ON u.user_id = up.user_id
WHERE u.user_name IN ('agri_admin', 'manager01', 'monitor01', 'monitor02', 'monitor03', 'ry');

INSERT INTO sys_user_post (user_id, post_id)
SELECT u.user_id, p.post_id
FROM sys_user u
JOIN (
    SELECT 'agri_admin' AS user_name, 2 AS post_id
    UNION ALL SELECT 'manager01', 2
    UNION ALL SELECT 'monitor01', 4
    UNION ALL SELECT 'monitor02', 4
    UNION ALL SELECT 'monitor03', 4
    UNION ALL SELECT 'ry', 4
) AS p ON p.user_name = u.user_name;
