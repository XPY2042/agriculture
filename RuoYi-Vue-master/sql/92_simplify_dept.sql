-- ============================================================
-- �������ಿ�ţ������ܹ�˾����ɳ�ֹ�˾���������ţ���ͳһ����ũҵ�������
-- �ڿ� ry-vue ��ִ�У�mysql -u root -p ry-vue < sql/92_simplify_dept.sql
-- ============================================================
SET NAMES utf8mb4;
USE `ry-vue`;

-- ���û�Ǩ����ũҵ�������
UPDATE sys_user SET dept_id = 100 WHERE dept_id IS NULL OR dept_id <> 100;

-- ������ɫ-���Ź����еĶ��ಿ��
DELETE FROM sys_role_dept WHERE dept_id <> 100;
INSERT IGNORE INTO sys_role_dept (role_id, dept_id) VALUES (2, 100), (3, 100);

-- ɾ�����ಿ�ţ����� dept_id=100��
DELETE FROM sys_dept WHERE dept_id <> 100;
