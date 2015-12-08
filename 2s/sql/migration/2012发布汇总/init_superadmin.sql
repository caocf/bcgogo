#新增店面
INSERT INTO `config`.`shop`(`id`,`created`,`last_update`,`version`,`name`,`legal_rep`,`no`,`area_id`,`address`,`zip`,`contact`,`landline`,`mobile`,`fax`,`email`,`qq`,`bank`,`account`,`category_id`,`account_name`,`reviewer`,`review_date`,`agent`,`state`,`memo`,`soft_price`) VALUES 
(0,1318928485391,1318928485391,1,'苏州统购','陈子豪','0',0,'苏州市相城经济开发区漕湖科技园C栋12F','215000','邓芳芳','66733331','66733331','0512-66733335','bcgogo@bcgogo.com','123456','农业银行','0000000000000000000','0','陈子豪','陈子豪','1318928485391','徐恒',1,'无',1898);

#新增用户
INSERT INTO `bcuser`.`user` (`id`,`created`,`last_update`,`version`,`shop_id`,`user_no`,`user_name`,`password`,`name`,`email`,`mobile`,`qq`) VALUES (1,1318928485391,1318928485391,0,0,'jackchen','jackchen','25cf8b51c773f3f8dc8b4be867a9a2','陈子豪','jackchen@bcgogo.com','18913104063','1127723152');

#新增组
INSERT INTO `bcuser`.`user_group` (`id`,`created`,`last_update`,`version`,`shop_id`,`name`) VALUES (10000010001000000,1318928485391,1318928485391,0,0,'超级管理员');

#新增用户和组关系
INSERT INTO `bcuser`.`user_group_user`(`id`,`created`,`last_update`,`version`,`user_group_id`,`user_id`)VALUES(10000010001000000,1318928485391,1318928485391,0,10000010001000000,1);

#新增用户组和角色之间的关系
INSERT INTO `bcuser`.`user_group_role`(`id`,`created`,`last_update`,`version`,`user_group_id`,`role_id`)VALUES(10000010001000000,1318928485391,1318928485391,0,10000010001000000,1);


#业务员初始化数据
INSERT INTO `bcuser`.`user`(`id`,`created`,`last_update`,`version`,`shop_id`,`user_no`,`user_name`,`password`,`name`,`email`,`mobile`,`qq`)VALUES(2,1318928485391,1318928485391,0,0,'4007','徐恒','25cf8b51c773f3f8dc8b4be867a9a2','徐恒','569380396@qq.com',15250046402,'569380396');
INSERT INTO `bcuser`.`user`(`id`,`created`,`last_update`,`version`,`shop_id`,`user_no`,`user_name`,`password`,`name`,`email`,`mobile`,`qq`)VALUES(3,1318928485391,1318928485391,0,0,'4008','毛琦','25cf8b51c773f3f8dc8b4be867a9a2','毛琦','413160374@qq.com',13063709961,'413160374');
INSERT INTO `bcuser`.`user`(`id`,`created`,`last_update`,`version`,`shop_id`,`user_no`,`user_name`,`password`,`name`,`email`,`mobile`,`qq`)VALUES(4,1318928485391,1318928485391,0,0,'4002','陈志根','25cf8b51c773f3f8dc8b4be867a9a2','陈志根','644850431@qq.com',13862425979,'644850431');
INSERT INTO `bcuser`.`user`(`id`,`created`,`last_update`,`version`,`shop_id`,`user_no`,`user_name`,`password`,`name`,`email`,`mobile`,`qq`)VALUES(5,1318928485391,1318928485391,0,0,'4011','方宁','25cf8b51c773f3f8dc8b4be867a9a2','方宁','849886547@qq.com',15250297654,'849886547');
INSERT INTO `bcuser`.`user`(`id`,`created`,`last_update`,`version`,`shop_id`,`user_no`,`user_name`,`password`,`name`,`email`,`mobile`,`qq`)VALUES(6,1318928485391,1318928485391,0,0,'4015','王潇阳','25cf8b51c773f3f8dc8b4be867a9a2','王潇阳','415680451@qq.com',15295698831,'415680451');
INSERT INTO `bcuser`.`user`(`id`,`created`,`last_update`,`version`,`shop_id`,`user_no`,`user_name`,`password`,`name`,`email`,`mobile`,`qq`)VALUES(7,1318928485391,1318928485391,0,0,'4016','李振国','25cf8b51c773f3f8dc8b4be867a9a2','李振国','80517545@qq.com',13776113224,'80517545');
INSERT INTO `bcuser`.`user`(`id`,`created`,`last_update`,`version`,`shop_id`,`user_no`,`user_name`,`password`,`name`,`email`,`mobile`,`qq`)VALUES(8,1318928485391,1318928485391,0,0,'4017','陈振宇','25cf8b51c773f3f8dc8b4be867a9a2','陈振宇','451952726@qq.com',18351111806,'451952726');
INSERT INTO `bcuser`.`user`(`id`,`created`,`last_update`,`version`,`shop_id`,`user_no`,`user_name`,`password`,`name`,`email`,`mobile`,`qq`)VALUES(9,1318928485391,1318928485391,0,0,'4018','康国龙','25cf8b51c773f3f8dc8b4be867a9a2','康国龙','122219918@qq.com',13404218791,'122219918');
INSERT INTO `bcuser`.`user`(`id`,`created`,`last_update`,`version`,`shop_id`,`user_no`,`user_name`,`password`,`name`,`email`,`mobile`,`qq`)VALUES(10,1318928485391,1318928485391,0,0,'4019','王培','25cf8b51c773f3f8dc8b4be867a9a2','王培','49348387@qq.com',13586633367,'49348387');

