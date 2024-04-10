CREATE TABLE `group_members` (
    group_id int NOT NULL,
    username varchar(50) NOT NULL,
    PRIMARY KEY (`group_id`, `username`),
    FOREIGN KEY (`group_id`) REFERENCES `groups`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`username`) REFERENCES `users`(`username`) ON DELETE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=latin1;


