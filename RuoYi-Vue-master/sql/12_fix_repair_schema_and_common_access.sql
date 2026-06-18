-- ============================================================
-- Safe repair/news/remote fix for existing databases.
-- 1. Selects the default project DB: ry-vue
-- 2. Ensures repair_request has the node_id column expected by code
-- 3. Ensures remote/repair/news menus exist
-- 4. Grants the common role (role_id=2) access to remote/news and self-service repair menus
-- ============================================================

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
USE `ry-vue`;

CREATE TABLE IF NOT EXISTS repair_request (
  request_id       bigint(20)      NOT NULL AUTO_INCREMENT    COMMENT '报修ID',
  user_id          bigint(20)      NOT NULL                   COMMENT '报修用户ID',
  node_id          bigint(20)      DEFAULT NULL               COMMENT '关联大棚节点ID',
  title            varchar(100)    NOT NULL DEFAULT ''        COMMENT '报修原因',
  description      varchar(1000)   DEFAULT ''                 COMMENT '问题描述',
  location         varchar(200)    DEFAULT ''                 COMMENT '报修地点',
  contact_phone    varchar(20)     DEFAULT ''                 COMMENT '联系电话',
  status           char(1)         DEFAULT '0'                COMMENT '0待受理 1处理中 2已完成 3已取消',
  admin_reply      varchar(500)    DEFAULT ''                 COMMENT '管理员回复',
  del_flag         char(1)         DEFAULT '0'                COMMENT '0存在 2删除',
  create_by        varchar(64)     DEFAULT ''                 COMMENT '创建者',
  create_time      datetime                                   COMMENT '创建时间',
  update_by        varchar(64)     DEFAULT ''                 COMMENT '更新者',
  update_time      datetime                                   COMMENT '更新时间',
  remark           varchar(500)    DEFAULT NULL               COMMENT '备注',
  PRIMARY KEY (request_id),
  KEY idx_repair_user_id (user_id),
  KEY idx_repair_status (status),
  KEY idx_repair_node_status (node_id, status, del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修申请';

SET @repair_has_node_id := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'repair_request'
    AND column_name = 'node_id'
);
SET @repair_add_node_id_sql := IF(
  @repair_has_node_id = 0,
  'ALTER TABLE repair_request ADD COLUMN node_id bigint(20) DEFAULT NULL COMMENT ''关联大棚节点ID'' AFTER user_id',
  'SELECT 1'
);
PREPARE stmt FROM @repair_add_node_id_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @repair_has_node_idx := (
  SELECT COUNT(*)
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'repair_request'
    AND index_name = 'idx_repair_node_status'
);
SET @repair_add_node_idx_sql := IF(
  @repair_has_node_idx = 0,
  'ALTER TABLE repair_request ADD INDEX idx_repair_node_status (node_id, status, del_flag)',
  'SELECT 1'
);
PREPARE stmt FROM @repair_add_node_idx_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS agri_remote_command (
  command_id       bigint(20)      NOT NULL AUTO_INCREMENT    COMMENT '指令ID',
  node_id          bigint(20)      NOT NULL                   COMMENT '节点ID',
  command_type     varchar(32)     NOT NULL                   COMMENT '指令类型',
  command_label    varchar(64)     NOT NULL DEFAULT ''        COMMENT '指令名称',
  status           char(1)         NOT NULL DEFAULT '0'       COMMENT '0待执行 1成功 2失败',
  result_message   varchar(500)    DEFAULT ''                 COMMENT '执行结果',
  create_by        varchar(64)     DEFAULT ''                 COMMENT '操作人',
  create_time      datetime        DEFAULT NULL               COMMENT '操作时间',
  execute_time     datetime        DEFAULT NULL               COMMENT '执行完成时间',
  PRIMARY KEY (command_id),
  KEY idx_agri_remote_cmd_node (node_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='远程设备操作指令';

INSERT IGNORE INTO sys_menu VALUES
(2100, '智慧农业', '0', '5', 'agri', NULL, '', '', 1, 0, 'M', '0', '0', '', 'international', 'admin', sysdate(), '', NULL, '智慧农业目录'),
(2130, '远程互联', '2100', '4', 'agriRemote', 'agri/remote/index', '', 'AgriRemoteLink', 1, 0, 'C', '0', '0', 'agri:remote:view', 'link', 'admin', sysdate(), '', NULL, '远程网络检测与设备操作'),
(2131, '互联查询', '2130', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:remote:view', '#', 'admin', sysdate(), '', NULL, ''),
(2132, '远程操作', '2130', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:remote:operate', '#', 'admin', sysdate(), '', NULL, ''),
(2133, '出网检测', '2130', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:remote:view', '#', 'admin', sysdate(), '', NULL, ''),
(2134, '远程拉取', '2130', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:remote:operate', '#', 'admin', sysdate(), '', NULL, ''),
(2135, '模拟上报', '2130', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:remote:operate', '#', 'admin', sysdate(), '', NULL, ''),
(2300, '报修服务', '0', '6', 'repair', NULL, '', '', 1, 0, 'M', '0', '0', '', 'tool', 'admin', sysdate(), '', NULL, '报修服务目录'),
(2301, '我的报修', '2300', '1', 'repairApply', 'repair/apply/index', '', 'RepairApply', 1, 0, 'C', '0', '0', 'repair:apply:list', 'form', 'admin', sysdate(), '', NULL, '用户提交报修'),
(2302, '报修管理', '2300', '2', 'repairAdmin', 'repair/admin/index', '', 'RepairAdmin', 1, 0, 'C', '0', '0', 'repair:admin:list', 'list', 'admin', sysdate(), '', NULL, '管理员处理报修'),
(2310, '报修查询', '2301', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:apply:query', '#', 'admin', sysdate(), '', NULL, ''),
(2311, '提交报修', '2301', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:apply:add', '#', 'admin', sysdate(), '', NULL, ''),
(2312, '取消报修', '2301', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:apply:cancel', '#', 'admin', sysdate(), '', NULL, ''),
(2320, '管理查询', '2302', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:admin:query', '#', 'admin', sysdate(), '', NULL, ''),
(2321, '处理报修', '2302', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:admin:edit', '#', 'admin', sysdate(), '', NULL, ''),
(2400, '农业快讯', '0', '7', 'agriFlash', NULL, '', '', 1, 0, 'M', '0', '0', '', 'message', 'admin', sysdate(), '', NULL, '农业快讯目录'),
(2401, '农业新闻', '2400', '1', 'newsFeed', 'agri/news/index', '', 'AgriNewsFeed', 1, 0, 'C', '0', '0', 'agri:news:list', 'documentation', 'admin', sysdate(), '', NULL, 'RSS抓取农业新闻'),
(2410, '新闻查询', '2401', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:news:list', '#', 'admin', sysdate(), '', NULL, ''),
(2411, '重新抓取', '2401', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:news:refresh', '#', 'admin', sysdate(), '', NULL, '');

INSERT IGNORE INTO sys_role_menu (role_id, menu_id) VALUES
('2','2100'),
('2','2130'),('2','2131'),('2','2132'),('2','2133'),('2','2134'),('2','2135'),
('2','2300'),('2','2301'),('2','2310'),('2','2311'),('2','2312'),
('2','2400'),('2','2401'),('2','2410'),('2','2411');
