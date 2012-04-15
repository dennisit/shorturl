CREATE TABLE `user` (
  `user` varchar(40) NOT NULL,
  `pass` varchar(50) NOT NULL,
  `reserved` varchar(64) NOT NULL default '',
  PRIMARY KEY  (`user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
