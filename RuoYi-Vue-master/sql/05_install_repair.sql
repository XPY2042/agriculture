-- ============================================================
-- 报修服务：数据表 + 菜单(menu_id 2300-2322) + 角色授权
-- 默认库名取 application-druid.yml 中的 ry-vue
-- 如果你本地改了库名，请把下面的 USE 改成你的实际库名
-- ============================================================

SET NAMES utf8mb4;
USE `ry-vue`;

DROP TABLE IF EXISTS repair_request;

CREATE TABLE repair_request (
  request_id       bigint(20)      NOT NULL AUTO_INCREMENT    COMMENT '报修ID',
  user_id          bigint(20)      NOT NULL                   COMMENT '报修用户ID',
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
  KEY idx_repair_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修申请';

DELETE FROM sys_role_menu WHERE menu_id IN (2300,2301,2302,2310,2311,2312,2320,2321,2322);
DELETE FROM sys_menu WHERE menu_id IN (2300,2301,2302,2310,2311,2312,2320,2321,2322);

INSERT INTO sys_menu VALUES(2300, '报修服务', '0', '6', 'repair', NULL, '', '', 1, 0, 'M', '0', '0', '', 'tool', 'admin', sysdate(), '', NULL, '报修服务目录');
INSERT INTO sys_menu VALUES(2301, '我的报修', '2300', '1', 'repairApply', 'repair/apply/index', '', 'RepairApply', 1, 0, 'C', '0', '0', 'repair:apply:list', 'form', 'admin', sysdate(), '', NULL, '用户提交报修');
INSERT INTO sys_menu VALUES(2302, '报修管理', '2300', '2', 'repairAdmin', 'repair/admin/index', '', 'RepairAdmin', 1, 0, 'C', '0', '0', 'repair:admin:list', 'list', 'admin', sysdate(), '', NULL, '管理员处理报修');

INSERT INTO sys_menu VALUES(2310, '报修查询', '2301', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:apply:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2311, '提交报修', '2301', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:apply:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2312, '取消报修', '2301', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:apply:cancel', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu VALUES(2320, '管理查询', '2302', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:admin:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2321, '处理报修', '2302', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:admin:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2322, '删除报修', '2302', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:admin:remove', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_role_menu VALUES ('2', '2300');
INSERT INTO sys_role_menu VALUES ('2', '2301');
INSERT INTO sys_role_menu VALUES ('2', '2302');
INSERT INTO sys_role_menu VALUES ('2', '2310');
INSERT INTO sys_role_menu VALUES ('2', '2311');
INSERT INTO sys_role_menu VALUES ('2', '2312');
INSERT INTO sys_role_menu VALUES ('2', '2320');
INSERT INTO sys_role_menu VALUES ('2', '2321');

INSERT INTO sys_role_menu VALUES ('1', '2300');
INSERT INTO sys_role_menu VALUES ('1', '2302');
INSERT INTO sys_role_menu VALUES ('1', '2320');
INSERT INTO sys_role_menu VALUES ('1', '2321');
INSERT INTO sys_role_menu VALUES ('1', '2322');
