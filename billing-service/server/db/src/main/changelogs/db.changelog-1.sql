--liquibase formatted sql

--changeset hien:0
CREATE TABLE `card` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `card_number` varchar(128) NOT NULL,
  `account_number` varchar(128) NOT NULL,
  `exp_date` varchar(128) DEFAULT NULL,
  `cvv` varchar(3) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `card_number` (`card_number`)
) ENGINE=InnoDB AUTO_INCREMENT=1000000 DEFAULT CHARSET=utf8;
	
--changeset hien:1
CREATE TABLE `transaction` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_number` varchar(128) NOT NULL,
  `amount` double NOT NULL,
  `otp` varchar(32) NOT NULL,
  `status` varchar(32) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
	
--changeset hien:2
CREATE TABLE `configuration` (
  `key` varchar(128) NOT NULL,
  `value` text NOT NULL,
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--changeset hien:3
CREATE TABLE `account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_number` varchar(128) NOT NULL,
  `phone_number` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2000000 DEFAULT CHARSET=utf8;
