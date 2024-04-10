CREATE TABLE `groups` (
  `id` int NOT NULL AUTO_INCREMENT,
  `group_name` varchar(500) NOT NULL,
  `description` text,
  `date_created` datetime NOT NULL default current_timestamp,
  `created_by` varchar(50),
  PRIMARY KEY (`id`),
  FOREIGN KEY (`created_by`) REFERENCES `users`(`username`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

-- Index for `group_name`
CREATE INDEX idx_group_name ON `groups`(group_name(191));

-- Index for `date_created`
CREATE INDEX idx_date_created ON `groups`(date_created);

-- Index for `created_by`
CREATE INDEX idx_created_by ON `groups`(created_by);

-- add deleted column for undo deletions --
ALTER TABLE `groups` ADD COLUMN deleted TINYINT(1) NOT NULL DEFAULT 0;

