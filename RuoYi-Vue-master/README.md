# 智慧农业监测系统

基于 **Spring Boot** 与 **Vue 2** 的农田物联网监测后台：采集土壤湿度、空气温湿度、光照等数据，提供趋势分析与作物生长建议；含传感节点档案与模拟上报便于演示。

## 环境与启动

1. **MySQL**：按序号执行 `sql/` 下脚本。必做：`01_create_database.sql`（建库）→ `02_init_schema.sql`（全量初始化，已含智慧农业表与菜单、用户管理；**末尾会删掉「系统管理 / 系统监控 / 系统工具」等框架菜单**，侧边栏只保留「智慧农业」与「用户管理」）。**可选扩展**（按需）：`03_install_agri.sql`（告警中心等农业模块）、`04_install_agri_remote.sql`（远程互联）、`05_install_repair.sql`（报修服务）、`07_install_repair_worker.sql`（维修员角色与维修任务菜单）、`06_install_agri_news.sql`（农业快讯 / 农业新闻 RSS）、`90_seed_multi_users.sql`（批量测试账号，见下方说明）、`91_reset_password_ry.sql`（重置 `ry` 密码为 `123456`）。若远程互联菜单中文乱码，执行 `04_fix_agri_remote_utf8.sql`；**若维修员角色/菜单乱码，执行 `08_fix_repair_worker_utf8.sql`**。**若侧边栏消失或菜单乱码，执行 `93_restore_sidebar_menus.sql` 一键恢复全部菜单**（执行后重新登录）。**导入菜单后请重新登录**（或清空 Redis 中该用户缓存）以便侧边栏刷新。

> **SQL 中文编码约定**：新增或修改 `sql/` 脚本时，**不要在 `.sql` 文件中直接写中文**（Windows 下易乱码），应使用 `UNHEX('...')` 写入 UTF-8 十六进制，注释用英文；可参考 `08_fix_repair_worker_utf8.sql`、`93_restore_sidebar_menus.sql`。
2. **Redis**：默认 `localhost:6379`。
3. **后端**：配置 `ruoyi-admin/src/main/resources/application-druid.yml`（可用环境变量 `MYSQL_USER`、`MYSQL_PASSWORD` 等覆盖），运行 `RuoYiApplication`。
4. **前端**：在 `ruoyi-ui` 执行 `npm install`、`npm run dev`；页面标题见 `.env.development` 中 `VUE_APP_TITLE`。

## 局域网多终端访问（场景 A）

同一 WiFi / 同一网段内，**手机、平板或其他电脑**可通过浏览器访问本机上的系统（无需在每台设备安装 MySQL/Redis）。

1. **启动服务**：本机运行后端 `RuoYiApplication` 与前端 `npm run dev`（前端已绑定 `0.0.0.0:80`，后端 `0.0.0.0:8080`）。
2. **查看地址**：启动后控制台会打印「局域网访问」URL，例如 `http://192.168.1.100`。
3. **其他设备**：浏览器打开上述地址，使用同一套账号登录即可。
4. **Windows 防火墙**：若其他设备无法打开页面，以**管理员**运行：
   ```powershell
   Set-ExecutionPolicy -Scope Process Bypass -Force
   & ".\tools\open-lan-firewall.ps1"
   ```
5. **可选**：若从局域网访问时热更新异常，在 `ruoyi-ui/.env.development` 中设置 `VUE_APP_PUBLIC_HOST = 你的本机IP` 后重启 `npm run dev`。

> API 请求经本机前端 dev 代理转发到后端，局域网设备只需能访问前端端口（默认 80），不必直连 8080。

## 主要功能模块

| 模块 | 说明 |
|------|------|
| 智慧农业 → 环境监测 | 实时读数、趋势图、生长建议、模拟上报 |
| 智慧农业 → 传感节点 | 设备编码、地块、作物类型等维护 |
| 报修服务 → 我的报修 | 普通用户提交报修、查看进度 |
| 报修服务 → 维修任务 | 维修员查看待受理报修并处理 |
| 报修服务 → 报修管理 | 管理员查看全部报修并处理 |
| 农业快讯 → 农业新闻 | 从公开 RSS 源抓取农业资讯，支持搜索与刷新 |

## 多用户账号（可选）

执行 `sql/90_seed_multi_users.sql` 后可用以下账号登录（**初始密码均为 `123456`**，登录后可在个人中心修改）：

| 账号 | 昵称 | 角色 | 权限说明 |
|------|------|------|----------|
| `admin` | 管理员 | 超级管理员 | 系统内置，`user_id=1`，拥有全部菜单 |
| `agri_admin` | 农业管理员 | 农业管理员 | 智慧农业全部功能（含节点增删改、模拟上报） |
| `manager01` | 农技主管 | 农业管理员 | 同上 |
| `monitor01` / `monitor02` / `monitor03` | 监测员 | 普通角色 | 仅查看环境监测与传感节点列表 |
| `repair01` / `repair02` | 维修员 | 维修员 | 查看待受理报修、受理并处理（需先执行 `07_install_repair_worker.sql`） |
| `ry` | 监测员 | 普通角色 | 同上（若库中已有该账号会同步为普通角色） |

> 管理员登录后可在侧边栏 **用户管理** 中增删改用户、分配角色与重置密码（导入 `02_init_schema.sql` 即已包含）。普通监测员账号无此菜单。

## 用户管理（管理员）

| 能力 | 说明 |
|------|------|
| 可见账号 | `admin`（超级管理员）、`agri_admin` / `manager01`（农业管理员角色） |
| 功能 | 用户列表、新增/修改/删除、重置密码、分配角色、导入导出 |
| 不可见 | `monitor01` 等普通角色账号 |

## 说明

系统在开源管理框架基础上扩展了农业业务表与菜单。当前仓库前端已裁剪为**登录、首页、个人中心、智慧农业页面**所需资源，并移除了监控、代码生成、系统管理界面与顶栏搜索/全屏/布局抽屉等；与 `02_init_schema.sql` 及已删减的后端 Controller 配套使用。
