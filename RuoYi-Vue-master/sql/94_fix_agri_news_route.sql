-- Fix agri news menu route path (agriNews -> newsFeed) for reliable Vue Router match
-- Run after 93_restore_sidebar_menus.sql if page still blank on click

SET NAMES utf8mb4;

UPDATE sys_menu SET path = 'newsFeed' WHERE menu_id = 2401;
