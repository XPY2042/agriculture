-- ============================================================
-- Revoke repair admin menus from the common role (role_id=2).
-- Keeps self-service repair menus for ordinary users.
-- ============================================================

SET NAMES utf8mb4;
USE `ry-vue`;

DELETE FROM sys_role_menu
WHERE role_id = '2'
  AND menu_id IN (2302, 2320, 2321, 2322);
