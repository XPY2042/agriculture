-- Agri news menus only (menu_id 2400-2411) - use 93_restore_sidebar_menus.sql if sidebar is empty
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

DELETE FROM sys_role_menu WHERE menu_id IN (2400, 2401, 2410, 2411);
DELETE FROM sys_menu WHERE menu_id IN (2400, 2401, 2410, 2411);

INSERT INTO sys_menu VALUES(2400, UNHEX('E5869CE4B89AE5BFABE8AEAF'), '0', '7', 'agriFlash', NULL, '', '', 1, 0, 'M', '0', '0', '', 'message', 'admin', sysdate(), '', NULL, UNHEX('E5869CE4B89AE8B584E8AEAFE5BFABE8AEAFE79BAEE5BD95'));
INSERT INTO sys_menu VALUES(2401, UNHEX('E5869CE4B89AE696B0E997BB'), '2400', '1', 'newsFeed', 'agri/news/index', '', 'AgriNewsFeed', 1, 0, 'C', '0', '0', 'agri:news:list', 'documentation', 'admin', sysdate(), '', NULL, UNHEX('525353E68A93E58F96E5869CE4B89AE696B0E997BB'));
INSERT INTO sys_menu VALUES(2410, UNHEX('E696B0E997BBE69FA5E8AFA2'), '2401', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:news:list', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2411, UNHEX('E9878DE696B0E68A93E58F96'), '2401', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:news:refresh', '#', 'admin', sysdate(), '', NULL, '');

INSERT IGNORE INTO sys_role_menu VALUES ('2', '2400'), ('2', '2401'), ('2', '2410');
INSERT IGNORE INTO sys_role_menu VALUES ('1', '2400'), ('1', '2401'), ('1', '2410'), ('1', '2411');
INSERT IGNORE INTO sys_role_menu VALUES ('3', '2400'), ('3', '2401'), ('3', '2410'), ('3', '2411');
