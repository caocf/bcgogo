# 应付款打印
insert into   bcuser.`resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000370','1337862019901','1337862019901','0','/web/payable.do?method=printPayable','web_supplier_printPayable','request',null,'active');
insert into   bcuser.`resource` (`id`,`created`,`last_update`,`version`,`value`,`name`,`type`,`module_id`,`status`) VALUES ('11000371','1337862019901','1337862019901','0','/web/payable.do?method=printDeposit','web_supplier_printDeposit','request',null,'active');

insert into   bcuser.`role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000370','21000035','31000370',1337862019901,1337862019901,0);
insert into   bcuser.`role_resource` (`resource_id`,`role_id`,`id`,`created`,`last_update`,`version`) VALUES ('11000371','21000035','31000371',1337862019901,1337862019901,0);
