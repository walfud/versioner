SET NAMES utf8mb4;

# database
DROP DATABASE IF EXISTS versioner;
CREATE DATABASE versioner;
USE versioner;

# privilege
GRANT ALL PRIVILEGES ON `versioner`.* to 'versioner'@'%';

# version
DROP TABLE IF EXISTS `version`;
CREATE TABLE `version`
(
    id          CHAR(36) PRIMARY KEY,

    value       INT,

    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
