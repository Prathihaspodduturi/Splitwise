CREATE TABLE `expenseParticipants` (
    `expense_id` int NOT NULL,
    `username` varchar(50) NOT NULL,
    `amount_owed` decimal(10,2) NOT NULL,
    PRIMARY KEY (`expense_id`, `username`),
    FOREIGN KEY (`expense_id`) REFERENCES `expenses`(`expense_id`) ON DELETE CASCADE,
    FOREIGN KEY (`username`) REFERENCES `users`(`username`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE INDEX idx_expenseParticipants_username ON `expenseParticipants`(username);

