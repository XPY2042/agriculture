-- ============================================================
-- 账号 ry 密码重置为 123456（BCrypt，与 Spring Security 一致）
-- 在库 ry-vue 中执行：mysql -u root -p ry-vue < sql/91_reset_password_ry.sql
-- ============================================================
USE `ry-vue`;

-- 1) �����û� ry��ֻ������
UPDATE sys_user
SET password = '$2a$10$byDA8t8ArsucoMR7dyS0ue6LUPPECFSqLgK.LQeF/OsyWbd/EMlDy',
    pwd_update_date = sysdate(),
    update_time = sysdate()
WHERE user_name = 'ry'
  AND del_flag = '0';

-- 2) ��û�� ry�����类ɾ�����������û� + ��ͨ��ɫ(role_id=2) + ��λ(post_id=2����ٷ��ű�һ��)
INSERT INTO sys_user (
    dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar,
    password, status, del_flag, login_ip, pwd_update_date, create_by, create_time, remark
)
SELECT
    100, 'ry', '����', '00', 'ry@qq.com', '15666666666', '1', '',
    '$2a$10$byDA8t8ArsucoMR7dyS0ue6LUPPECFSqLgK.LQeF/OsyWbd/EMlDy',
    '0', '0', '', sysdate(), 'admin', sysdate(), '����Ա'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE user_name = 'ry');

INSERT IGNORE INTO sys_user_role (user_id, role_id)
SELECT u.user_id, 2
FROM sys_user u
WHERE u.user_name = 'ry';

INSERT IGNORE INTO sys_user_post (user_id, post_id)
SELECT u.user_id, 2
FROM sys_user u
WHERE u.user_name = 'ry';
