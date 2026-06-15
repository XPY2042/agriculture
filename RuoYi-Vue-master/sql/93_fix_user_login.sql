-- �޸�δ�����ɫ���û���������ͨ��ɫ role_id=2�����Ա�ȣ�
-- �ڿ� ry-vue ��ִ�У�mysql -u root -p ry-vue < sql/93_fix_user_login.sql
USE `ry-vue`;

INSERT IGNORE INTO sys_user_role (user_id, role_id)
SELECT u.user_id, 2
FROM sys_user u
WHERE u.del_flag = '0'
  AND u.user_id <> 1
  AND NOT EXISTS (SELECT 1 FROM sys_user_role ur WHERE ur.user_id = u.user_id);
