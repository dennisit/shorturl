CREATE TABLE `share` (
  `user` varchar(40) NOT NULL,
  `type` varchar(16) NOT NULL,
  `token` varchar(64) NOT NULL,
  `secret` varchar(64) NOT NULL,
  PRIMARY KEY  (`user`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
