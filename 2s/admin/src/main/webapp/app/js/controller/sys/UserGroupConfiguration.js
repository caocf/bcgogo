Ext.define('Ext.controller.sys.UserGroupConfiguration', {
    extend:'Ext.app.Controller',

    stores:["Ext.store.sys.UserGroups"],

    models:["Ext.model.sys.UserGroup"],

    views:['Ext.view.sys.userGroup.List', 'Ext.view.sys.module.View'],

    requires:[
        "Ext.view.sys.userGroup.Add",
        "Ext.view.sys.userGroup.Update"
    ],

    refs:[
        {ref:'sysStatus', selector:'sysstatus'},
        //相当于一个映射,这样就可以在控制层方便的通过geter取得相应的对象了
        {ref:'userGroupList', selector:'sysUserGroupList'} ,
        //启用button
        {ref:'enableRoleButton', selector:'sysUserGroupList #enableRoleButton'},
        //禁用button
        {ref:'forbiddenRoleButton', selector:'sysUserGroupList #forbiddenRoleButton'},
        //role 表单
        {ref:'formAddUserGroup', selector:'windowAddUserGroup formUserGroup'},
        //资源复选框
//        {ref:'resourceCheckBoxes', selector:'sysrmoduleview roleCheckboxGroup'},
        {ref:'roleCheckBoxes', selector:'sysrmoduleview sysRoleList'},
        //模块
        {ref:'moduleView', selector:'sysrmoduleview modulelist'},
        //role window
        {
            ref:'windowAddUserGroup',
            selector:'windowAddUserGroup',
            autoCreate:true,
            xtype:'windowAddUserGroup'
        },
        //update role window
        {
            ref:'windowUpdateUserGroup',
            selector:'windowUpdateUserGroup',
            autoCreate:true,
            xtype:'windowUpdateUserGroup'
        },
        //config role resources
        {
            ref:'sysrmoduleview',
            selector:'sysrmoduleview',
            autoCreate:true,
            xtype:'sysrmoduleview'
        }
    ],

    init:function () {
        var me = this;
        this.componentUtils = Ext.create("Ext.utils.ComponentUtils");
        this.permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        this.commonUtils = Ext.create("Ext.utils.Common");
        this.control({
            //role 列表
            'sysUserGroupList':{
                afterrender:function () {
                    me.getUserGroupList().store.proxy.extraParams = {}; //防止 共用store的层 带入参数
                    me.getUserGroupList().store.loadPage(1);
                },
                selectionchange:function (view, records) {
                    var enable = !records.length;
                    me.getEnableRoleButton().setDisabled(enable);
                    me.getForbiddenRoleButton().setDisabled(enable);
                }
            },
            'sysUserGroupList #searchRoleComponent':{
                beforerender:function (view, eOpts) {
                    if (!me.permissionUtils.hasPermission("CRM_sys_role_search")) {
                        view.hide();
                    }
                }
            },
            // 搜素按钮
            'sysUserGroupList button[action=search]':{
                beforerender:function () {
                    return me.permissionUtils.hasPermission("CRM_sys_role_search");
                },
                click:me.onSearchRoles
            },
            //清除条件按钮
            'sysUserGroupList button[action=clearSearchCondition]':{
                click:me.clearSearchCondition
            },
            //启用按钮
            'sysUserGroupList button[action=enableRoles]':{
                beforerender:function (view, eOpts) {
                    return me.permissionUtils.hasPermission("CRM_sys_role_enable");
                },
                click:me.enableSomeUserGroups
            },
            //禁用按钮
            'sysUserGroupList button[action=forbiddenRoles]':{
                beforerender:function (view, eOpts) {
                    return me.permissionUtils.hasPermission("CRM_sys_role_disable");
                },
                click:me.forbiddenSomeUserGroups
            },
            //show add role window
            'sysUserGroupList button[action=addNewRole]':{
                beforerender:function (view, eOpts) {
                    return me.permissionUtils.hasPermission("CRM_sys_role_add");
                },
                click:function () {
                    Ext.create("Ext.utils.Common").mask();
                    me.getWindowAddUserGroup().show();
                }
            },
            //角色修改 配置
            'sysUserGroupList actioncolumn':{
                beforerender:function (view, eOpts) {
                    if (!me.permissionUtils.hasPermission("CRM_sys_role_update")) {
                        view.hide();
                    }
                },
                click:function (grid, cell, row, col, e) {
                    var rec = grid.getStore().getAt(row);
                    var index = me.componentUtils.getActionColumnItemsIndex(e);
                    if (index === Ext.utils.ComponentUtils.ACTION_COLUMN_FIRST) {
                        me.editRole(grid, row, col);
                    } else if (index === Ext.utils.ComponentUtils.ACTION_COLUMN_SECOND) {
                        me.configurationRole(grid, row, col);
                    }
                }
            },
            //role form
            'windowAddUserGroup formUserGroup':{
                create:me.addRoleToMemory
            },
            //表单中的save按钮
            'windowAddUserGroup formUserGroup button[action=save]':{
                click:me.addUserGroup
            },
            // 表单中的update按钮
            'windowUpdateUserGroup formUserGroup button[action=save]':{
                click:me.updateUserGroup
            },
            //角色配置-模块
            'sysrmoduleview modulelist':{
                viewready:function () {
                    me.getRoleCheckBoxes().fireEvent('storeloaded', me.getRoleCheckBoxes());
                },
                afterrender:function (grid, opts) {
                    me.getModuleView().store.load();
                },
                selectionchange:function (view, records) {
                    me.showResources(records[0].data.id)
                }
            },
            //角色配置-资源集
            'sysrmoduleview sysRoleList':{
                //当资源集渲染完毕 默认选择第一行
                storeloaded:function (view) {
                    var selectModels = me.getModuleView().getSelectionModel();
                    if (!selectModels.hasSelection())selectModels.select(0);
                }
            },
            //角色配置-资源集-更新配置
            'sysrmoduleview button[action=updateResourceConfig]':{
                click:me.updateResourceConfig
            }
        });
    },
    //更新配置
    updateResourceConfig:function () {
        var me = this,
            userGroupIds = "",
            roleIds = "",
            checks = "",
            roleCheckBoxesView = me.getRoleCheckBoxes();
        var records = roleCheckBoxesView.store.getUpdatedRecords();
        for (var i = 0, max = records.length; i < max; i++) {
            userGroupIds += records[i].data.userGroupId + ",";
            roleIds += records[i].data.id + ",";
            checks += records[i].data.hasCheckedByUserGroup + ",";
        }
        roleCheckBoxesView.store.proxy.extraParams = {
            userGroupIds:userGroupIds,
            checks:checks,
            roleIds:roleIds
        };
        roleCheckBoxesView.store.sync();
    },
    //根据 模块 显示资源
    showResources:function (moduleId) {
        var me = this;
        var resourceStore = me.getRoleCheckBoxes().store;
        resourceStore.clearFilter();
        var filters = new Ext.util.Filter({
            filterFn:function (item) {
                return item.data.moduleId === moduleId;
            }
        });
        resourceStore.filter(filters);
    },
    //修改角色
    editRole:function (grid, rowIndex, colIndex) {
        var me = this;
        var rec = grid.getStore().getAt(rowIndex);
//        me.fireEvent('editRole', me, rec);
        var win = me.getWindowUpdateUserGroup();
        win.down("form").loadRecord(rec);
        Ext.create("Ext.utils.Common").mask();
        win.show();

    },
    //配置角色
    configurationRole:function (grid, rowIndex, colIndex) {
        var me = this;
        var rec = grid.getStore().getAt(rowIndex);
        var win = me.getSysrmoduleview();
        me.getRoleCheckBoxes().store.proxy.extraParams = {
            userGroupId:rec.data.id
        };
        me.getRoleCheckBoxes().store.load();
        Ext.create("Ext.utils.Common").mask();
        win.show();
    },
    addRoleToMemory:function (data) {
        if (data.status) {
            if (data.status == 'inActive') {
                data.statusValue = "禁用";
            } else if (data.status == 'active') {
                data.statusValue = "启用";
            }
        }
        this.getUserGroupList().store.insert(0, data);
//        this.getUserGroupList().view.refresh(); //仅限增加的排序
//        this.getUserGroupList().store.load();
    },
    //更新角色
    updateUserGroup:function (view, keyCode) {  //todo ext:应该使用自动同步化
        var me = this;
        var form = view.ownerCt.ownerCt.form;
        var thisWin = view.ownerCt.ownerCt.ownerCt;
        if (form.isValid()) {
            form.submit({
                url:'userGroup.do?method=updateUserGroup',
                success:function (formData, action) {
                    var result = Ext.JSON.decode(action.response.responseText);
                    Ext.Msg.alert('返回结果', result.message, function () {
                        form.updateRecord(form.getRecord());
                        thisWin.close();
                        me.getUserGroupList().store.commitChanges();
                    });
                },
                failure:function (formData, action) {
                    me.commonUtils.formFailAction(action, action.result.msg);
                }
            });
        }
    },
    //增加角色
    addUserGroup:function () {
        var me = this;
        var formUserGroup = me.getFormAddUserGroup().form;
        if (formUserGroup.isValid()) {
            formUserGroup.submit({
                url:'userGroup.do?method=updateUserGroup',
                success:function (formData, action) {
//                    me.getFormRole().fireEvent('create', form.getValues());
                    me.getUserGroupList().store.loadPage(1);
                    var result = Ext.JSON.decode(action.response.responseText);
                    Ext.Msg.alert('返回结果', result.message);
                    formUserGroup.reset();
                },
                failure:function (formData, action) {
                    me.commonUtils.formFailAction(action, action.result.msg);
                }
            });
        }
    },
    //禁用角色
    forbiddenSomeUserGroups:function () {
        var me = this;
        me.commonUtils.ajax({
            url:'userGroup.do?method=updateUserGroupsStatusByIds',
            params:{ids:me.getSelectionIds(), status:'inActive'},
            success:function (result) {
                me.updateStatusMemory("禁用");
            }
        });
    },
    //启用角色
    enableSomeUserGroups:function () {
        var me = this;
        me.commonUtils.ajax({
            url:'userGroup.do?method=updateUserGroupsStatusByIds',
            params:{ids:me.getSelectionIds(), status:'active'},
            success:function (response) {
                me.updateStatusMemory("启用");
            }
        });
    },
    //search action
    onSearchRoles:function () {
        var me = this.getUserGroupList();
        me.store.proxy.extraParams = {
            name:Ext.getCmp('roleNameForSearch').getValue(),
            memo:Ext.getCmp('roleDescriptionForSearch').getValue(),
            status:Ext.getCmp('sysRoleStatusForSearch').getValue()
        };
        me.store.loadPage(1);
    },
    clearSearchCondition:function () {
        Ext.getCmp('roleNameForSearch').setValue("");
        Ext.getCmp('roleDescriptionForSearch').setValue("");
        Ext.getCmp('sysRoleStatusForSearch').setValue("");
    },
    //update role status in memory
    updateStatusMemory:function (status) {
        var me = this.getUserGroupList();
        me.store.load();
//        var selects = me.getSelectionModel().getSelection();
//        if (selects) {
//            for (var i = 0, max = selects.length; i < max; i++) {
//                selects[i].data.statusValue = status;
//            }
//        }
//        me.store.loadData(me.store.data.items, false);
    },
    //get selected role ids
    getSelectionIds:function () {
        return Ext.create("Ext.utils.Common").getSelectionIds(this.getUserGroupList())
    }
});