进入user库
insert into  `role` (`id`,`created`,`last_update`,`version`,`name`,`status`,`memo`,`value`,`type`,`module_id`) VALUES ('510000023','1','1','1','CRM_SYS_FESTIVAL_SAVE','active','保存节日','保存节日','CRM','500000001');
insert into  `role` (`id`,`created`,`last_update`,`version`,`name`,`status`,`memo`,`value`,`type`,`module_id`) VALUES ('510000024','1','1','1','CRM_SYS_FESTIVAL_EDIT','active','编辑节日','编辑节日','CRM','500000001');
insert into `user_group_role` (`user_group_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES (41000006,510000023,510000023,1,1,1);
insert into `user_group_role` (`user_group_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES (41000006,510000024,510000024,1,1,1);
insert into  `tree_menu` (`id`,`created`,`last_update`,`version`,`text`,`component`,`type`,`icon_class`,`description`,`sort`,`parent_id`,`role_id`,`leaf`) VALUES ('520010022','1','1','1','节日管理','Ext.controller.sys.FestivalController','COMPONENT','','节日管理','6',520000001,510000023,'true');


