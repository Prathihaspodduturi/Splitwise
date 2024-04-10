CREATE TABLE `payments` (
    `payment_id` int NOT NULL AUTO_INCREMENT,
    `payer_username` varchar(50) NOT NULL,
    `payee_username` varchar(50) NOT NULL,
    `amount` decimal(10,2) NOT NULL,
    `payment_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`payment_id`),
    FOREIGN KEY (`payer_username`) REFERENCES `users`(`username`) ON DELETE CASCADE,
    FOREIGN KEY (`payee_username`) REFERENCES `users`(`username`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE INDEX idx_payments_payer_username ON payments(payer_username);
CREATE INDEX idx_payments_payee_username ON payments(payee_username);
CREATE INDEX idx_payments_payment_date ON payments(payment_date);

ALTER TABLE `payments` ADD COLUMN deleted TINYINT NOT NULL DEFAULT 0;
