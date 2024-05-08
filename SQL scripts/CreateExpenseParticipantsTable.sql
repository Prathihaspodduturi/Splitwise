CREATE TABLE `expenseparticipants` (
  `expense_id` int NOT NULL,
  `username` varchar(50) NOT NULL,
  `amount_owed` decimal(10,2) NOT NULL DEFAULT '0.00',
  `amount_paid` decimal(10,2) NOT NULL DEFAULT '0.00',
  `is_deleted` tinyint DEFAULT '0',
  PRIMARY KEY (`expense_id`,`username`),
  KEY `idx_expenseParticipants_username` (`username`),
  CONSTRAINT `expenseparticipants_ibfk_1` FOREIGN KEY (`expense_id`) REFERENCES `expenses` (`expense_id`) ON DELETE CASCADE,
  CONSTRAINT `expenseparticipants_ibfk_2` FOREIGN KEY (`username`) REFERENCES `users` (`username`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
