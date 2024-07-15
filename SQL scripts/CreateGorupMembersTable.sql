

CREATE TABLE `group_members` (
  `id` int AUTO_INCREMENT PRIMARY KEY,
  `group_id` int NOT NULL,
  `username` varchar(50) NOT NULL,
  `removed_by` varchar(50) DEFAULT NULL,
  `removed_date` datetime DEFAULT NULL,
  `added_by` varchar(50) DEFAULT NULL,
  `added_date` datetime DEFAULT NULL,
  UNIQUE KEY `unique_group_user` (`group_id`, `username`),
  KEY `username` (`username`),
  KEY `group_members_ibfk_3` (`removed_by`),
  KEY `group_members_ibfk_4` (`added_by`),
  CONSTRAINT `group_members_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE CASCADE,
  CONSTRAINT `group_members_ibfk_2` FOREIGN KEY (`username`) REFERENCES `users` (`username`) ON DELETE CASCADE,
  CONSTRAINT `group_members_ibfk_3` FOREIGN KEY (`removed_by`) REFERENCES `users` (`username`) ON DELETE SET NULL,
  CONSTRAINT `group_members_ibfk_4` FOREIGN KEY (`added_by`) REFERENCES `users` (`username`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
