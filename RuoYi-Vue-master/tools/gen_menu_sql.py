# -*- coding: utf-8 -*-
menus = [
(2100,'\u667a\u6167\u519c\u4e1a','0','5','agri',None,'','',1,0,'M','0','0','','international','\u667a\u6167\u519c\u4e1a\u76ee\u5f55'),
(2101,'\u73af\u5883\u76d1\u6d4b','2100','1','agriEnv','agri/monitor/index','','AgriEnvMonitor',1,0,'C','0','0','agri:monitor:view','chart','\u5b9e\u65f6\u8bfb\u6570\u4e0e\u8d8b\u52bf'),
(2102,'\u4f20\u611f\u8282\u70b9','2100','2','agriSensor','agri/node/index','','AgriSensorNode',1,0,'C','0','0','agri:node:list','server','\u8bbe\u5907\u6863\u6848\u7ef4\u62a4'),
(2103,'\u544a\u8b66\u4e2d\u5fc3','2100','3','agriAlarm','agri/alarm/index','','AgriAlarmCenter',1,0,'C','0','0','agri:monitor:view','message','\u4f20\u611f\u9608\u503c\u8d85\u9650\u544a\u8b66'),
(2110,'\u8282\u70b9\u67e5\u8be2','2102','1','','','','',1,0,'F','0','0','agri:node:query','#',''),
(2111,'\u8282\u70b9\u65b0\u589e','2102','2','','','','',1,0,'F','0','0','agri:node:add','#',''),
(2112,'\u8282\u70b9\u4fee\u6539','2102','3','','','','',1,0,'F','0','0','agri:node:edit','#',''),
(2113,'\u8282\u70b9\u5220\u9664','2102','4','','','','',1,0,'F','0','0','agri:node:remove','#',''),
(2114,'\u6570\u636e\u4e0a\u62a5','2101','1','','','','',1,0,'F','0','0','agri:monitor:ingest','#',''),
(2120,'\u544a\u8b66\u67e5\u8be2','2103','1','','','','',1,0,'F','0','0','agri:monitor:view','#',''),
(2121,'\u544a\u8b66\u786e\u8ba4','2103','2','','','','',1,0,'F','0','0','agri:monitor:view','#',''),
(2122,'\u544a\u8b66\u626b\u63cf','2103','3','','','','',1,0,'F','0','0','agri:monitor:view','#',''),
(2130,'\u8fdc\u7a0b\u4e92\u8054','2100','4','agriRemote','agri/remote/index','','AgriRemoteLink',1,0,'C','0','0','agri:remote:view','link','\u8fdc\u7a0b\u7f51\u7edc\u68c0\u6d4b\u4e0e\u8bbe\u5907\u64cd\u4f5c'),
(2131,'\u4e92\u8054\u67e5\u8be2','2130','1','','','','',1,0,'F','0','0','agri:remote:view','#',''),
(2132,'\u8fdc\u7a0b\u64cd\u4f5c','2130','2','','','','',1,0,'F','0','0','agri:remote:operate','#',''),
(2133,'\u51fa\u7f51\u68c0\u6d4b','2130','3','','','','',1,0,'F','0','0','agri:remote:view','#',''),
(2134,'\u8fdc\u7a0b\u62c9\u53d6','2130','4','','','','',1,0,'F','0','0','agri:remote:operate','#',''),
(2135,'\u6a21\u62df\u4e0a\u62a5','2130','5','','','','',1,0,'F','0','0','agri:remote:operate','#',''),
(2200,'\u7cfb\u7edf\u7ba1\u7406','0','5','system',None,'','',1,0,'M','0','0','','system',''),
(100,'\u7528\u6237\u7ba1\u7406','2200','1','user','system/user/index','','',1,0,'C','0','0','system:user:list','user','\u7cfb\u7edf\u7528\u6237\u7ef4\u62a4'),
(1000,'\u7528\u6237\u67e5\u8be2','100','1','','','','',1,0,'F','0','0','system:user:query','#',''),
(1001,'\u7528\u6237\u65b0\u589e','100','2','','','','',1,0,'F','0','0','system:user:add','#',''),
(1002,'\u7528\u6237\u4fee\u6539','100','3','','','','',1,0,'F','0','0','system:user:edit','#',''),
(1003,'\u7528\u6237\u5220\u9664','100','4','','','','',1,0,'F','0','0','system:user:remove','#',''),
(1004,'\u7528\u6237\u5bfc\u51fa','100','5','','','','',1,0,'F','0','0','system:user:export','#',''),
(1005,'\u7528\u6237\u5bfc\u5165','100','6','','','','',1,0,'F','0','0','system:user:import','#',''),
(1006,'\u91cd\u7f6e\u5bc6\u7801','100','7','','','','',1,0,'F','0','0','system:user:resetPwd','#',''),
(2300,'\u62a5\u4fee\u670d\u52a1','0','6','repair',None,'','',1,0,'M','0','0','','tool','\u62a5\u4fee\u670d\u52a1\u76ee\u5f55'),
(2301,'\u6211\u7684\u62a5\u4fee','2300','1','repairApply','repair/apply/index','','RepairApply',1,0,'C','0','0','repair:apply:list','form','\u7528\u6237\u63d0\u4ea4\u62a5\u4fee'),
(2302,'\u62a5\u4fee\u7ba1\u7406','2300','2','repairAdmin','repair/admin/index','','RepairAdmin',1,0,'C','0','0','repair:admin:list','list','\u7ba1\u7406\u5458\u5904\u7406\u62a5\u4fee'),
(2310,'\u62a5\u4fee\u67e5\u8be2','2301','1','','','','',1,0,'F','0','0','repair:apply:query','#',''),
(2311,'\u63d0\u4ea4\u62a5\u4fee','2301','2','','','','',1,0,'F','0','0','repair:apply:add','#',''),
(2312,'\u53d6\u6d88\u62a5\u4fee','2301','3','','','','',1,0,'F','0','0','repair:apply:cancel','#',''),
(2320,'\u7ba1\u7406\u67e5\u8be2','2302','1','','','','',1,0,'F','0','0','repair:admin:query','#',''),
(2321,'\u5904\u7406\u62a5\u4fee','2302','2','','','','',1,0,'F','0','0','repair:admin:edit','#',''),
(2322,'\u5220\u9664\u62a5\u4fee','2302','3','','','','',1,0,'F','0','0','repair:admin:remove','#',''),
(2400,'\u519c\u4e1a\u5feb\u8baf','0','7','agriFlash',None,'','',1,0,'M','0','0','','message','\u519c\u4e1a\u8d44\u8baf\u5feb\u8baf\u76ee\u5f55'),
(2401,'\u519c\u4e1a\u65b0\u95fb','2400','1','newsFeed','agri/news/index','','AgriNewsFeed',1,0,'C','0','0','agri:news:list','documentation','RSS\u6293\u53d6\u519c\u4e1a\u65b0\u95fb'),
(2410,'\u65b0\u95fb\u67e5\u8be2','2401','1','','','','',1,0,'F','0','0','agri:news:list','#',''),
(2411,'\u91cd\u65b0\u6293\u53d6','2401','2','','','','',1,0,'F','0','0','agri:news:refresh','#',''),
]

