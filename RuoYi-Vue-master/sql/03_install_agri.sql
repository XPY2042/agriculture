-- ============================================================
-- 智慧农业：数据表 + 演示数据 + 菜单(menu_id 2100-2114) + 普通角色授权
-- 执行前请先：USE 你的数据库名;  （与 application-druid 中库名一致）
-- 可重复执行：会先删本模块菜单再插入，并重建业务表（演示数据会重置）
-- ============================================================

SET NAMES utf8mb4;

-- ---------- 表结构 ----------
DROP TABLE IF EXISTS agri_sensor_reading;
DROP TABLE IF EXISTS agri_sensor_node;

CREATE TABLE agri_sensor_node (
  node_id          bigint(20)      NOT NULL AUTO_INCREMENT    COMMENT '节点ID',
  node_code        varchar(64)     NOT NULL                   COMMENT '设备编码',
  node_name        varchar(100)    NOT NULL DEFAULT ''        COMMENT '节点名称',
  plot_location    varchar(200)    DEFAULT ''                 COMMENT '地块位置',
  crop_type        varchar(64)     DEFAULT ''                 COMMENT '作物类型',
  status           char(1)         DEFAULT '0'                COMMENT '0正常 1停用',
  del_flag         char(1)         DEFAULT '0'                COMMENT '0存在 2删除',
  create_by        varchar(64)     DEFAULT ''                 COMMENT '创建者',
  create_time      datetime                                   COMMENT '创建时间',
  update_by        varchar(64)     DEFAULT ''                 COMMENT '更新者',
  update_time      datetime                                   COMMENT '更新时间',
  remark           varchar(500)    DEFAULT NULL               COMMENT '备注',
  PRIMARY KEY (node_id),
  UNIQUE KEY uk_agri_node_code (node_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农业传感节点';

CREATE TABLE agri_sensor_reading (
  reading_id           bigint(20)      NOT NULL AUTO_INCREMENT    COMMENT '读数ID',
  node_id              bigint(20)      NOT NULL                   COMMENT '节点ID',
  soil_moisture_pct    decimal(5,2)    DEFAULT NULL               COMMENT '土壤湿度%',
  air_temp_c           decimal(5,2)    DEFAULT NULL               COMMENT '气温℃',
  air_humidity_pct     decimal(5,2)    DEFAULT NULL               COMMENT '空气湿度%',
  light_lux            int(11)         DEFAULT NULL               COMMENT '光照lux',
  soil_temp_c          decimal(5,2)    DEFAULT NULL               COMMENT '土壤温度℃',
  soil_ph              decimal(4,2)    DEFAULT NULL               COMMENT '土壤pH',
  co2_ppm              int(11)         DEFAULT NULL               COMMENT 'CO2浓度ppm',
  water_ph             decimal(4,2)    DEFAULT NULL               COMMENT '水体pH',
  reading_time         datetime        NOT NULL                   COMMENT '采集时间',
  create_time          datetime        DEFAULT NULL               COMMENT '入库时间',
  PRIMARY KEY (reading_id),
  KEY idx_agri_reading_node_time (node_id, reading_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='传感读数';

CREATE TABLE IF NOT EXISTS agri_alarm_record (
  alarm_id         bigint(20)      NOT NULL AUTO_INCREMENT    COMMENT '告警ID',
  node_id          bigint(20)      NOT NULL                   COMMENT '节点ID',
  reading_id       bigint(20)      DEFAULT NULL               COMMENT '触发读数ID',
  metric_code      varchar(32)     NOT NULL                   COMMENT '指标编码',
  metric_name      varchar(64)     NOT NULL DEFAULT ''        COMMENT '指标名称',
  alarm_level      char(1)         NOT NULL DEFAULT '2'       COMMENT '1严重 2警告',
  direction        varchar(8)      NOT NULL DEFAULT ''        COMMENT 'low/high',
  actual_value     varchar(32)     DEFAULT ''                 COMMENT '实测值',
  threshold_value  varchar(32)     DEFAULT ''                 COMMENT '阈值',
  alarm_message    varchar(500)    NOT NULL                   COMMENT '告警说明',
  status           char(1)         NOT NULL DEFAULT '0'       COMMENT '0未处理 1已确认',
  alarm_time       datetime        NOT NULL                   COMMENT '告警时间',
  handle_by        varchar(64)     DEFAULT ''                 COMMENT '处理人',
  handle_time      datetime        DEFAULT NULL               COMMENT '处理时间',
  create_time      datetime        DEFAULT NULL               COMMENT '创建时间',
  PRIMARY KEY (alarm_id),
  KEY idx_agri_alarm_node_time (node_id, alarm_time),
  KEY idx_agri_alarm_status (status, alarm_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='传感阈值告警';

INSERT INTO agri_sensor_node VALUES
(1, 'DEV-FARM-001', '大田1号土壤传感器', '试验田东区-A块', '小麦', '0', '0', 'admin', sysdate(), '', NULL, '演示'),
(2, 'DEV-GH-002', '温室3号环境监测', '日光温室3号', '番茄', '0', '0', 'admin', sysdate(), '', NULL, '演示');

INSERT INTO agri_sensor_reading (node_id, soil_moisture_pct, air_temp_c, air_humidity_pct, light_lux, soil_temp_c, reading_time, create_time)
SELECT 1, 42.5, 22.3, 58, 18500, 19.1, DATE_SUB(NOW(), INTERVAL 6 HOUR), sysdate() UNION ALL
SELECT 1, 41.0, 23.1, 57, 22000, 19.4, DATE_SUB(NOW(), INTERVAL 5 HOUR), sysdate() UNION ALL
SELECT 1, 38.2, 24.5, 55, 28000, 20.0, DATE_SUB(NOW(), INTERVAL 4 HOUR), sysdate() UNION ALL
SELECT 1, 35.0, 25.8, 52, 32000, 20.6, DATE_SUB(NOW(), INTERVAL 3 HOUR), sysdate() UNION ALL
SELECT 1, 32.1, 26.2, 50, 35000, 21.0, DATE_SUB(NOW(), INTERVAL 2 HOUR), sysdate() UNION ALL
SELECT 1, 28.5, 27.0, 48, 30000, 21.5, DATE_SUB(NOW(), INTERVAL 1 HOUR), sysdate() UNION ALL
SELECT 1, 26.0, 28.1, 46, 25000, 22.0, NOW(), sysdate();

INSERT INTO agri_sensor_reading (node_id, soil_moisture_pct, air_temp_c, air_humidity_pct, light_lux, soil_temp_c, reading_time, create_time)
SELECT 2, 55.0, 24.0, 72, 12000, 22.5, DATE_SUB(NOW(), INTERVAL 2 HOUR), sysdate() UNION ALL
SELECT 2, 54.2, 24.5, 71, 15000, 22.8, DATE_SUB(NOW(), INTERVAL 1 HOUR), sysdate() UNION ALL
SELECT 2, 53.5, 25.0, 70, 18000, 23.0, NOW(), sysdate();

-- ---------- 菜单（可重复执行）----------
DELETE FROM sys_role_menu WHERE menu_id IN (2100,2101,2102,2103,2110,2111,2112,2113,2114,2120,2121,2122);
DELETE FROM sys_menu WHERE menu_id IN (2100,2101,2102,2103,2110,2111,2112,2113,2114,2120,2121,2122);

INSERT INTO sys_menu VALUES(2100, '智慧农业', '0', '5', 'agri', NULL, '', '', 1, 0, 'M', '0', '0', '', 'international', 'admin', sysdate(), '', NULL, '智慧农业目录');
-- 子路由 path 勿用 monitor：会与顶级「系统监控」(/monitor) 撞名导致 Vue Router name 重复、侧边栏异常与 404
INSERT INTO sys_menu VALUES(2101, '环境监测', '2100', '1', 'agriEnv', 'agri/monitor/index', '', 'AgriEnvMonitor', 1, 0, 'C', '0', '0', 'agri:monitor:view', 'chart', 'admin', sysdate(), '', NULL, '实时读数与趋势');
INSERT INTO sys_menu VALUES(2102, '传感节点', '2100', '2', 'agriSensor', 'agri/node/index', '', 'AgriSensorNode', 1, 0, 'C', '0', '0', 'agri:node:list', 'server', 'admin', sysdate(), '', NULL, '设备档案维护');

INSERT INTO sys_menu VALUES(2110, '节点查询', '2102', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:node:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2111, '节点新增', '2102', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:node:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2112, '节点修改', '2102', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:node:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2113, '节点删除', '2102', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:node:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2114, '数据上报', '2101', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:monitor:ingest', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2103, '告警中心', '2100', '3', 'agriAlarm', 'agri/alarm/index', '', 'AgriAlarmCenter', 1, 0, 'C', '0', '0', 'agri:monitor:view', 'message', 'admin', sysdate(), '', NULL, '传感阈值超限告警（需环境监测权限）');
INSERT INTO sys_menu VALUES(2120, '告警查询', '2103', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:monitor:view', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2121, '告警确认', '2103', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:monitor:view', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2122, '告警扫描', '2103', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:monitor:view', '#', 'admin', sysdate(), '', NULL, '');

-- 普通角色 role_id=2（如用户 ry）
INSERT INTO sys_role_menu VALUES ('2', '2100');
INSERT INTO sys_role_menu VALUES ('2', '2101');
INSERT INTO sys_role_menu VALUES ('2', '2102');
INSERT INTO sys_role_menu VALUES ('2', '2103');
INSERT INTO sys_role_menu VALUES ('2', '2110');
INSERT INTO sys_role_menu VALUES ('2', '2111');
INSERT INTO sys_role_menu VALUES ('2', '2112');
INSERT INTO sys_role_menu VALUES ('2', '2113');
INSERT INTO sys_role_menu VALUES ('2', '2114');
INSERT INTO sys_role_menu VALUES ('2', '2120');
INSERT INTO sys_role_menu VALUES ('2', '2121');
INSERT INTO sys_role_menu VALUES ('2', '2122');
