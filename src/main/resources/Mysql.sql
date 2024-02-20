/**
  database 세팅 후 최초 설정 DDL
 */

create database cafe;
create user 'cafe' identified by 'password';
grant all privileges on cafe.* TO 'cafe';
flush privileges;


/**
  account table
 */
CREATE TABLE `accounts`
(
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `mobileNumber` VARCHAR(255)    NOT NULL,
    `password`     VARCHAR(255)    NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_mobile_number` (`mobileNumber`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

/**
  product table
*/
CREATE TABLE `products`
(
    `id`             BIGINT        NOT NULL AUTO_INCREMENT,
    `category`       VARCHAR(255)  NOT NULL,
    `price`          INT           NOT NULL CHECK (`price` >= 0),
    `cost`           INT           NOT NULL CHECK (`cost` >= 0),
    `name`           VARCHAR(255)  NOT NULL,
    `description`    VARCHAR(1024) NOT NULL,
    `barcode`        VARCHAR(255)  NOT NULL,
    `expirationDate` DATE          NOT NULL,
    `size`           VARCHAR(255)  NOT NULL,
    `isActive`       BOOLEAN       NOT NULL DEFAULT TRUE,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
