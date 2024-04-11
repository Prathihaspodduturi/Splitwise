CREATE TABLE `payments` (
  `payment_id` int NOT NULL AUTO_INCREMENT,
  `payer_username` varchar(50) NOT NULL,
  `payee_username` varchar(50) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `payment_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`payment_id`),
  KEY `idx_payments_payer_username` (`payer_username`),
  KEY `idx_payments_payee_username` (`payee_username`),
  KEY `idx_payments_payment_date` (`payment_date`),
  CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`payer_username`) REFERENCES `users` (`username`) ON DELETE CASCADE,
  CONSTRAINT `payments_ibfk_2` FOREIGN KEY (`payee_username`) REFERENCES `users` (`username`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `payments`
ADD COLUMN `group_id` int NOT NULL,
ADD CONSTRAINT `fk_payments_group` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE CASCADE;


