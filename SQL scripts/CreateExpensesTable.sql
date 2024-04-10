CREATE TABLE `expenses` (
    expense_id int NOT NULL AUTO_INCREMENT,
    group_id int NOT NULL,
    paid_by varchar(50) NOT NULL,
    amount decimal(10,2) NOT NULL,
    description text,
    date_created datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`expense_id`),
    FOREIGN KEY (`group_id`) REFERENCES `groups`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`paid_by`) REFERENCES `users`(`username`) ON DELETE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE INDEX idx_expenses_group_id ON expenses(group_id);
CREATE INDEX idx_expenses_paid_by ON expenses(paid_by);
CREATE INDEX idx_expenses_date_created ON expenses(date_created);

-- adding delete column for undo of deletions --
ALTER TABLE `expenses` ADD COLUMN deleted TINYINT(1) NOT NULL DEFAULT 0;
