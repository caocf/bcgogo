/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50515
Source Host           : 192.168.1.186:3306
Source Database       : product

Target Server Type    : MYSQL
Target Server Version : 50515
File Encoding         : 65001

Date: 2013-07-24 14:29:28
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `standard_vehicle_brand`
-- ----------------------------
DROP TABLE IF EXISTS `standard_vehicle_brand`;
CREATE TABLE `standard_vehicle_brand` (
  `id` bigint(20) NOT NULL DEFAULT '0',
  `created` bigint(20) NOT NULL,
  `last_update` bigint(20) NOT NULL,
  `version` bigint(20) NOT NULL,
  `name` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `first_letter` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_standard_vehicle_brand` (`first_letter`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of standard_vehicle_brand
-- ----------------------------
INSERT INTO `standard_vehicle_brand` VALUES ('10000010001', '1374569837377', '1374569837377', '0', 'ABT', 'A');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010002', '1374569837377', '1374569837377', '0', '一汽大众奥迪', 'A');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010003', '1374569837377', '1374569837377', '0', '进口奥迪', 'A');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010004', '1374569837377', '1374569837377', '0', '奥迪RS', 'A');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010005', '1374569837377', '1374569837377', '0', '阿尔法罗密欧', 'A');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010006', '1374569837377', '1374569837377', '0', '阿斯顿马丁', 'A');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010007', '1374569837377', '1374569837377', '0', 'AC Schnitzer', 'A');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010008', '1374569837377', '1374569837377', '0', '华晨宝马', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010009', '1374569837377', '1374569837377', '0', '进口宝马', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010010', '1374569837377', '1374569837377', '0', '宝马M', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010011', '1374569837377', '1374569837377', '0', '宝骏', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010012', '1374569837377', '1374569837377', '0', '保时捷', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010013', '1374569837377', '1374569837377', '0', '北京汽车', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010014', '1374569837377', '1374569837377', '0', '北京汽车制造厂', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010015', '1374569837377', '1374569837377', '0', '北汽威旺', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010016', '1374569837377', '1374569837377', '0', '北京奔驰', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010017', '1374569837377', '1374569837377', '0', '福建奔驰', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010018', '1374569837377', '1374569837377', '0', '进口奔驰', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010019', '1374569837377', '1374569837377', '0', '进口奔驰AMG', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010020', '1374569837377', '1374569837377', '0', '奔腾', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010021', '1374569837377', '1374569837377', '0', '广汽本田', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010022', '1374569837377', '1374569837377', '0', '东风本田', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010023', '1374569837377', '1374569837377', '0', '进口本田', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010024', '1374569837377', '1374569837377', '0', '东风标致', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010025', '1374569837377', '1374569837377', '0', '进口标致', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010026', '1374569837377', '1374569837377', '0', '上海通用别克', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010027', '1374569837377', '1374569837377', '0', '进口别克', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010028', '1374569837377', '1374569837377', '0', '宾利', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010029', '1374569837377', '1374569837377', '0', '比亚迪', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010030', '1374569837377', '1374569837377', '0', '布加迪', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010031', '1374569837377', '1374569837377', '0', '保斐利', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010032', '1374569837377', '1374569837377', '0', '宝龙', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010033', '1374569837377', '1374569837377', '0', '巴博斯', 'B');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010034', '1374569837377', '1374569837377', '0', '长安乘用车', 'C');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010035', '1374569837377', '1374569837377', '0', '长安商用车', 'C');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010036', '1374569837377', '1374569837377', '0', '长城汽车', 'C');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010037', '1374569837377', '1374569837377', '0', '昌河汽车', 'C');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010038', '1374569837377', '1374569837377', '0', '道奇', 'D');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010039', '1374569837377', '1374569837377', '0', '上海大众', 'D');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010040', '1374569837377', '1374569837377', '0', '一汽大众', 'D');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010041', '1374569837377', '1374569837377', '0', '进口大众', 'D');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010042', '1374569837377', '1374569837377', '0', '帝豪', 'D');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010043', '1374569837377', '1374569837377', '0', '东风-郑州日产', 'D');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010044', '1374569837377', '1374569837377', '0', '东风汽车', 'D');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010045', '1374569837377', '1374569837377', '0', '东风校车', 'D');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010046', '1374569837377', '1374569837377', '0', '东风风行', 'D');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010047', '1374569837377', '1374569837377', '0', '东风风神', 'D');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010048', '1374569837377', '1374569837377', '0', '东风风度', 'D');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010049', '1374569837377', '1374569837377', '0', '东风小康', 'D');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010050', '1374569837377', '1374569837377', '0', '东南汽车', 'D');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010051', '1374569837377', '1374569837377', '0', 'DS', 'D');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010052', '1374569837377', '1374569837377', '0', '上汽MAXUS大通', 'D');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010053', '1374569837377', '1374569837377', '0', '大迪', 'D');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010054', '1374569837377', '1374569837377', '0', '底特津电动车', 'D');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010055', '1374569837377', '1374569837377', '0', '法拉利', 'F');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010056', '1374569837377', '1374569837377', '0', '长安菲亚特', 'F');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010057', '1374569837377', '1374569837377', '0', '广汽菲亚特', 'F');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010058', '1374569837377', '1374569837377', '0', '进口菲亚特', 'F');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010059', '1374569837377', '1374569837377', '0', '一汽丰田', 'F');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010060', '1374569837377', '1374569837377', '0', '广汽丰田', 'F');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010061', '1374569837377', '1374569837377', '0', '进口丰田', 'F');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010062', '1374569837377', '1374569837377', '0', '福迪汽车', 'F');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010063', '1374569837377', '1374569837377', '0', '长安福特', 'F');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010064', '1374569837377', '1374569837377', '0', '进口福特', 'F');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010065', '1374569837377', '1374569837377', '0', '福田汽车', 'F');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010066', '1374569837377', '1374569837377', '0', '飞驰', 'F');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010067', '1374569837377', '1374569837377', '0', '菲斯克', 'F');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010068', '1374569837377', '1374569837377', '0', '华翔富奇', 'F');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010069', '1374569837377', '1374569837377', '0', '菲那萨利', 'F');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010070', '1374569837377', '1374569837377', '0', 'GMC', 'G');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010071', '1374569837377', '1374569837377', '0', '光冈自动车', 'G');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010072', '1374569837377', '1374569837377', '0', '观致', 'G');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010073', '1374569837377', '1374569837377', '0', '广汽乘用车', 'G');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010074', '1374569837377', '1374569837377', '0', '广汽日野', 'G');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010075', '1374569837377', '1374569837377', '0', '广汽长丰', 'G');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010076', '1374569837377', '1374569837377', '0', '广汽吉奥', 'G');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010077', '1374569837377', '1374569837377', '0', '哈飞汽车', 'H');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010078', '1374569837377', '1374569837377', '0', '海南海马', 'H');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010079', '1374569837377', '1374569837377', '0', '郑州海马', 'H');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010080', '1374569837377', '1374569837377', '0', '悍马', 'H');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010081', '1374569837377', '1374569837377', '0', '红旗', 'H');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010082', '1374569837377', '1374569837377', '0', '黄海', 'H');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010083', '1374569837377', '1374569837377', '0', '华普汽车', 'H');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010084', '1374569837377', '1374569837377', '0', '华泰汽车', 'H');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010085', '1374569837377', '1374569837377', '0', '海格汽车', 'H');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010086', '1374569837377', '1374569837377', '0', '汇众', 'H');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010087', '1374569837377', '1374569837377', '0', '航天圆通', 'H');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010088', '1374569837377', '1374569837377', '0', '恒天汽车', 'H');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010089', '1374569837377', '1374569837377', '0', '哈弗', 'H');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010090', '1374569837377', '1374569837377', '0', 'Jeep', 'J');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010091', '1374569837377', '1374569837377', '0', '江淮', 'J');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010092', '1374569837377', '1374569837377', '0', '江淮安驰', 'J');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010093', '1374569837377', '1374569837377', '0', '江淮客车', 'J');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010094', '1374569837377', '1374569837377', '0', '江铃', 'J');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010095', '1374569837377', '1374569837377', '0', '捷豹', 'J');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010096', '1374569837377', '1374569837377', '0', '华晨金杯', 'J');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010097', '1374569837377', '1374569837377', '0', '吉利全球鹰', 'J');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010098', '1374569837377', '1374569837377', '0', '江南', 'J');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010099', '1374569837377', '1374569837377', '0', '俊凤', 'J');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010100', '1374569837377', '1374569837377', '0', '九龙', 'J');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010101', '1374569837377', '1374569837377', '0', '金旅', 'J');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010102', '1374569837377', '1374569837377', '0', '上海通用卡迪拉克', 'K');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010103', '1374569837377', '1374569837377', '0', '进口凯迪拉克', 'K');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010104', '1374569837377', '1374569837377', '0', '开瑞', 'K');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010105', '1374569837377', '1374569837377', '0', '东南克莱斯勒', 'K');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010106', '1374569837377', '1374569837377', '0', '进口克莱斯勒', 'K');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010107', '1374569837377', '1374569837377', '0', '柯尼赛格', 'K');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010108', '1374569837377', '1374569837377', '0', '科尼赛克', 'K');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010109', '1374569837377', '1374569837377', '0', '卡尔森', 'K');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010110', '1374569837377', '1374569837377', '0', '兰博基尼', 'L');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010111', '1374569837377', '1374569837377', '0', '劳斯莱斯', 'L');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010112', '1374569837377', '1374569837377', '0', '劳伦士', 'L');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010113', '1374569837377', '1374569837377', '0', '雷克萨斯', 'L');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010114', '1374569837377', '1374569837377', '0', '雷诺', 'L');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010115', '1374569837377', '1374569837377', '0', '莲花汽车', 'L');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010116', '1374569837377', '1374569837377', '0', '力帆汽车', 'L');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010117', '1374569837377', '1374569837377', '0', '长安铃木', 'L');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010118', '1374569837377', '1374569837377', '0', '昌河铃木', 'L');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010119', '1374569837377', '1374569837377', '0', '进口铃木', 'L');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010120', '1374569837377', '1374569837377', '0', '广汽本田-理念', 'L');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010121', '1374569837377', '1374569837377', '0', '林肯', 'L');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010122', '1374569837377', '1374569837377', '0', '陆风汽车', 'L');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010123', '1374569837377', '1374569837377', '0', '路虎', 'L');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010124', '1374569837377', '1374569837377', '0', '路特斯', 'L');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010125', '1374569837377', '1374569837377', '0', '蓝海', 'L');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010126', '1374569837377', '1374569837377', '0', '蓝旗亚', 'L');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010127', '1374569837377', '1374569837377', '0', '迈巴赫', 'M');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010128', '1374569837377', '1374569837377', '0', '玛莎拉蒂', 'M');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010129', '1374569837377', '1374569837377', '0', '长安马自达', 'M');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010130', '1374569837377', '1374569837377', '0', '一汽马自达', 'M');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010131', '1374569837377', '1374569837377', '0', '进口马自达', 'M');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010132', '1374569837377', '1374569837377', '0', 'MG', 'M');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010133', '1374569837377', '1374569837377', '0', 'MINI', 'M');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010134', '1374569837377', '1374569837377', '0', '迈凯轮', 'M');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010135', '1374569837377', '1374569837377', '0', '摩根', 'M');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010136', '1374569837377', '1374569837377', '0', '美亚', 'M');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010137', '1374569837377', '1374569837377', '0', '纳智捷', 'N');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010138', '1374569837377', '1374569837377', '0', '欧宝', 'O');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010139', '1374569837377', '1374569837377', '0', '讴歌', 'O');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010140', '1374569837377', '1374569837377', '0', '一汽欧朗', 'O');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010141', '1374569837377', '1374569837377', '0', '帕加尼', 'P');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010142', '1374569837377', '1374569837377', '0', 'PGO', 'P');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010143', '1374569837377', '1374569837377', '0', '东风日产-启辰', 'Q');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010144', '1374569837377', '1374569837377', '0', '庆铃', 'Q');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010145', '1374569837377', '1374569837377', '0', '奇瑞汽车', 'Q');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010146', '1374569837377', '1374569837377', '0', '东风悦达起亚', 'Q');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010147', '1374569837377', '1374569837377', '0', '进口起亚', 'Q');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010148', '1374569837377', '1374569837377', '0', '全球鹰', 'Q');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010149', '1374569837377', '1374569837377', '0', '郑州日产', 'R');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010150', '1374569837377', '1374569837377', '0', '东风日产', 'R');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010151', '1374569837377', '1374569837377', '0', '进口日产', 'R');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010152', '1374569837377', '1374569837377', '0', '上汽荣威', 'R');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010153', '1374569837377', '1374569837377', '0', '瑞麒', 'R');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010154', '1374569837377', '1374569837377', '0', '萨博', 'S');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010155', '1374569837377', '1374569837377', '0', '东南三菱', 'S');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010156', '1374569837377', '1374569837377', '0', '广汽三菱', 'S');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010157', '1374569837377', '1374569837377', '0', '进口三菱', 'S');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010158', '1374569837377', '1374569837377', '0', '世爵', 'S');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010159', '1374569837377', '1374569837377', '0', '双环', 'S');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010160', '1374569837377', '1374569837377', '0', '双龙', 'S');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010161', '1374569837377', '1374569837377', '0', '斯巴鲁', 'S');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010162', '1374569837377', '1374569837377', '0', '绅宝', 'S');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010163', '1374569837377', '1374569837377', '0', '川汽野马', 'S');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010164', '1374569837377', '1374569837377', '0', '上海大众斯柯达', 'S');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010165', '1374569837377', '1374569837377', '0', '进口斯柯达', 'S');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010166', '1374569837377', '1374569837377', '0', 'Smart', 'S');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010167', '1374569837377', '1374569837377', '0', 'SPIRRA', 'S');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010168', '1374569837377', '1374569837377', '0', 'TESLA', 'T');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010169', '1374569837377', '1374569837377', '0', '天马', 'T');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010170', '1374569837377', '1374569837377', '0', '威麟', 'W');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010171', '1374569837377', '1374569837377', '0', '威兹曼', 'W');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010172', '1374569837377', '1374569837377', '0', '长安沃尔沃', 'W');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010173', '1374569837377', '1374569837377', '0', '进口沃尔沃', 'W');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010174', '1374569837377', '1374569837377', '0', '上汽通用五菱', 'W');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010175', '1374569837377', '1374569837377', '0', '夏利', 'X');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010176', '1374569837377', '1374569837377', '0', '北京现代', 'X');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010177', '1374569837377', '1374569837377', '0', '进口现代', 'X');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010178', '1374569837377', '1374569837377', '0', '西雅特', 'X');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010179', '1374569837377', '1374569837377', '0', '上海通用雪佛兰', 'X');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010180', '1374569837377', '1374569837377', '0', '上汽通用五菱雪佛兰', 'X');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010181', '1374569837377', '1374569837377', '0', '进口雪佛兰', 'X');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010182', '1374569837377', '1374569837377', '0', '东风雪铁龙', 'X');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010183', '1374569837377', '1374569837377', '0', '进口雪铁龙', 'X');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010184', '1374569837377', '1374569837377', '0', '新凯', 'X');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010185', '1374569837377', '1374569837377', '0', '新大地', 'X');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010186', '1374569837377', '1374569837377', '0', '英菲尼迪', 'Y');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010187', '1374569837377', '1374569837377', '0', '吉利英伦', 'Y');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010188', '1374569837377', '1374569837377', '0', '一汽吉林', 'Y');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010189', '1374569837377', '1374569837377', '0', '天津一汽', 'Y');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010190', '1374569837377', '1374569837377', '0', '一汽轻型汽车', 'Y');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010191', '1374569837377', '1374569837377', '0', '一汽通用', 'Y');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010192', '1374569837377', '1374569837377', '0', '南京依维柯', 'Y');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010193', '1374569837377', '1374569837377', '0', '永源汽车', 'Y');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010194', '1374569837377', '1374569837377', '0', '英特诺蒂', 'Y');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010195', '1374569837377', '1374569837377', '0', '宇通客车', 'Y');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010196', '1374569837377', '1374569837377', '0', '亚星客车', 'Y');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010197', '1374569837377', '1374569837377', '0', '华晨中华', 'Z');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010198', '1374569837377', '1374569837377', '0', '众泰汽车', 'Z');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010199', '1374569837377', '1374569837377', '0', '中兴汽车', 'Z');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010200', '1374569837377', '1374569837377', '0', '中客华北', 'Z');
INSERT INTO `standard_vehicle_brand` VALUES ('10000010201', '1374569837377', '1374569837377', '0', '中顺汽车', 'Z');
