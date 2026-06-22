-- ============================================================
-- Safe fix for repair technician/admin schema on existing DBs.
-- Adds technician workflow columns used by RepairRequestMapper.xml.
-- ============================================================

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
USE `ry-vue`;

SET @repair_has_technician_id := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'repair_request'
    AND column_name = 'technician_id'
);
SET @repair_add_technician_id_sql := IF(
  @repair_has_technician_id = 0,
  'ALTER TABLE repair_request ADD COLUMN technician_id bigint(20) DEFAULT NULL COMMENT ''受理维修人员ID'' AFTER admin_reply',
  'SELECT 1'
);
PREPARE stmt FROM @repair_add_technician_id_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @repair_has_accepted_at := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'repair_request'
    AND column_name = 'accepted_at'
);
SET @repair_add_accepted_at_sql := IF(
  @repair_has_accepted_at = 0,
  'ALTER TABLE repair_request ADD COLUMN accepted_at datetime DEFAULT NULL COMMENT ''受理时间'' AFTER technician_id',
  'SELECT 1'
);
PREPARE stmt FROM @repair_add_accepted_at_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @repair_has_repair_start_at := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'repair_request'
    AND column_name = 'repair_start_at'
);
SET @repair_add_repair_start_at_sql := IF(
  @repair_has_repair_start_at = 0,
  'ALTER TABLE repair_request ADD COLUMN repair_start_at datetime DEFAULT NULL COMMENT ''维修开始时间'' AFTER accepted_at',
  'SELECT 1'
);
PREPARE stmt FROM @repair_add_repair_start_at_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @repair_has_repair_finish_at := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'repair_request'
    AND column_name = 'repair_finish_at'
);
SET @repair_add_repair_finish_at_sql := IF(
  @repair_has_repair_finish_at = 0,
  'ALTER TABLE repair_request ADD COLUMN repair_finish_at datetime DEFAULT NULL COMMENT ''维修完成时间'' AFTER repair_start_at',
  'SELECT 1'
);
PREPARE stmt FROM @repair_add_repair_finish_at_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @repair_has_repair_log := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'repair_request'
    AND column_name = 'repair_log'
);
SET @repair_add_repair_log_sql := IF(
  @repair_has_repair_log = 0,
  'ALTER TABLE repair_request ADD COLUMN repair_log text COMMENT ''维修日志/工作记录'' AFTER repair_finish_at',
  'SELECT 1'
);
PREPARE stmt FROM @repair_add_repair_log_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @repair_has_parts_used := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'repair_request'
    AND column_name = 'parts_used'
);
SET @repair_add_parts_used_sql := IF(
  @repair_has_parts_used = 0,
  'ALTER TABLE repair_request ADD COLUMN parts_used varchar(500) DEFAULT '''' COMMENT ''使用配件'' AFTER repair_log',
  'SELECT 1'
);
PREPARE stmt FROM @repair_add_parts_used_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @repair_has_repair_cost := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'repair_request'
    AND column_name = 'repair_cost'
);
SET @repair_add_repair_cost_sql := IF(
  @repair_has_repair_cost = 0,
  'ALTER TABLE repair_request ADD COLUMN repair_cost decimal(10,2) DEFAULT NULL COMMENT ''维修费用'' AFTER parts_used',
  'SELECT 1'
);
PREPARE stmt FROM @repair_add_repair_cost_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @repair_has_technician_idx := (
  SELECT COUNT(*)
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'repair_request'
    AND index_name = 'idx_repair_technician_status'
);
SET @repair_add_technician_idx_sql := IF(
  @repair_has_technician_idx = 0,
  'ALTER TABLE repair_request ADD INDEX idx_repair_technician_status (technician_id, status, del_flag)',
  'SELECT 1'
);
PREPARE stmt FROM @repair_add_technician_idx_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
