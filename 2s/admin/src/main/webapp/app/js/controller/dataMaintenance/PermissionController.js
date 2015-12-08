Ext.define('Ext.controller.dataMaintenance.PermissionController', {
    extend:'Ext.app.Controller',

    stores:[
        "Ext.store.dataMaintenance.Modules",
        "Ext.store.dataMaintenance.Roles",
        "Ext.store.dataMaintenance.Resources",
        "Ext.store.dataMaintenance.UserGroups",
        "Ext.store.dataMaintenance.ShopVersions"
    ],

    models:[
        "Ext.model.dataMaintenance.Module",
        "Ext.model.dataMaintenance.Resource"
    ],

    views:[
        'Ext.view.dataMaintenance.permission.PermissionView'
    ],

    requires:[
        "Ext.utils.ComponentUtils",
        "Ext.utils.PermissionUtils",
        "Ext.utils.Common"
    ],

    refs:[
        //树菜单
        {
            ref:'moduleTreeMenu',
            selector:'moduleTreeMenu',
            xtype:'moduleTreeMenu',
            autoCreate:true
        },
        {
            ref:'addResourceWindow',
            selector:'addResourceWindow',
            xtype:'addResourceWindow',
            autoCreate:true
        },
        {
            ref:'updateResourceWindow',
            selector:'updateResourceWindow',
            xtype:'updateResourceWindow',
            autoCreate:true
        },
        {
            ref:'resourceRoleWindow',
            selector:'resourceRoleWindow',
            xtype:'resourceRoleWindow',
            autoCreate:true
        },
        {
            ref:'addUserGroupWindow',
            selector:'addUserGroupWindow',
            xtype:'addUserGroupWindow',
            autoCreate:true
        },
        {
            ref:'addShopVersionWindow',
            selector:'addShopVersionWindow',
            xtype:'addShopVersionWindow',
            autoCreate:true
        },
        {
            ref:'addRoleToResourceWindow',
            selector:'addRoleToResourceWindow',
            xtype:'addRoleToResourceWindow',
            autoCreate:true
        },
        //对应shop类型 下的role资源
        {ref:'roleTreeForShopType', selector:'roleTreeForShopType'},
        //对应shop类型下的用户组下的资源
        {ref:'roleTreeForUserGroup', selector:'roleTreeForUserGroup'},
        // 对应shop类型下的用户组
        {ref:'userGroupList', selector:'permissionUserGroupList'},
        {ref:'shopVersionForm', selector:'shopVersionForm'},
        {ref:'shopVersionList', selector:'permissionShopVersionList'},
        {ref:'moduleTree', selector:'moduleTree'},
        {ref:'resourceList', selector:'permissionResourceList'},
        {ref:'resourceRoleWindow', selector:'resourceRoleWindow'},
        {ref:'resourceRoleList', selector:'resourceRoleWindow resourceRoleList'},
        {ref:'addResourceForm', selector:'addResourceWindow resourceForm'},
        {ref:'updateResourceForm', selector:'updateResourceWindow resourceForm'},
        {ref:'roleResources', selector:'permissionRoleResources'},
        {ref:'roleTreeForResourceRole', selector:'addRoleToResourceWindow modelTreePicker'}
    ],

    init:function () {
        var me = this;
        this.componentUtils = Ext.create("Ext.utils.ComponentUtils");
        this.permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        this.commonUtils = Ext.create("Ext.utils.Common");
        this.control({
            //模块 tree
            "dataMaintenancePermissionView moduleTree":{
                itemcontextmenu:me.treeMenu,
                edit:me.updateModuleOrRole,
                itemclick:function (view, record, item, rowIndex, e) {
                    var contextMenu = this.getModuleTreeMenu();
                    if (record.data.id != -1) {
                        if (record.get('type') == "ROLE") {
                            contextMenu.setRole(record);
                            contextMenu.setModule(null);
                            me.showRoleDetails(record.get("id"))
                        } else {
                            contextMenu.setModule(record);
                            contextMenu.setRole(null);
                            me.getRoleResources().store.removeAll()
                        }
                    } else {
                        me.getRoleResources().store.removeAll()
                    }
                }
            },
            //resource 下面的roles
            "resourceRoleWindow resourceRoleList":{
                selectionchange:function (view, records) {
                    var enable = !records.length;
                    this.getResourceRoleList().down('button[action=delRole]').setDisabled(enable);
                    me.getTreeModuleRolesForUserGroup();
                }
            },
            "resourceRoleWindow resourceRoleList button[action=add]":{
                click:function () {
                    me.getAddRoleToResourceWindow().show();
                }
            },
            //resource 下 add roles
            "addRoleToResourceWindow button[action=save]":{
                click:function () {
                    var role = me.getRoleTreeForResourceRole(),
                        resourceRec = me.getResourceList().getSelectionModel().getSelection()[0],
                        resourceId = resourceRec.get("resourceId"),
                        roleId = role.getValue();
                    if (roleId && resourceId) {
                        me.saveRoleResource("", roleId, resourceId, null, function () {
                            me.getResourceRoleList().store.load();
                        })
                    }
                }
            },
            'addRoleToResourceWindow treepicker':{
                select:function (view, records, eOpts) {
                    if (records.data.type == "MODULE" || records.data.id === -1) {
                        Ext.Msg.alert('警告', "请选择角色！");
                        view.setRawValue("");
                    }
                }
            },
            "resourceRoleWindow":{
                beforehide:function (panel) {
                    var roleWin = me.getAddRoleToResourceWindow();
                    roleWin.close();
                }
            },
            //
            "resourceRoleWindow resourceRoleList button[action=delRole]":{
                click:function () {
                    var me = this,
                        resourceRec = me.getResourceList().getSelectionModel().getSelection()[0],
                        resourceId = resourceRec.get("resourceId"),
                        roleRec = me.getResourceRoleList().getSelectionModel().getSelection()[0],
                        roleId = roleRec.get("id");
                    me.deleteRoleResource(roleId, resourceId, function () {
                        me.showResourceDetails();
//                        me.getResourceRoleList().store.load();
                    });
                }
            },
            "permissionResourceList":{
                afterrender:function (view) {
                    view.down("#searchSystemType").setValue("SHOP");
                    me.getResourceList().store.proxy.extraParams = {
                        systemType:'SHOP'
                    };
                    me.getResourceList().store.loadPage(1);
                },
                selectionchange:function (view, records) {
                    var enable = !records.length;
                    Ext.getCmp('delResourceButton').setDisabled(enable);
                    Ext.getCmp('editResourceButton').setDisabled(enable);
                    Ext.getCmp('resourceDetailsButton').setDisabled(enable);
                    me.getResourceList().down('button[action=addToRole]').setDisabled(enable);
                }
            },
            'permissionResourceList button[action=search]':{
                click:me.onSearchResources
            },
            'permissionResourceList button[action=refresh]':{
                click:me.refreshPermission
            },
            //UserGroup of shop version
            'permissionUserGroupList':{
                selectionchange:function (view, records) {
                    var enable = !records.length, userGroupId;
                    me.getUserGroupList().down('button[action=delete]').setDisabled(enable);
                    me.getTreeModuleRolesForUserGroup();
                },
                edit:function (editor, e) {
                    me.commonUtils.ajax({
                        url:'userGroup.do?method=updateUserGroup',
                        params:e.record.getData(),
                        success:function (result) {
                            e.record.commit();
                        }
                    });
                }
            },
            //shop version 下的用户组操作 打开window
            'permissionUserGroupList button[action=add]':{
                click:function () {
                    me.getAddUserGroupWindow().show();
                }
            },
            'permissionUserGroupList button[action=delete]':{
                click:me.deleteUserGroup
            },
            //form 保存user group
            'addUserGroupWindow userGroupForm button[action=save]':{
                click:me.saveUserGroupForVersion
            },
            //shop versions
            'permissionShopVersionList':{
                afterrender:function (view) {
                    me.getShopVersionList().store.load();
                    me.getUserGroupList().store.removeAll();
                    me.removeAllChildren(me.getRoleTreeForShopType().store);
                    me.removeAllChildren(me.getRoleTreeForUserGroup().store);
                },
                selectionchange:function (view, records) {
                    var enable = !records.length, shopVersionId;
                    this.getShopVersionList().down('button[action=delete]').setDisabled(enable);
                    this.getUserGroupList().down('button[action=add]').setDisabled(enable);
                    // 重建role tree
                    if (records.length <= 0) {
                        shopVersionId = null;
                    } else {
                        shopVersionId = records[0].get("id")
                    }
                    me.loadUserGroupList(shopVersionId);
                    me.getTreeModuleRolesForShopVersion(shopVersionId);
                    me.getUserGroupList().setTitle(records[0].get("value") + "-版本所含用户组");
                    me.getRoleTreeForShopType().setTitle(records[0].get("value") + "-版本所含角色");
                    me.removeAllChildren(me.getRoleTreeForUserGroup().store);
                },
                edit:function (editor, e) {
                    me.commonUtils.ajax({
                        url:'shopVersion.do?method=saveOrUpdateShopVersion',
                        params:e.record.getData(),
                        success:function (result) {
                            e.record.commit();
                        }
                    });
                }
            },
            'permissionShopVersionList button[action=add]':{
                click:function () {
                    me.getAddShopVersionWindow().show();
                }
            },
            'permissionShopVersionList button[action=delete]':{
                click:me.deleteShopVersion
            },
            //role of user group
            'roleTreeForUserGroup button[action=save]':{
                click:me.saveRolesConfigForUserGroup
            },
            'roleTreeForShopType':{
                checkchange:me.checkChangeForTreeRoles
            },
            //role of version
            'roleTreeForShopType button[action=save]':{
                click:me.saveRolesConfigForShopType
            },
            'roleTreeForShopType button[action=expand]':{
                click:function () {
                    var roleTree = me.getRoleTreeForShopType(),
                        rec = roleTree.getSelectionModel().getSelection()[0];
                    rec.expand(true);
                }
            },
            'roleTreeForUserGroup':{
                checkchange:me.checkChangeForTreeRoles
            },
            'roleTreeForUserGroup button[action=expand]':{
                click:function () {
                    var roleTree = me.getRoleTreeForUserGroup(),
                        rec = roleTree.getSelectionModel().getSelection()[0];
                    if (rec) {
                        rec.expand(true);
                    }
                }
            },
            'permissionResourceList button[action=addResource]':{
                click:function () {
                    me.commonUtils.mask();
                    me.getAddResourceWindow().show();
                }
            },
            'addShopVersionWindow shopVersionForm button[action=save]':{
                click:me.addShopVersion
            },
            'permissionResourceList button[id=editResourceButton]':{
                click:me.editResource
            },
            'permissionResourceList button[id=resourceDetailsButton]':{
                click:me.showResourceDetails
            },
            'permissionResourceList button[id=delResourceButton]':{
                click:me.deleteResource
            },
            'permissionResourceList button[action=addToRole]':{
                click:function () {
                    var contextMenu = this.getModuleTreeMenu(),
                        roleRec = contextMenu.getRole(),
                        moduleRec = contextMenu.getModule(),
                        resourceRec = me.getResourceList().getSelectionModel().getSelection()[0],
                        resourceId = resourceRec.get("resourceId"),
                        roleId, moduleId;
                    if (roleRec) {
                        roleId = roleRec.get("id");
                    } else {
                        moduleId = moduleRec.get("id");
                    }
                    if (resourceId) {
                        if (moduleId) {
                            Ext.MessageBox.confirm('确认', "确定把资源添加到(" + moduleRec.get("value") + ")的所有角色下?", function (btn) {
                                if (btn == "yes") {
                                    me.saveRoleResource(moduleId, "", resourceId, moduleRec.get("systemType"), function () {
                                    });
                                }
                            });
                        } else {
                            me.saveRoleResource("", roleId, resourceId,null, function () {
                                me.getRoleResources().store.load();
                            })
                        }
                    }
                }
            },
            'addResourceWindow resourceForm button[action=save]': {
                click: function () {
                    me.getAddResourceWindow().down("form").addAndUpdateResource(function () {
                        me.getResourceList().store.loadPage(1);
//                        me.getAddResourceWindow().close();
                    });
                }
            },
            'updateResourceWindow resourceForm button[action=save]': {
                click: function () {
                    me.getUpdateResourceWindow().down("form").addAndUpdateResource(function () {
                        me.getResourceList().store.loadPage(1);
//                        me.getUpdateResourceWindow().close();
                    });
                }
            },
            "dataMaintenancePermissionView permissionRoleResources actioncolumn":{
                click:function (grid, cell, row, col, e) {
                    var rec = grid.getStore().getAt(row),
                        contextMenu = this.getModuleTreeMenu(),
                        role = contextMenu.getRole();
                    me.deleteRoleResource(role.get("id"), rec.get("resourceId"), function () {
                        me.showRoleDetails(role.get("id"));
                    });
                }
            },
            //Module tree menu
            "[id=addRole]":{
                click:me.addRole
            },
            "[id=addModule]":{
                click:me.addModule
            },
            "[id=editModule]":{
                click:me.editModule
            },
            "[id=editRole]":{
                click:me.editRole
            },
            "[id=copyRole]":{
                click:me.copyRole
            },
            "[id=pasteRole]":{
                click:me.pasteRole
            },
            //删除部门
            "[id=deleteModule]":{
                click:me.deleteModule
            },
            //删除职位
            "[id=deleteRole]":{
                click:me.deleteRole
            },
            "[id=refreshModuleTree]":{
                click:function () {
                    me.getModuleTree().store.load();
                }
            }
        });
    },
    saveRoleResource:function (moduleId, roleId, resourceId, systemType, callBack) {
        if ((roleId || moduleId) && resourceId) {

            this.commonUtils.ajax({
                url:'resource.do?method=saveRoleResource',
                params:{
                    moduleId:moduleId,
                    resourceId:resourceId,
                    systemType:systemType,
                    roleId:roleId
                },
                success:function (result) {
                    callBack();
                }
            });
        }
    },
    removeAllChildren:function (store) {
        store.getRootNode().removeAll();
    },
    loadUserGroupList:function (shopVersionId) {
        var me = this;
        me.getUserGroupList().store.proxy.extraParams = {
            shopVersionId:shopVersionId
        };
        me.getUserGroupList().store.load();
    },
    getTreeModuleRolesForShopVersion:function (shopVersionId) {
        var me = this;
        me.getRoleTreeForShopType().store.proxy.api.read = "shopVersion.do?method=getTreeModuleRolesForShopVersion";
        me.getRoleTreeForShopType().store.proxy.extraParams = {
            shopVersionId:shopVersionId
        };
        me.getRoleTreeForShopType().store.load({
            callback:function () {
                me.getRoleTreeForShopType().expand();
            }
        });
    },
    getTreeModuleRolesForUserGroup:function () {
        var me = this, userGroupId , shopVersionId , roleTreeForUserGroup = me.getRoleTreeForUserGroup(),
            userGroupRec = me.getUserGroupList().getSelectionModel().getSelection()[0],
            shopVersionRec = me.getShopVersionList().getSelectionModel().getSelection()[0];
        if (userGroupRec) {
            userGroupId = userGroupRec.get("id");
        }
        if (shopVersionRec) {
            shopVersionId = shopVersionRec.get("id");
        }
        if (userGroupRec && shopVersionRec) {
            roleTreeForUserGroup.setTitle(shopVersionRec.get("value") + "-" + userGroupRec.get("name") + "-用户组所含角色");
        } else {
            roleTreeForUserGroup.setTitle("用户组所含角色");
        }
        roleTreeForUserGroup.store.proxy.api.read = "shopVersion.do?method=getTreeModuleRolesForUserGroup";
        roleTreeForUserGroup.store.proxy.extraParams = {
            userGroupId:userGroupId,
            shopVersionId:shopVersionId
        };
        roleTreeForUserGroup.store.load({
            callback:function () {
                me.getRoleTreeForUserGroup().expand();
            }
        });
    },
    //更新配置
    saveRolesConfigForShopType:function () {
        var me = this,
            roleIds = "", i, max  ,
            shopVersionRec = me.getShopVersionList().getSelectionModel().getSelection()[0],
            versionId = shopVersionRec.get("id"),
            roleTreeForShopType = me.getRoleTreeForShopType(),
            checkedRec = roleTreeForShopType.getChecked();
        for (i = 0, max = checkedRec.length; i < max; i++) {
            if (checkedRec[i].get("type") != "ROLE")continue;
            roleIds += checkedRec[i].get("id") + ","
        }
        roleTreeForShopType.store.proxy.api.update = 'shopVersion.do?method=saveRolesConfigForShopVersion';
        roleTreeForShopType.store.proxy.extraParams = {
            versionId:versionId,
            roleIds:roleIds
        };
        roleTreeForShopType.store.update();
        me.getTreeModuleRolesForUserGroup();
    },
    saveRolesConfigForUserGroup:function () {
        var me = this, roleIds = "", i, max  ,
            userGroupRec = me.getUserGroupList().getSelectionModel().getSelection()[0],
            userGroupId = userGroupRec.get("id"),
            roleTreeForUserGroup = me.getRoleTreeForUserGroup(),
            records = roleTreeForUserGroup.getChecked();
        for (i = 0, max = records.length; i < max; i++) {
            roleIds += records[i].data.id + ",";
        }
        roleTreeForUserGroup.store.proxy.api.update = 'userGroup.do?method=saveRolesConfigForUserGroup';
        roleTreeForUserGroup.store.proxy.extraParams = {
            userGroupId:userGroupId,
            roleIds:roleIds
        };
        roleTreeForUserGroup.store.update();
    },
    deleteResource:function () {
        var me = this,
            rec = me.getResourceList().getSelectionModel().getSelection()[0],
            resourceId = rec.get("resourceId");
        me.commonUtils.ajax({
            url:'resource.do?method=checkResourceBeforeDelete',
            params:{
                resourceId:resourceId
            },
            success:function (result) {
                if (result['hasBeUsed']) {
                    Ext.Msg.alert('警告', "该资源尚有资源使用!");
                } else {
                    me.deleteResourceImplementor(resourceId);
                }
            }
        });
    },
    deleteShopVersion:function () {
        var me = this,
            shopVersionList = me.getShopVersionList(),
            rec = shopVersionList.getSelectionModel().getSelection()[0],
            shopVersionId = rec.get("id");
        Ext.MessageBox.confirm('确认', '删除版本可能删除版本下面的资源，确认删除?', function (btn) {
            if (btn == "yes") {
                me.commonUtils.ajax({
                    url:'shopVersion.do?method=deleteShopVersion',
                    params:{shopVersionId:shopVersionId},
                    success:function (result) {
                        shopVersionList.store.load();
                    }
                });
            }
        });
    },
    saveUserGroupForVersion:function () {
        var me = this,
            form = me.getAddUserGroupWindow().down("form"),
            formEl = form.getEl(),
            baseForm = form.form,
            shopVersionList = me.getShopVersionList(),
            shopVersionRec = shopVersionList.getSelectionModel().getSelection()[0],
            shopVersionId = shopVersionRec.get("id");
        if (baseForm.isValid()) {
            formEl.mask('正在保存 . . .');
            var params = baseForm.getFieldValues();
            params.shopVersionId = shopVersionId;
            me.commonUtils.ajax({
                url:'userGroup.do?method=saveUserGroupForShopVersion',
                params:params,
                success:function (result) {
                    me.getUserGroupList().store.load();
                    Ext.Msg.alert('返回结果', result.message);
                    baseForm.reset();
                    formEl.unmask();
                },
                failure:function (response) {
                    formEl.unmask();
                }
            });
        }
    },
    deleteUserGroup:function () {
        var me = this,
            shopVersionList = me.getShopVersionList(),
            userGroupList = me.getUserGroupList(),
            shopVersionRec = shopVersionList.getSelectionModel().getSelection()[0],
            userGroupRec = userGroupList.getSelectionModel().getSelection()[0],
            shopVersionId = shopVersionRec.get("id"),
            userGroupId = userGroupRec.get("id");
        Ext.MessageBox.confirm('确认', '确认删除该用户组?', function (btn) {
            if (btn == "yes") {
                me.commonUtils.ajax({
                    url:'userGroup.do?method=deleteUserGroup',
                    params:{
                        userGroupId:userGroupId,
                        shopVersionId:shopVersionId
                    },
                    success:function (result) {
                        userGroupList.store.load();
                    }
                });
            }
        });
    },
    addShopVersion:function () {
        var me = this,
            form = me.getAddShopVersionWindow().down("form"),
            formEl = form.getEl(),
            baseForm = form.form;
        if (baseForm.isValid()) {
            formEl.mask('正在保存 . . .');
            me.commonUtils.ajax({
                url:'shopVersion.do?method=saveOrUpdateShopVersion',
                params:baseForm.getFieldValues(),
                success:function (result) {
                    me.getShopVersionList().store.load();
                    Ext.Msg.alert('返回结果', result.message);
                    baseForm.reset();
                    formEl.unmask();
                },
                failure:function (response) {
                    formEl.unmask();
                }
            });
        }
    },
//    addResource:function () {
//        var me = this,
//            form = me.getAddResourceForm(),
//            formEl = form.getEl(),
//            baseForm = form.form;
//        if (baseForm.isValid()) {
//            console.log(baseForm.getFieldValues());

//            formEl.mask('正在保存 . . .');
//            me.commonUtils.ajax({
//                url:'resource.do?method=saveOrUpdateResource',
//                params:baseForm.getFieldValues(),
//                success:function (result) {
//                    me.getResourceList().store.loadPage(1);
//                    Ext.Msg.alert('返回结果', result.message);
//                    baseForm.reset();
//                    formEl.unmask();
//                },
//                failure:function (response) {
//                    formEl.unmask();
//                }
//            });
//        }
//    },
//    updateResource:function () {
//        var me = this,
//            form = me.getUpdateResourceForm(),
//            formEl = form.getEl(),
//            thisWin = me.getUpdateResourceWindow(),
//            baseForm = form.form;
//        if (baseForm.isValid()) {
//            formEl.mask('正在保存 . . .');
//            me.commonUtils.ajax({
//                url:'resource.do?method=saveOrUpdateResource',
//                params:baseForm.getFieldValues(),
//                success:function (result) {
//                    Ext.Msg.alert('返回结果', result.message, function () {
//                        thisWin.close();
//                        me.getResourceList().store.load();
//                        formEl.unmask();
//                    });
//                },
//                failure:function (response) {
//                    formEl.unmask();
//                }
//            });
//        }
//    },
    editResource:function () {
        var me = this,
            rec = me.getResourceList().getSelectionModel().getSelection()[0],
            win = me.getUpdateResourceWindow(),
            form = win.down("form");
        me.commonUtils.mask();
        form.loadRecord(rec);
//        if (form.down("[name=type]").getValue() === "menu") {
//            form.down("[name=label]").show();
//            form.down("[name=href]").show();
//            form.down("[name=grade]").show();
//            if (form.down("[name=grade]").getValue() != "1") {
//                form.down("[name=parentId]").show();
//            } else {
//                form.down("[name=parentId]").hide();
//            }
//        }
        me.commonUtils.unmask();
        win.show();
    },
    showResourceDetails:function () {
        var me = this,
            win = me.getResourceRoleWindow() ,
            resourceRec = me.getResourceList().getSelectionModel().getSelection()[0],
            resourceId = resourceRec.get("resourceId");
        me.getResourceRoleList().store.proxy.extraParams = {
            resourceId:resourceId
        };
        me.getResourceRoleList().store.load();
        win.show();
    },
    //search action
    onSearchResources:function (view) {
        var me = this,
            resourceList = me.getResourceList();
        resourceList.store.proxy.extraParams = {
            value:resourceList.down('#searchResourceValue').getValue(),
            roleName:resourceList.down('#searchRoleName').getValue(),
            name:resourceList.down('#searchResourceName').getValue(),
            memo:resourceList.down('#searchResourceMemo').getValue(),
            systemType:resourceList.down('#searchSystemType').getValue(),
            type:resourceList.down('#searchResourceType').getValue()
        };
        resourceList.store.loadPage(1);
    },
    deleteRoleResource:function (roleId, resourceId, callback) {
        var me = this;
        Ext.MessageBox.confirm('确认', '确认删除该角色中的资源?', function (btn) {
            if (btn == "yes") {
                me.commonUtils.ajax({
                    url:'role.do?method=deleteRoleResource',
                    params:{
                        roleId:roleId,
                        resourceId:resourceId
                    },
                    success:function (result) {
                        callback();
                    }
                });
            }
        });
    },

    refreshPermission:function (roleId, resourceId, callback) {
        this.commonUtils.ajax({
            url:'userGroup.do?method=refreshPermission',
            params:{
                roleId:roleId,
                resourceId:resourceId
            },
            success:function (result) {
                Ext.Msg.alert('警告',"操作成功！");
            }
        });
    },
    updateModuleOrRole:function (editor, e, eOpts) {
        var me = this, url,
            rec = e.record, message,
            listTree = me.getModuleTree();
        if (rec.get("type") == 'MODULE') {
            url = 'module.do?method=updateModule';
            message = "模块重复！";
        } else {
            url = 'role.do?method=updateRole';
            message = "角色重复！";
        }
        console.log(rec.get("systemType"));
        me.commonUtils.ajax({
            url:url,
            params:{
                id:rec.get("id"),
                parentId:rec.get("parentId"),
                systemType:rec.get("systemType"),
                value:rec.get("value"),
                name:rec.get("name"),
                sort:rec.get("sort")
            },
            success:function (result) {
                if (result.duplicate) {
                    var cellEditingPlugin = listTree.cellEditingPlugin;
                    Ext.Msg.alert('警告', message, function () {
                        rec.set("value", rec.get("value") + "-新");
                        cellEditingPlugin.startEdit(rec, 0);
                    });
                } else {
                    rec.set("id", result.node.id);
                    rec.set("name", result.node.name);
                    rec.set("systemType", result.node.type);
                    rec.set("sort", result.node.sort);
                    rec.commit();
                    listTree.store.sort('sort', 'ASC');
                }
            }
        });
    },
    addModule:function (component, e) {
        var newModule = Ext.create('Ext.model.dataMaintenance.Module', {
            value:'新模块',
            leaf:false,
            type:"MODULE",
            parentId:component.ownerCt.getModule().get("id"),
            systemType:component.ownerCt.getModule().get("systemType"),
            loaded:true // set loaded to true, so the tree won't try to dynamically load children for this node when expanded
        });
        var listTree = this.getModuleTree(),
            cellEditingPlugin = listTree.cellEditingPlugin,
            selectionModel = listTree.getSelectionModel(),
            selectedList = selectionModel.getSelection()[0],
            parentList = selectedList.isLeaf() ? selectedList.parentNode : selectedList,
            expandAndEdit = function () {
                if (parentList.isExpanded()) {
                    selectionModel.select(newModule);
                    cellEditingPlugin.startEdit(newModule, 0);
                } else {
                    listTree.on('afteritemexpand', function startEdit(list) {
                        if (list === parentList) {
                            selectionModel.select(newModule);
                            cellEditingPlugin.startEdit(newModule, 0);
                            // remove the afterexpand event listener
                            listTree.un('afteritemexpand', startEdit);
                        }
                    });
                    parentList.expand();
                }
            };

        parentList.appendChild(newModule);
        if (listTree.getView().isVisible(true)) {
            expandAndEdit();
        } else {
            listTree.on('expand', function onExpand() {
                expandAndEdit();
                listTree.un('expand', onExpand);
            });
            listTree.expand();
        }
    },
    addRole:function (component, e) {
        var newRole = Ext.create('Ext.model.dataMaintenance.Module', {
            value:'新角色',
            leaf:true,
            type:"ROLE",
            parentId:component.ownerCt.getModule().get("id"),
            systemType:component.ownerCt.getModule().get("systemType"),
            loaded:true // set loaded to true, so the tree won't try to dynamically load children for this node when expanded
        });
        var listTree = this.getModuleTree(),
            cellEditingPlugin = listTree.cellEditingPlugin,
            selectionModel = listTree.getSelectionModel(),
            selectedList = selectionModel.getSelection()[0],
            parentList = selectedList.isLeaf() ? selectedList.parentNode : selectedList,
            expandAndEdit = function () {
                if (parentList.isExpanded()) {
                    selectionModel.select(newRole);
                    cellEditingPlugin.startEdit(newRole, 0);
                } else {
                    listTree.on('afteritemexpand', function startEdit(list) {
                        if (list === parentList) {
                            selectionModel.select(newRole);
                            cellEditingPlugin.startEdit(newRole, 0);
                            // remove the afterexpand event listener
                            listTree.un('afteritemexpand', startEdit);
                        }
                    });
                    parentList.expand();
                }
            };

        parentList.appendChild(newRole);
        if (listTree.getView().isVisible(true)) {
            expandAndEdit();
        } else {
            listTree.on('expand', function onExpand() {
                expandAndEdit();
                listTree.un('expand', onExpand);
            });
            listTree.expand();
        }
    },
    editModule:function (component, e) {
        var listTree = this.getModuleTree(),
            cellEditingPlugin = listTree.cellEditingPlugin;
        cellEditingPlugin.startEdit(component.ownerCt.getModule(), 0);
    },
    editRole:function (component, e) {
        var listTree = this.getModuleTree(),
            cellEditingPlugin = listTree.cellEditingPlugin;
        cellEditingPlugin.startEdit(component.ownerCt.getModule(), 0);
    },
    deleteModule:function () {
        var me = this,
            listTree = this.getModuleTree(),
            selectionModel = listTree.getSelectionModel().getSelection()[0],
            moduleId = selectionModel.get("id");
        if (selectionModel.hasChildNodes()) {
            Ext.Msg.alert('警告', "请先删除子节点！");
        } else {
            me.deleteModuleImplementor(moduleId);
        }
    },
    deleteRole:function () {
        var me = this,
            listTree = me.getModuleTree(),
            selectionModel = listTree.getSelectionModel().getSelection()[0],
            roleId = selectionModel.get("id");
        me.commonUtils.ajax({
            url:'role.do?method=checkRoleBeforeDelete',
            params:{
                roleId:roleId
            },
            success:function (result) {
                if (result.hasBeUsed) {
                    Ext.Msg.alert('警告', "该角色有资源!");
                } else {
                    me.deleteRolesImplementor(roleId);
                }
            }
        });
    },

    pasteRole:function () {
        var me = this,
            listTree = me.getModuleTree(),
            selectionModel = listTree.getSelectionModel().getSelection()[0],
            contextMenu = this.getModuleTreeMenu();
        if (selectionModel && contextMenu.getCopyRole()) {
            me.commonUtils.ajax({
                url:'resource.do?method=copyRoleResource',
                params:{
                    desRoleId:selectionModel.get("id"),
                    origRoleId:contextMenu.getCopyRole().get("id")
                },
                success:function (result) {
                    contextMenu.setCopyRole(null);
                }
            });
        }
    },

    copyRole:function () {
        var me = this,
            listTree = me.getModuleTree(),
            selectionModel = listTree.getSelectionModel().getSelection()[0],
            contextMenu = this.getModuleTreeMenu();
        contextMenu.setCopyRole(selectionModel);
    },
    deleteResourceImplementor:function (resourceId) {
        var me = this,
            resourceList = me.getResourceList();
        Ext.MessageBox.confirm('确认', '确认删除资源?', function (btn) {
            if (btn == "yes") {
                me.commonUtils.ajax({
                    url:'resource.do?method=deleteResource',
                    params:{resourceId:resourceId},
                    success:function (result) {
                        resourceList.store.loadPage(1);
                    }
                });
            }
        });
    },
    deleteModuleImplementor:function (moduleId) {
        var me = this;
        Ext.MessageBox.confirm('确认', '确认删除模块?', function (btn) {
            if (btn == "yes") {
                me.commonUtils.ajax({
                    url:'module.do?method=deleteModule',
                    params:{moduleId:moduleId},
                    success:function (result) {
                        me.getModuleTree().getStore().getNodeById(moduleId).remove();
                    }
                });
            }
        });
    },
    deleteRolesImplementor:function (roleId) {
        var me = this;
        Ext.MessageBox.confirm('确认', '确认删除角色?', function (btn) {
            if (btn == "yes") {
                me.commonUtils.ajax({
                    url:'role.do?method=deleteRole',
                    params:{roleId:roleId},
                    success:function (result) {
                        me.getModuleTree().getStore().getNodeById(roleId).remove();
                    }
                });
            }
        });
    },

    showRoleDetails:function (id) {
        var me = this;
        if(!id)
        {
            return;
        }
        me.getRoleResources().store.proxy.extraParams = {
            roleId:id
        };
        me.getRoleResources().store.load();
    },

    treeMenu:function (view, record, item, rowIndex, e) {
        var me = this,
            selectionModel = me.getModuleTree().getSelectionModel().getSelection()[0],
            contextMenu = me.getModuleTreeMenu(),
            addModule = contextMenu.down("#addModule"),
            addRole = contextMenu.down("#addRole"),
            editModule = contextMenu.down("#editModule"),
            editRole = contextMenu.down("#editRole"),
            deleteRole = contextMenu.down("#deleteRole"),
            deleteModule = contextMenu.down("#deleteModule"),
            pasteRole = contextMenu.down("#pasteRole"),
            copyRole = contextMenu.down("#copyRole");
        //如果是根节点 隐藏 添加 部门
        if (record.get("value").indexOf("统购平台") != -1) {
            addModule.show();
            addRole.show();
            editModule.hide();
            editRole.hide();
            deleteRole.hide();
            deleteModule.hide();
            pasteRole.hide();
            copyRole.hide();
        } else if (record.get('type') == "MODULE") {
            addModule.show();
            addRole.show();
            editModule.show();
            editRole.hide();
            deleteRole.hide();
            deleteModule.show();
            copyRole.hide();
            pasteRole.hide();
        } else if (record.get('type') == "ROLE") {
            addModule.hide();
            addRole.hide();
            editModule.hide();
            editRole.show();
            deleteModule.hide();
            deleteRole.show();
            copyRole.show();
            if (contextMenu.getCopyRole() && selectionModel.get("id") != contextMenu.getCopyRole().get("id")) {
                pasteRole.show();
            } else {
                pasteRole.hide();
            }
        }
        if (!record.get('id')) {
            addModule.hide();
            addRole.hide();
        }
        contextMenu.setModule(record);
        contextMenu.showAt(e.getX(), e.getY());
        e.preventDefault();
    },
    checkChangeForTreeRoles:function (node, checked, eOpts) {
        function checkChildren(currNode) {
            if (currNode.hasChildNodes) {
                currNode.eachChild(function (n) {
                    n.data.checked = checked;
                    n.updateInfo({checked:checked});
                    checkChildren(n);
                });
            }
        }

        function checkParent(currNode, currChecked) {
            if (currNode.parentNode && currNode.parentNode.get("name") != "BCGOGO") {
                if (!currChecked) {
                    currNode.parentNode.data.checked = false;
                    currNode.parentNode.updateInfo({checked:false});
                    checkParent(currNode.parentNode, false);
                } else {
                    if (traverse(currNode.parentNode)) {
                        currNode.parentNode.data.checked = true;
                        currNode.parentNode.updateInfo({checked:true});
                        checkParent(currNode.parentNode, true);
                    } else {
                        currNode.parentNode.data.checked = false;
                        currNode.parentNode.updateInfo({checked:false});
                        checkParent(currNode.parentNode, false);
                    }
                }
            }
        }

        function traverse(currNode) {
            if (currNode.hasChildNodes && currNode.get("type") == "MODULE") {
                for (var i = 0, max = currNode.childNodes.length; i < max; i++) {
                    if (!traverse(currNode.childNodes[i]))return false;
                }
            } else {
                if (!currNode.data.checked) return false;
            }
            return true;
        }

        checkChildren(node);
        checkParent(node, checked);
    }
});