-- 搜索一期相关
insert into   `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000342','1337862019901','1337862019901','0','/web/inquiryCenter','web_inquiryCenter','render',null,'active');
insert into   `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000337','1337862019901','1337862019901','0','/web/inquiryCenter.do?method=inquiryCenterIndex','web_inquiryCenter_inquiryCenterIndex','request',null,'active');
insert into   `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000338','1337862019901','1337862019901','0','/web/inquiryCenter.do?method=inquiryCenterSearchOrderAction','web_inquiryCenter_inquiryCenterSearchOrderAction','request',null,'active');
insert into   `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000339','1337862019901','1337862019901','0','/web/inquiryCenter.do?method=getItemIndexesByOrderId','web_inquiryCenter_getItemIndexesByOrderId','request',null,'active');
insert into   `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000340','1337862019901','1337862019901','0','/web/product.do?method=getProductSuggestionAndHistory','web_product_searchProductInfoForStockSearch','request',null,'active');

insert into   `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000342','21000018','31000342',1337862019901,1337862019901,0);
insert into   `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000337','21000018','31000337',1337862019901,1337862019901,0);
insert into   `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000338','21000018','31000338',1337862019901,1337862019901,0);
insert into   `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000339','21000018','31000339',1337862019901,1337862019901,0);
insert into   `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000340','21000004','31000340',1337862019901,1337862019901,0);

insert into   `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000341','21000007','31000341',1337862019901,1337862019901,0);
insert into   `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000341','1337862019901','1337862019901','0','/web/customer.do?method=getCustomerOrSupplierSuggestion','web_SC_getCustomerOrSupplierSuggestion','request',null,'active');

-- 会员bugfix相关
insert into   `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000343','21000059','31000343',1337862019901,1337862019901,0);
insert into   `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000343','1337862019901','1337862019901','0','/web/member.do?method=searchSuggestionForServices','web_member_searchSuggestionForServices','request',null,'active');

insert into  `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000345','1337862019901','1337862019901','0','/web/category.do?method=updateServiceStatus','web_member_updateServiceStatus','request',null,'active');
insert into  `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000346','1337862019901','1337862019901','0','/web/category.do?method=checkServiceDisabled','web_member_checkServiceDisabled','request',null,'active');
insert into  `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000347','1337862019901','1337862019901','0','/web/category.do?method=deleteService','web_member_deleteService','request',null,'active');
insert into  `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000348','1337862019901','1337862019901','0','/web/category.do?method=checkServiceUsed','web_member_checkServiceUsed','request',null,'active');

insert into  `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000345','21000059','31000345',1337862019901,1337862019901,0);
insert into  `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000346','21000059','31000346',1337862019901,1337862019901,0);
insert into  `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000347','21000059','31000347',1337862019901,1337862019901,0);
insert into  `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000348','21000059','31000348',1337862019901,1337862019901,0);

insert into   `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000344','1337862019901','1337862019901','0','/web/washBeauty.do?method=getCustomerInfoByMemberNo','web_member_getCustomerInfoByMemberNo','request',null,'active');
insert into   `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000344','21000058','31000344',1337862019901,1337862019901,0);

insert into   `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000349','1337862019901','1337862019901','0','/web/member.do?method=ajaxGetCustomerWithMember','web_member_ajaxGetCustomerWithMember','request',null,'active');
insert into   `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000349','21000058','31000349',1337862019901,1337862019901,0);

insert into   `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000350','1337862019901','1337862019901','0','/web/category.do?method=getObscureServiceByName','web_member_getObscureServiceByName','request',null,'active');
insert into   `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000351','1337862019901','1337862019901','0','/web/category.do?method=getObscureCategoryByName','web_member_getObscureCategoryByName','request',null,'active');
insert into   `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000352','1337862019901','1337862019901','0','/web/txn.do?method=getServiceByServiceName','web_member_getServiceByServiceName','request',null,'active');

insert into   `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000350','21000058','31000350',1337862019901,1337862019901,0);
insert into   `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000351','21000058','31000351',1337862019901,1337862019901,0);
insert into   `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000352','21000058','31000352',1337862019901,1337862019901,0);

-- 会员结算短信 notification
INSERT INTO `message_template` VALUES (10000010001000021, 1, 1318928485391, 1318928485391, 1, 'memberConsumeMsg', '尊敬的会员$!{cardOwnerName}，您于$!{consumeDate}消费#if(${consumeItems}!=$null && ${consumeItems.isEmpty()}==false)项目：#foreach(${item} in ${consumeItems.keySet()})$!{item}$!{consumeItems.get(${item})}次#if($velocityHasNext)，#end#end；#end#if(${consumeAmount}>0)使用卡上金额${consumeAmount}元；#end#if(${remainItems}!=$null && ${remainItems.isEmpty()}==false)剩余项目：#foreach(${item} in ${remainItems.keySet()})$!{item}$!{remainItems.get(${item})}次#if($velocityHasNext)，#end#end#if(${remainAmount}>0)；#else。#end#end#if(${remainAmount}>0)卡上余额${remainAmount}元。#end', '会员结算通知', 'MEMBER_CONSUME', 'NECESSARY');

-- 服务初始化 txn
update service set status = 'ENABLED';

-- 失效日期初始化 bcuser
update member_service set deadline = deadline +86399000 where deadline is not null and deadline <> -1

-- config
insert into config values (10000010004090002, 0, unix_timestamp() * 1000, unix_timestamp() * 1000, -1, 'SelectOptionNumber', 15, null);
insert into config values (10000010004090003, 0, unix_timestamp() * 1000, unix_timestamp() * 1000, -1, 'RecentChangedProductExpirationTime', 60, null);
