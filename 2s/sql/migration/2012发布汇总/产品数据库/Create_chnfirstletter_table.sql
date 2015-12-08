CREATE TABLE `chnfirstletter` (
  `id` bigint(20) NOT NULL auto_increment,
  `hanzi` varchar(10) default NULL,
  `py` varchar(20) default NULL,
  `firstLetter` varchar(5) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8