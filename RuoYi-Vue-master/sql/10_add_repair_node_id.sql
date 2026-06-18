-- ============================================================
-- Add node_id column to repair_request for heatmap integration
-- 执行前请先选择 ry-vue 数据库（USE ry-vue; 或GUI中切换）
-- 如果列/索引已存在，会提示错误，忽略即可
-- ============================================================

SET NAMES utf8mb4;
USE `ry-vue`;

ALTER TABLE repair_request
  ADD COLUMN node_id bigint(20) DEFAULT NULL COMMENT '关联大棚节点ID' AFTER user_id;

ALTER TABLE repair_request
  ADD INDEX idx_repair_node_status (node_id, status, del_flag);
