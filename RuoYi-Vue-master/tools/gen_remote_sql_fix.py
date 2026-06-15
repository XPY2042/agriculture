# Regenerate 04_fix_agri_remote_utf8.sql with correct irrigation chars (ASCII-safe generator).
from pathlib import Path

GAI = "\u704c\u6e89"
JIA = "\u704c\u6d43"
JIA2 = "\u704c\u6d4d"

sql = f"""SET NAMES utf8mb4;

UPDATE sys_menu SET menu_name = '\u8fdc\u7a0b\u4e92\u8054', remark = '\u8fdc\u7a0b\u7f51\u7edc\u68c0\u6d4b\u4e0e\u8bbe\u5907\u64cd\u4f5c' WHERE menu_id = 2130;
UPDATE sys_menu SET menu_name = '\u4e92\u8054\u67e5\u8be2' WHERE menu_id = 2131;
UPDATE sys_menu SET menu_name = '\u8fdc\u7a0b\u64cd\u4f5c' WHERE menu_id = 2132;
UPDATE sys_menu SET menu_name = '\u51fa\u7f51\u68c0\u6d4b' WHERE menu_id = 2133;
UPDATE sys_menu SET menu_name = '\u8fdc\u7a0b\u62c9\u53d6' WHERE menu_id = 2134;
UPDATE sys_menu SET menu_name = '\u6a21\u62df\u4e0a\u62a5' WHERE menu_id = 2135;

UPDATE agri_remote_command SET command_label = '\u5f00\u542f{GAI}' WHERE command_type = 'IRRIGATE_ON';
UPDATE agri_remote_command SET command_label = '\u5173\u95ed{GAI}' WHERE command_type = 'IRRIGATE_OFF';
UPDATE agri_remote_command SET command_label = '\u5f00\u542f\u901a\u98ce' WHERE command_type = 'FAN_ON';
UPDATE agri_remote_command SET command_label = '\u5173\u95ed\u901a\u98ce' WHERE command_type = 'FAN_OFF';
UPDATE agri_remote_command SET command_label = '\u89e6\u53d1\u4f20\u611f\u8bfb\u6570' WHERE command_type = 'READ_SENSOR';
UPDATE agri_remote_command SET command_label = '\u91cd\u542f\u8282\u70b9' WHERE command_type = 'REBOOT';

UPDATE agri_remote_command SET command_label = REPLACE(command_label, '{JIA}', '{GAI}') WHERE command_label LIKE '%{JIA}%';
UPDATE agri_remote_command SET command_label = REPLACE(command_label, '{JIA2}', '{GAI}') WHERE command_label LIKE '%{JIA2}%';
UPDATE agri_remote_command SET result_message = REPLACE(result_message, '{JIA}', '{GAI}') WHERE result_message LIKE '%{JIA}%';
UPDATE agri_remote_command SET result_message = REPLACE(result_message, '{JIA2}', '{GAI}') WHERE result_message LIKE '%{JIA2}%';
"""

if __name__ == "__main__":
    out = Path(__file__).resolve().parents[1] / "sql/04_fix_agri_remote_utf8.sql"
    out.write_text(sql, encoding="utf-8")
    assert GAI in out.read_text(encoding="utf-8")
    print("wrote", out)
