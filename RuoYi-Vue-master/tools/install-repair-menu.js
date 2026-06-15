const fs = require('fs');
const { execSync } = require('child_process');
const path = require('path');

const sql = `SET NAMES utf8mb4;
USE \`ry-vue\`;

CREATE TABLE IF NOT EXISTS repair_request (
  request_id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '\u62a5\u4feeID',
  user_id bigint(20) NOT NULL COMMENT '\u62a5\u4fee\u7528\u6237ID',
  title varchar(100) NOT NULL DEFAULT '' COMMENT '\u62a5\u4fee\u539f\u56e0',
  description varchar(1000) DEFAULT '' COMMENT '\u95ee\u9898\u63cf\u8ff0',
  location varchar(200) DEFAULT '' COMMENT '\u62a5\u4fee\u5730\u70b9',
  contact_phone varchar(20) DEFAULT '' COMMENT '\u8054\u7cfb\u7535\u8bdd',
  status char(1) DEFAULT '0' COMMENT '0\u5f85\u53d7\u7406 1\u5904\u7406\u4e2d 2\u5df2\u5b8c\u6210 3\u5df2\u53d6\u6d88',
  admin_reply varchar(500) DEFAULT '' COMMENT '\u7ba1\u7406\u5458\u56de\u590d',
  del_flag char(1) DEFAULT '0' COMMENT '0\u5b58\u5728 2\u5220\u9664',
  create_by varchar(64) DEFAULT '' COMMENT '\u521b\u5efa\u8005',
  create_time datetime COMMENT '\u521b\u5efa\u65f6\u95f4',
  update_by varchar(64) DEFAULT '' COMMENT '\u66f4\u65b0\u8005',
  update_time datetime COMMENT '\u66f4\u65b0\u65f6\u95f4',
  remark varchar(500) DEFAULT NULL COMMENT '\u5907\u6ce8',
  PRIMARY KEY (request_id),
  KEY idx_repair_user_id (user_id),
  KEY idx_repair_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='\u62a5\u4fee\u7533\u8bf7';

DELETE FROM sys_role_menu WHERE menu_id IN (2300,2301,2302,2310,2311,2312,2320,2321,2322);
DELETE FROM sys_menu WHERE menu_id IN (2300,2301,2302,2310,2311,2312,2320,2321,2322);

INSERT INTO sys_menu VALUES(2300, '\u62a5\u4fee\u670d\u52a1', '0', '6', 'repair', NULL, '', '', 1, 0, 'M', '0', '0', '', 'tool', 'admin', sysdate(), '', NULL, '\u62a5\u4fee\u670d\u52a1\u76ee\u5f55');
INSERT INTO sys_menu VALUES(2301, '\u6211\u7684\u62a5\u4fee', '2300', '1', 'repairApply', 'repair/apply/index', '', 'RepairApply', 1, 0, 'C', '0', '0', 'repair:apply:list', 'form', 'admin', sysdate(), '', NULL, '\u7528\u6237\u63d0\u4ea4\u62a5\u4fee');
INSERT INTO sys_menu VALUES(2302, '\u62a5\u4fee\u7ba1\u7406', '2300', '2', 'repairAdmin', 'repair/admin/index', '', 'RepairAdmin', 1, 0, 'C', '0', '0', 'repair:admin:list', 'list', 'admin', sysdate(), '', NULL, '\u7ba1\u7406\u5458\u5904\u7406\u62a5\u4fee');

INSERT INTO sys_menu VALUES(2310, '\u62a5\u4fee\u67e5\u8be2', '2301', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:apply:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2311, '\u63d0\u4ea4\u62a5\u4fee', '2301', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:apply:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2312, '\u53d6\u6d88\u62a5\u4fee', '2301', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:apply:cancel', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu VALUES(2320, '\u7ba1\u7406\u67e5\u8be2', '2302', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:admin:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2321, '\u5904\u7406\u62a5\u4fee', '2302', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:admin:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2322, '\u5220\u9664\u62a5\u4fee', '2302', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'repair:admin:remove', '#', 'admin', sysdate(), '', NULL, '');

INSERT IGNORE INTO sys_role_menu (role_id, menu_id) VALUES
('2','2300'),('2','2301'),('2','2310'),('2','2311'),('2','2312'),
('1','2300'),('1','2302'),('1','2320'),('1','2321'),('1','2322');
`;

const tmp = path.join(__dirname, 'repair-menu-utf8.sql');
fs.writeFileSync(tmp, sql, 'utf8');

const mysql = 'E:\\MySQL\\bin\\mysql.exe';
execSync(`"${mysql}" -uroot -p123456 --default-character-set=utf8mb4 < "${tmp}"`, {
  stdio: 'inherit',
  shell: true
});

const out = execSync(
  `"${mysql}" -uroot -p123456 --default-character-set=utf8mb4 ry-vue -N -e "SELECT menu_id, menu_name FROM sys_menu WHERE menu_id BETWEEN 2300 AND 2302 ORDER BY menu_id;"`,
  { encoding: 'utf8', shell: true }
);
console.log('menus:\n' + out);
