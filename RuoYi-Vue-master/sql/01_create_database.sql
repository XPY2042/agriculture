-- ============================================================
-- 创建 RuoYi-Vue 数据库（库名与 application-druid.yml 中一致）
-- 建议执行顺序：
--   1) 本文件
--   2) 02_init_schema.sql
--   3) 03_install_agri.sql（可选，补全告警中心等）
--   4) 04_install_agri_remote.sql（可选，远程互联）
--   5) 05_install_repair.sql（可选，报修服务）
--   6) 07_install_repair_worker.sql（可选，维修员角色）
--   7) 90_seed_multi_users.sql（可选，批量测试账号）
-- 若菜单/角色中文乱码：08_fix_repair_worker_utf8.sql（维修员）或 93_restore_sidebar_menus.sql（全量恢复）
-- ============================================================

CREATE DATABASE IF NOT EXISTS `ry-vue`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE `ry-vue`;
