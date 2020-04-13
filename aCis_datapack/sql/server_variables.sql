CREATE TABLE IF NOT EXISTS `server_variables` (
  `name` VARCHAR(86) NOT NULL DEFAULT '',
  `value` VARCHAR(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`name`)
);