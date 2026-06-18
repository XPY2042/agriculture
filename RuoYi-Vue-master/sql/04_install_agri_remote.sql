SET NAMES utf8mb4;
USE `ry-vue`;

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

DELETE FROM sys_role_menu WHERE menu_id IN (2130,2131,2132,2133,2134,2135);
DELETE FROM sys_menu WHERE menu_id IN (2130,2131,2132,2133,2134,2135);

INSERT INTO sys_menu VALUES(2130, '远程互联', '2100', '4', 'agriRemote', 'agri/remote/index', '', 'AgriRemoteLink', 1, 0, 'C', '0', '0', 'agri:remote:view', 'link', 'admin', sysdate(), '', NULL, '远程网络检测与设备操作');
INSERT INTO sys_menu VALUES(2131, '互联查询', '2130', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:remote:view', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2132, '远程操作', '2130', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:remote:operate', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2133, '出网检测', '2130', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:remote:view', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2134, '远程拉取', '2130', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:remote:operate', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2135, '模拟上报', '2130', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'agri:remote:operate', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_role_menu VALUES ('2', '2130');
INSERT INTO sys_role_menu VALUES ('2', '2131');
INSERT INTO sys_role_menu VALUES ('2', '2132');
INSERT INTO sys_role_menu VALUES ('2', '2133');
INSERT INTO sys_role_menu VALUES ('2', '2134');
INSERT INTO sys_role_menu VALUES ('2', '2135');

INSERT IGNORE INTO sys_role_menu VALUES ('3', '2130');
INSERT IGNORE INTO sys_role_menu VALUES ('3', '2131');
INSERT IGNORE INTO sys_role_menu VALUES ('3', '2132');
INSERT IGNORE INTO sys_role_menu VALUES ('3', '2133');
INSERT IGNORE INTO sys_role_menu VALUES ('3', '2134');
INSERT IGNORE INTO sys_role_menu VALUES ('3', '2135');