INSERT INTO `bcuser`.`user_group`(`id`,`created`,`last_update`,`version`,`shop_id`,`name`)VALUES(10000010001000001,1318928485391,1318928485391,0,0,'代理商');
INSERT INTO `bcuser`.`user_group`(`id`,`created`,`last_update`,`version`,`shop_id`,`name`)VALUES(10000010001000002,1318928485391,1318928485391,0,0,'代理商');
INSERT INTO `bcuser`.`user_group`(`id`,`created`,`last_update`,`version`,`shop_id`,`name`)VALUES(10000010001000003,1318928485391,1318928485391,0,0,'代理商');
INSERT INTO `bcuser`.`user_group`(`id`,`created`,`last_update`,`version`,`shop_id`,`name`)VALUES(10000010001000004,1318928485391,1318928485391,0,0,'代理商');
INSERT INTO `bcuser`.`user_group`(`id`,`created`,`last_update`,`version`,`shop_id`,`name`)VALUES(10000010001000009,1318928485391,1318928485391,0,0,'代理商');
INSERT INTO `bcuser`.`user_group`(`id`,`created`,`last_update`,`version`,`shop_id`,`name`)VALUES(10000010001000006,1318928485391,1318928485391,0,0,'代理商');
INSERT INTO `bcuser`.`user_group`(`id`,`created`,`last_update`,`version`,`shop_id`,`name`)VALUES(10000010001000007,1318928485391,1318928485391,0,0,'代理商');
INSERT INTO `bcuser`.`user_group`(`id`,`created`,`last_update`,`version`,`shop_id`,`name`)VALUES(10000010001000008,1318928485391,1318928485391,0,0,'代理商');
INSERT INTO `bcuser`.`user_group`(`id`,`created`,`last_update`,`version`,`shop_id`,`name`)VALUES(10000010001000010,1318928485391,1318928485391,0,0,'代理商');

INSERT INTO `bcuser`.`user_group_role`(`id`,`created`,`last_update`,`version`,`user_group_id`,role_id)VALUES(10000010001000001,1318928485391,1318928485391,0,10000010001000001,256);
INSERT INTO `bcuser`.`user_group_role`(`id`,`created`,`last_update`,`version`,`user_group_id`,role_id)VALUES(10000010001000002,1318928485391,1318928485391,0,10000010001000002,256);
INSERT INTO `bcuser`.`user_group_role`(`id`,`created`,`last_update`,`version`,`user_group_id`,role_id)VALUES(10000010001000003,1318928485391,1318928485391,0,10000010001000003,256);
INSERT INTO `bcuser`.`user_group_role`(`id`,`created`,`last_update`,`version`,`user_group_id`,role_id)VALUES(10000010001000004,1318928485391,1318928485391,0,10000010001000004,256);
INSERT INTO `bcuser`.`user_group_role`(`id`,`created`,`last_update`,`version`,`user_group_id`,role_id)VALUES(10000010001000009,1318928485391,1318928485391,0,10000010001000009,256);
INSERT INTO `bcuser`.`user_group_role`(`id`,`created`,`last_update`,`version`,`user_group_id`,role_id)VALUES(10000010001000006,1318928485391,1318928485391,0,10000010001000006,256);
INSERT INTO `bcuser`.`user_group_role`(`id`,`created`,`last_update`,`version`,`user_group_id`,role_id)VALUES(10000010001000007,1318928485391,1318928485391,0,10000010001000007,256);
INSERT INTO `bcuser`.`user_group_role`(`id`,`created`,`last_update`,`version`,`user_group_id`,role_id)VALUES(10000010001000008,1318928485391,1318928485391,0,10000010001000008,256);
INSERT INTO `bcuser`.`user_group_role`(`id`,`created`,`last_update`,`version`,`user_group_id`,role_id)VALUES(10000010001000010,1318928485391,1318928485391,0,10000010001000010,256);

