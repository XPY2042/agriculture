SET NAMES utf8mb4;

UPDATE sys_menu SET menu_name = '远程互联', remark = '远程网络检测与设备操作' WHERE menu_id = 2130;
UPDATE sys_menu SET menu_name = '互联查询' WHERE menu_id = 2131;
UPDATE sys_menu SET menu_name = '远程操作' WHERE menu_id = 2132;
UPDATE sys_menu SET menu_name = '出网检测' WHERE menu_id = 2133;
UPDATE sys_menu SET menu_name = '远程拉取' WHERE menu_id = 2134;
UPDATE sys_menu SET menu_name = '模拟上报' WHERE menu_id = 2135;

UPDATE agri_remote_command SET command_label = '开启灌浃' WHERE command_type = 'IRRIGATE_ON';
UPDATE agri_remote_command SET command_label = '关闭灌浃' WHERE command_type = 'IRRIGATE_OFF';
UPDATE agri_remote_command SET command_label = '开启通风' WHERE command_type = 'FAN_ON';
UPDATE agri_remote_command SET command_label = '关闭通风' WHERE command_type = 'FAN_OFF';
UPDATE agri_remote_command SET command_label = '触发传感读数' WHERE command_type = 'READ_SENSOR';
UPDATE agri_remote_command SET command_label = '重启节点' WHERE command_type = 'REBOOT';

UPDATE agri_remote_command SET command_label = REPLACE(command_label, '灌浍', '灌浃') WHERE command_label LIKE '%灌浍%';
UPDATE agri_remote_command SET command_label = REPLACE(command_label, '灌溉', '灌浃') WHERE command_label LIKE '%灌溉%';
UPDATE agri_remote_command SET result_message = REPLACE(result_message, '灌浍', '灌浃') WHERE result_message LIKE '%灌浍%';
UPDATE agri_remote_command SET result_message = REPLACE(result_message, '灌溉', '灌浃') WHERE result_message LIKE '%灌溉%';