def hx(s):
    if not s:
        return "''"
    return "UNHEX('" + s.encode('utf-8').hex().upper() + "')"

lines = []
ids = [str(m[0]) for m in menus]
lines.append("DELETE FROM sys_role_menu WHERE menu_id IN (" + ",".join(ids) + ");")
lines.append("DELETE FROM sys_menu WHERE menu_id IN (" + ",".join(ids) + ");")
lines.append("")

for m in menus:
    mid, name, pid, order, path, comp, query, rname, iframe, cache, mtype, vis, stat, perms, icon, remark = m
    comp_sql = 'NULL' if comp is None else "'" + comp + "'"
    path_sql = 'NULL' if path is None else "'" + path + "'"
    remark_sql = "''" if not remark else hx(remark)
    lines.append(
        "INSERT INTO sys_menu VALUES({mid}, {name}, '{pid}', '{order}', {path}, {comp}, '{query}', '{rname}', {iframe}, {cache}, '{mtype}', '{vis}', '{stat}', '{perms}', '{icon}', 'admin', sysdate(), '', NULL, {remark});".format(
            mid=mid, name=hx(name), pid=pid, order=order, path=path_sql, comp=comp_sql,
            query=query, rname=rname, iframe=iframe, cache=cache, mtype=mtype, vis=vis, stat=stat,
            perms=perms, icon=icon, remark=remark_sql))

print("\n".join(lines))
