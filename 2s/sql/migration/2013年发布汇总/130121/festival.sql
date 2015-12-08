/*
Navicat MySQL Data Transfer

Source Server         : 192.168.1.245_name
Source Server Version : 50519
Source Host           : 192.168.1.245:3306
Source Database       : notification

Target Server Type    : MYSQL
Target Server Version : 50519
File Encoding         : 65001

Date: 2013-01-21 23:29:14
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `festival`
-- ----------------------------
DROP TABLE IF EXISTS `festival`;
CREATE TABLE `festival` (
  `id` bigint(20) NOT NULL DEFAULT '0',
  `version` bigint(20) DEFAULT NULL,
  `created` bigint(20) NOT NULL,
  `last_update` bigint(20) NOT NULL,
  `shop_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `title` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `content` mediumblob,
  `release_date` bigint(20) DEFAULT NULL,
  `pre_day` bigint(20) DEFAULT NULL,
  `start_remind_date` bigint(20) DEFAULT NULL,
  `end_remind_date` bigint(20) DEFAULT NULL,
  `release_man_id` bigint(20) DEFAULT NULL,
  `release_man` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `status` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `create_date` bigint(20) DEFAULT NULL,
  `frequency` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_FESTIVAL_RELEASEDATE` (`release_date`),
  KEY `IDX_FESTIVAL_STARTREMINDDATE` (`start_remind_date`),
  KEY `IDX_FESTIVAL_ENDREMINDDATE` (`end_remind_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of festival
-- ----------------------------
INSERT INTO `festival` VALUES ('10000010012231123', '0', '1358715288935', '1358715288935', null, null, '春节', null, '1360425600000', '2', '1360252800000', '1360511999000', '1', '陈子豪', 'ENABLED', '1358715288933', null);
INSERT INTO `festival` VALUES ('10000010012231124', '0', '1358715321113', '1358715321113', null, null, '清明节', null, '1365004800000', '2', '1364832000000', '1365091199000', '1', '陈子豪', 'ENABLED', '1358715321111', null);
INSERT INTO `festival` VALUES ('10000010012231125', '0', '1358715351467', '1358715351467', null, null, '劳动节', null, '1367337600000', '2', '1367164800000', '1367423999000', '1', '陈子豪', 'ENABLED', '1358715351464', null);
INSERT INTO `festival` VALUES ('10000010012231126', '0', '1358715375323', '1358715375323', null, null, '端午节', null, '1370966400000', '2', '1370793600000', '1371052799000', '1', '陈子豪', 'ENABLED', '1358715375321', null);
INSERT INTO `festival` VALUES ('10000010012231127', '0', '1358715402462', '1358715402462', null, null, '中秋节', null, '1379520000000', '2', '1379347200000', '1379606399000', '1', '陈子豪', 'ENABLED', '1358715402460', null);
INSERT INTO `festival` VALUES ('10000010012231128', '0', '1358715428699', '1358715428699', null, null, '国庆节', null, '1380556800000', '2', '1380384000000', '1380643199000', '1', '陈子豪', 'ENABLED', '1358715428697', null);
INSERT INTO `festival` VALUES ('10000010012231129', '0', '1358715460855', '1358715460855', null, null, '元旦', null, '1388505600000', '2', '1388332800000', '1388591999000', '1', '陈子豪', 'ENABLED', '1358715460854', null);
INSERT INTO `festival` VALUES ('10000010012231130', '0', '1358715489813', '1358715489813', null, null, '春节', null, '1391097600000', '2', '1390924800000', '1391183999000', '1', '陈子豪', 'ENABLED', '1358715489811', null);
INSERT INTO `festival` VALUES ('10000010012231131', '0', '1358715533423', '1358715533423', null, null, '清明节', null, '1396627200000', '2', '1396454400000', '1396713599000', '1', '陈子豪', 'ENABLED', '1358715533422', null);
INSERT INTO `festival` VALUES ('10000010012231132', '0', '1358715555127', '1358715555127', null, null, '劳动节', null, '1398873600000', '2', '1398700800000', '1398959999000', '1', '陈子豪', 'ENABLED', '1358715555126', null);
INSERT INTO `festival` VALUES ('10000010012231133', '0', '1358715582078', '1358715582078', null, null, '端午节', null, '1401638400000', '2', '1401465600000', '1401724799000', '1', '陈子豪', 'ENABLED', '1358715582076', null);
INSERT INTO `festival` VALUES ('10000010012231134', '0', '1358715611338', '1358715611338', null, null, '中秋节', null, '1410105600000', '2', '1409932800000', '1410191999000', '1', '陈子豪', 'ENABLED', '1358715611337', null);
INSERT INTO `festival` VALUES ('10000010012231135', '1', '1358715637201', '1358779733608', null, null, '国庆节', null, '1412092800000', '2', '1411920000000', '1412179199000', '1', '陈子豪', 'ENABLED', '1358715637200', null);
