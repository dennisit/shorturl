CREATE TABLE `url` (
  `short` varchar(20) NOT NULL,
  `orig` varchar(4096) NOT NULL,
  `flag` boolean NOT NULL default false,
  `reserved` varchar(64) NOT NULL default '',
  PRIMARY KEY  (`short`),
  KEY `orig` (`orig`(64))
) ENGINE=InnoDB DEFAULT CHARSET=utf8
