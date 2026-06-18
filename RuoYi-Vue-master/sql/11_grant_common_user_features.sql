-- ============================================================
-- Grant requested feature menus to the common role (role_id=2).
-- Run after the related menu install scripts have been applied.
-- This script is additive and does not delete or recreate menus.
-- ============================================================

SET NAMES utf8mb4;
USE `ry-vue`;

INSERT IGNORE INTO sys_role_menu (role_id, menu_id) VALUES
-- Smart agriculture: env monitor, sensor nodes, alarm center, heatmap, remote link
('2','2100'),('2','2101'),('2','2102'),('2','2103'),('2','2104'),
('2','2110'),('2','2111'),('2','2112'),('2','2113'),('2','2114'),
('2','2120'),('2','2121'),('2','2122'),
('2','2130'),('2','2131'),('2','2132'),('2','2133'),('2','2134'),('2','2135'),('2','2136'),('2','2137'),
-- Repair: self-service repair plus scoped repair management, excluding delete
('2','2300'),('2','2301'),('2','2302'),
('2','2310'),('2','2311'),('2','2312'),
('2','2320'),('2','2321'),
-- Agriculture news
('2','2400'),('2','2401'),('2','2410'),('2','2411');
