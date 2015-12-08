SET autocommit=0;

#初始化
insert into  `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000364','1337862019901','1337862019901','1','/web/init.do?method=initProductSupplier','web_bcgogoSystem_initProductSupplier','request',null,'active');
insert into  `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000364','21000054','31000364',1337862019901,1337862019901,0);
insert into  `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000362','1337862019901','1337862019901','0','/web/payable.do?method=initPurchaseInventoryPayable','web_supplier_initPurchaseInventoryPayable','request',null,'active');
insert into  `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000362','21000054','31000362',1337862019901,1337862019901,0);
#下拉建议
insert into  `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000365','1337862019901','1337862019901','0','/web/searchInventoryIndex.do?method=getCustomerSupplierSuggestion','web_SC_getCustomerSupplierSuggestion','request',null,'active');
insert into  `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000366','1337862019901','1337862019901','0','/web/searchInventoryIndex.do?method=getOrderSuggestion','web_SC_getOrderSuggestion','request',null,'active');
insert into  `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000365','21000006','31000365',1337862019901,1337862019901,0);
insert into  `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000366','21000004','31000366',1337862019901,1337862019901,0);
insert into   `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000367','21000006','31000367',1337862019901,1337862019901,0);
insert into   `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000367','1337862019901','1337862019901','0','/web/customer.do?method=getCustomerOrSupplierSuggestion','web_SC_getCustomerOrSupplierSuggestion','request',null,'active');

#顾客
insert into  `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000363','1337862019901','1337862019901','0','/web/customer.do?method=searchCustomerDataAction','web_customer_searchCustomerDataAction','request',null,'active');
insert into  `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000363','21000033','31000363',1337862019901,1337862019901,0);
#供应商 搜索
insert into  `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000353','1337862019901','1337862019901','0','/web/supplier.do?method=searchSupplierDataAction','web_supplier_searchSupplierDataAction','request',null,'active');
insert into  `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000354','1337862019901','1337862019901','0','/web/payable.do?method=addDeposit','web_supplier_addDeposit','request',null,'active');
insert into  `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000355','1337862019901','1337862019901','0','/web/payable.do?method=searchPayable','web_supplier_searchPayable','request',null,'active');
insert into  `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000356','1337862019901','1337862019901','0','/web/payable.do?method=payToSupplier','web_supplier_=payToSupplier','request',null,'active');
insert into  `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000357','1337862019901','1337862019901','0','/web/payable.do?method=getTotalCountOfPayable','web_supplier_getTotalCountOfPayable','request',null,'active');
insert into  `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000358','1337862019901','1337862019901','0','/web/payable.do?method=payHistoryRecords','web_supplier_payHistoryRecords','request',null,'active');
insert into  `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000359','1337862019901','1337862019901','0','/web/payable.do?method=getCreditAmountBySupplierId','web_supplier_getCreditAmountBySupplierId','request',null,'active');
insert into  `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000360','1337862019901','1337862019901','0','/web/payable.do?method=getSumDepositBySupplierId','web_supplier_getSumDepositBySupplierId','request',null,'active');
insert into  `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000361','1337862019901','1337862019901','0','/web/payable.do?method=checkPaidInventory','web_supplier_checkPaidInventory','request',null,'active');

insert into  `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000353','21000035','31000353',1337862019901,1337862019901,0);
insert into  `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000354','21000035','31000354',1337862019901,1337862019901,0);
insert into  `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000355','21000035','31000355',1337862019901,1337862019901,0);
insert into  `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000356','21000035','31000356',1337862019901,1337862019901,0);
insert into  `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000357','21000035','31000357',1337862019901,1337862019901,0);
insert into  `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000358','21000035','31000358',1337862019901,1337862019901,0);
insert into  `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000359','21000035','31000359',1337862019901,1337862019901,0);
insert into  `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000360','21000035','31000360',1337862019901,1337862019901,0);
insert into  `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000361','21000035','31000361',1337862019901,1337862019901,0);

insert into   `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000369','1337862019901','1337862019901','0','/web/customer.do?method=sendMsgBySearchCondition','web_sms_sendMsgBySearchCondition','request',null,'active');
insert into   `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000369','21000040','31000369',1337862019901,1337862019901,0);