INSERT INTO `bcuser`.`user_group_user`(`id`,`created`,`last_update`,`version`,`user_group_id`,`user_id`)VALUES(10000010001000001,1318928485391,1318928485391,0,10000010001000001,2);
INSERT INTO `bcuser`.`user_group_user`(`id`,`created`,`last_update`,`version`,`user_group_id`,`user_id`)VALUES(10000010001000002,1318928485391,1318928485391,0,10000010001000002,3);
INSERT INTO `bcuser`.`user_group_user`(`id`,`created`,`last_update`,`version`,`user_group_id`,`user_id`)VALUES(10000010001000003,1318928485391,1318928485391,0,10000010001000003,4);
INSERT INTO `bcuser`.`user_group_user`(`id`,`created`,`last_update`,`version`,`user_group_id`,`user_id`)VALUES(10000010001000004,1318928485391,1318928485391,0,10000010001000004,5);
INSERT INTO `bcuser`.`user_group_user`(`id`,`created`,`last_update`,`version`,`user_group_id`,`user_id`)VALUES(10000010001000009,1318928485391,1318928485391,0,10000010001000009,9);
INSERT INTO `bcuser`.`user_group_user`(`id`,`created`,`last_update`,`version`,`user_group_id`,`user_id`)VALUES(10000010001000006,1318928485391,1318928485391,0,10000010001000006,6);
INSERT INTO `bcuser`.`user_group_user`(`id`,`created`,`last_update`,`version`,`user_group_id`,`user_id`)VALUES(10000010001000007,1318928485391,1318928485391,0,10000010001000007,7);
INSERT INTO `bcuser`.`user_group_user`(`id`,`created`,`last_update`,`version`,`user_group_id`,`user_id`)VALUES(10000010001000008,1318928485391,1318928485391,0,10000010001000008,8);
INSERT INTO `bcuser`.`user_group_user`(`id`,`created`,`last_update`,`version`,`user_group_id`,`user_id`)VALUES(10000010001000010,1318928485391,1318928485391,0,10000010001000010,10);

#新增店面
INSERT INTO `config`.`shop`(`id`,`created`,`last_update`,`version`,`name`,`legal_rep`,`no`,`area_id`,`address`,`zip`,`contact`,`landline`,`mobile`,`fax`,`email`,`qq`,`bank`,`account`,`category_id`,`account_name`,`reviewer`,`review_date`,`agent`,`state`,`memo`,`soft_price`) VALUES
(100,1318928485391,1318928485391,1,'苏州统购','陈子豪','0',0,'苏州市相城经济开发区漕湖科技园C栋12F','215000','邓芳芳','66733331','66733331','0512-66733335','bcgogo@bcgogo.com','123456','农业银行','0000000000000000000','0','陈子豪','陈子豪','1318928485391','徐恒',1,'无',1898);

#新增用户
INSERT INTO `bcuser`.`user` (`id`,`created`,`last_update`,`version`,`shop_id`,`user_no`,`user_name`,`password`,`name`,`email`,`mobile`,`qq`) VALUES(100,1318928485391,1318928485391,0,100,'test','test','1bbd886460827015e5d65ed44252251','陈子豪','jackchen1@bcgogo.com','18913104064','1127723151');

#新增组
INSERT INTO `bcuser`.`user_group` (`id`,`created`,`last_update`,`version`,`shop_id`,`name`) VALUES(10000010001000005,1318928485391,1318928485391,0,100,'超级管理员');

#新增用户和组关系
INSERT INTO `bcuser`.`user_group_user`(`id`,`created`,`last_update`,`version`,`user_group_id`,`user_id`)VALUES(10000010001000005,1318928485391,1318928485391,0,10000010001000005,100);

#新增用户组和角色之间的关系
INSERT INTO `bcuser`.`user_group_role`(`id`,`created`,`last_update`,`version`,`user_group_id`,`role_id`)VALUES(10000010001000005,1318928485391,1318928485391,0,10000010001000005,1);















