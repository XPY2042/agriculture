-- Restore default test account ry (was soft-deleted) with password 123456
USE `ry-vue`;

UPDATE sys_user
SET del_flag = '0',
    status = '0',
    password = '$2a$10$byDA8t8ArsucoMR7dyS0ue6LUPPECFSqLgK.LQeF/OsyWbd/EMlDy',
    pwd_update_date = sysdate(),
    update_time = sysdate()
WHERE user_name = 'ry';

INSERT IGNORE INTO sys_user_role (user_id, role_id)
SELECT u.user_id, 2 FROM sys_user u WHERE u.user_name = 'ry' AND u.del_flag = '0';
