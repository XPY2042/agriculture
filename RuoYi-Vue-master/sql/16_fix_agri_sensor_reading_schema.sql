-- ============================================================
-- Fix agri_sensor_reading schema for existing databases.
-- Adds fields used by current mapper/domain without rebuilding data.
-- MySQL 8.0 does not support ADD COLUMN IF NOT EXISTS on all versions,
-- so use information_schema checks.
-- ============================================================

SET NAMES utf8mb4;

DROP PROCEDURE IF EXISTS add_agri_reading_column_if_missing;
DELIMITER $$
CREATE PROCEDURE add_agri_reading_column_if_missing(
  IN col_name varchar(64),
  IN col_def varchar(255),
  IN after_col varchar(64)
)
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'agri_sensor_reading'
      AND column_name = col_name
  ) THEN
    SET @sql = CONCAT('ALTER TABLE agri_sensor_reading ADD COLUMN ', col_name, ' ', col_def, ' AFTER ', after_col);
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;
END$$
DELIMITER ;

CALL add_agri_reading_column_if_missing('soil_ph', 'decimal(4,2) DEFAULT NULL COMMENT ''土壤pH''', 'soil_temp_c');
CALL add_agri_reading_column_if_missing('co2_ppm', 'int(11) DEFAULT NULL COMMENT ''CO2浓度ppm''', 'soil_ph');
CALL add_agri_reading_column_if_missing('water_ph', 'decimal(4,2) DEFAULT NULL COMMENT ''水体pH''', 'co2_ppm');

DROP PROCEDURE IF EXISTS add_agri_reading_column_if_missing;
