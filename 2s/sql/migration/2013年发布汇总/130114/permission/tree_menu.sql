SET autocommit=0;

delete from `tree_menu`;

INSERT INTO `tree_menu` (`id`, `created`, `last_update`, `version`, `text`, `component`, `type`, `icon_Class`, `description`, `sort`, `parent_id`, `leaf`, `role_id`) VALUES (520000001, 1, 1, 1, '系统维护', '', '', '', '系统管理', 1, NULL, 'true', NULL);
INSERT INTO `tree_menu` (`id`, `created`, `last_update`, `version`, `text`, `component`, `type`, `icon_Class`, `description`, `sort`, `parent_id`, `leaf`, `role_id`) VALUES (520000002, 1, 1, 1, '客户管理', '', '', '', '客户管理', 2, NULL, 'true', NULL);
INSERT INTO `tree_menu` (`id`, `created`, `last_update`, `version`, `text`, `component`, `type`, `icon_Class`, `description`, `sort`, `parent_id`, `leaf`, `role_id`) VALUES (520000010, 1, 1, 1, '数据维护', '', '', '', '开发使用的模块', 10, NULL, 'true', NULL);
INSERT INTO `tree_menu` (`id`, `created`, `last_update`, `version`, `text`, `component`, `type`, `icon_Class`, `description`, `sort`, `parent_id`, `leaf`, `role_id`) VALUES (520010001, 1, 1, 1, '角色配置', 'Ext.controller.sys.UserGroupConfiguration', 'COMPONENT', '', '角色配置', 1, 520000001, 'true', 510000001);
INSERT INTO `tree_menu` (`id`, `created`, `last_update`, `version`, `text`, `component`, `type`, `icon_Class`, `description`, `sort`, `parent_id`, `leaf`, `role_id`) VALUES (520010002, 1, 1, 1, '用户管理', 'Ext.controller.sys.UserConfiguration', 'COMPONENT', '', '用户管理', 2, 520000001, 'true', 510000007);
INSERT INTO `tree_menu` (`id`, `created`, `last_update`, `version`, `text`, `component`, `type`, `icon_Class`, `description`, `sort`, `parent_id`, `leaf`, `role_id`) VALUES (520010003, 1, 1, 1, '系统日志', 'Ext.controller.sys.LogController', 'COMPONENT', '', '系统日志', 3, 520000001, 'true', 510000014);
INSERT INTO `tree_menu` (`id`, `created`, `last_update`, `version`, `text`, `component`, `type`, `icon_Class`, `description`, `sort`, `parent_id`, `leaf`, `role_id`) VALUES (520020001, 1, 1, 1, '线索客户', 'Ext.controller.customerMange.customersClues.CustomerClueController', 'COMPONENT', '', '角色配置', 1, 520000002, 'true', 10000010020181918);
INSERT INTO `tree_menu` (`id`, `created`, `last_update`, `version`, `text`, `component`, `type`, `icon_Class`, `description`, `sort`, `parent_id`, `leaf`, `role_id`) VALUES (520100001, 1, 1, 1, '权限维护', 'Ext.controller.dataMaintenance.PermissionController', 'COMPONENT', '', '角色配置', 1, 520000010, 'true', 510000017);
INSERT INTO `tree_menu` (`id`, `created`, `last_update`, `version`, `text`, `component`, `type`, `icon_Class`, `description`, `sort`, `parent_id`, `leaf`, `role_id`) VALUES (520100002, 1, 1, 1, '数据字典', 'Ext.controller.productMaintenance.ProductMainController', 'COMPONENT', '', '数据字典', 1, 520000001, 'true', 10000010020211923);


COMMIT;