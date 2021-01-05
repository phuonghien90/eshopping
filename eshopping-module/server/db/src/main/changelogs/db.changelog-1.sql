--liquibase formatted sql

--changeset hien:0
CREATE TABLE `order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `phone_number` varchar(128) NOT NULL,
  `sku_code` varchar(128) NOT NULL,
  `payment_id` varchar(128) DEFAULT NULL,
  `voucher_code` varchar(64) DEFAULT NULL,
  `status` varchar(32) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `phone_number` (`phone_number`, `status`, `created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1000000 DEFAULT CHARSET=utf8;

--changeset hien:1
CREATE TABLE `product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sku_code` varchar(128) NOT NULL,
  `price` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `sku_code` (`sku_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--changeset hien:2
CREATE TABLE `configuration` (
  `key` varchar(128) NOT NULL,
  `value` text NOT NULL,
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--changeset hien:3
ALTER TABLE `order` CHANGE `payment_id` `purchase_id` int(11) DEFAULT NULL;

--changeset hien:4
ALTER TABLE `product` ADD COLUMN `name` varchar(128) after `sku_code`;