insert into   `user_group_role` (`user_group_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('41000006','21000056','51000057',1337862019901,1337862019901,0);
insert into   `user_group_role` (`user_group_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('41000004','21000056','71000057',1337862019901,1337862019901,0);
insert into   `user_group_role` (`user_group_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('41000003','21000056','81000057',1337862019901,1337862019901,0);
insert into   `user_group_role` (`user_group_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('41000002','21000056','101000057',1337862019901,1337862019901,0);

insert into   `role` (`name`,`id`,`created`,`last_update`,`version`,`status`) VALUES ('web_txn_repair','21000056','1337862019901','1337862019901','0','active');
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('INTEGRATED_SHOP','21000056','22000056',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('TXN_SHOP','21000056','32000056',1337862019901,1337862019901,0);

insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000001','42000001',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000002','42000002',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000003','42000003',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000004','42000004',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000005','42000005',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000006','42000006',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000007','42000007',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000008','42000008',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000009','42000009',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000010','42000010',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000011','42000011',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000012','42000012',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000013','42000013',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000014','42000014',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000056','42000056',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000015','42000015',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000016','42000016',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000017','42000017',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000018','42000018',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000019','42000019',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000020','42000020',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000021','42000021',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000022','42000022',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000023','42000023',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000024','42000024',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000025','42000025',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000026','42000026',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000027','42000027',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000028','42000028',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000029','42000029',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000030','42000030',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000033','42000031',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000034','42000032',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000035','42000033',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000036','42000034',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000037','42000035',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000038','42000036',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000039','42000037',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000040','42000038',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000058','41000058',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000059','41000059',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000041','42000039',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000042','42000040',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000043','42000041',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000044','42000042',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000045','42000043',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000046','42000044',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000048','42000045',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000049','42000046',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000055','42000055',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('REPAIR_SHOP','21000052','42000047',1337862019901,1337862019901,0);

insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000001','52000001',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000002','52000002',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000003','52000003',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000004','52000004',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000005','52000005',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000006','52000006',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000007','52000007',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000056','52000056',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000018','52000018',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000019','52000019',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000020','52000020',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000026','52000026',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000027','52000027',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000028','52000028',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000029','52000029',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000030','52000030',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000033','52000031',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000034','52000032',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000037','52000035',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000038','52000036',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000039','52000037',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000040','52000038',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000058','51000058',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000059','51000059',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000041','52000039',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000042','52000040',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000043','52000041',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000044','52000042',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000045','52000043',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000046','52000044',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000048','52000045',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000049','52000046',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000055','52000055',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000052','52000047',1337862019901,1337862019901,0);
insert into   `shop_role` (`shop_type`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('WASH_SHOP','21000022','52000022',1337862019901,1337862019901,0);

update role_resource set role_id=21000056 where id IN (31000119,31000121,31000053,31000335);


insert into   `user_group_shop` (`id`,`created`,`last_update`,`version`,`user_group_id`,`shop_type`) VALUES ('241000003','1337862019901','1337862019901','0',41000003,'REPAIR_SHOP');
insert into   `user_group_shop` (`id`,`created`,`last_update`,`version`,`user_group_id`,`shop_type`) VALUES ('241000004','1337862019901','1337862019901','0',41000004,'REPAIR_SHOP');

insert into   `user_group_shop` (`id`,`created`,`last_update`,`version`,`user_group_id`,`shop_type`) VALUES ('261000003','1337862019901','1337862019901','0',41000003,'WASH_SHOP');
insert into   `user_group_shop` (`id`,`created`,`last_update`,`version`,`user_group_id`,`shop_type`) VALUES ('261000004','1337862019901','1337862019901','0',41000004,'WASH_SHOP');

# 初始化supplier_record权限
insert into   `resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000368','1337862019901','1337862019901','0','/web/init.do?method=initSupplierRecord','web_bcgogoSystem_initSupplierRecord','request',null,'active');
insert into   `role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000368','21000054','31000368',1337862019901,1337862019901,0);



COMMIT;