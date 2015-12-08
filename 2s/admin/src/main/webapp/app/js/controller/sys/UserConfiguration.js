Ext.define('Ext.controller.sys.UserConfiguration', {
    extend:'Ext.app.Controller',

    stores:[
        "Ext.store.sys.Users",
        'Ext.store.sys.Departments',
        'Ext.store.sys.DepartmentDetails'
    ],

    models:[
        "Ext.model.sys.User",
        "Ext.model.sys.Department",
        "Ext.model.sys.DepartmentDetail"
    ],

    views:[
        'Ext.view.sys.user.View',
        'Ext.view.sys.department.Tree'
    ],

    requires:[
        "Ext.view.sys.user.Add",
        "Ext.view.sys.user.Update"
    ],
    refs:[
        {ref:'sysStatus', selector:'sysstatus'},
        //view->user别表
        {ref:'userList', selector:'sysuserview sysuserlist'},
        {ref:'departmentLeader', selector:'sysuserview departmentLeaders'} ,
        {ref:'departmentMember', selector:'sysuserview departmentMembers'} ,
        {ref:'departmentTree', selector:'sysuserview departmentTree'},
        //add user windows
        {ref:'addUserForm', selector:'windowadduser formuser'},
        {ref:'updateUserForm', selector:'windowupdateuser formuser'},
        {ref:'departmentList', selector:'sysuserview sysdepartmentlist'} ,
        //增加用户 角色下拉框
        {ref:'roleAddSelect', selector:'windowadduser formuser userGroupSelect'} ,
        //修改用户 角色下拉框
        {ref:'userGroupUpdateSelect', selector:'windowupdateuser formuser userGroupSelect'} ,
        //树菜单
        {
            ref:'departmentTreeMenu',
            selector:'sysDepartmentTreeMenu',
            xtype:'sysDepartmentTreeMenu',
            autoCreate:true
        },
        //add role window
        {
            ref:'windowAddUser',
            selector:'windowadduser',
            autoCreate:true,
            xtype:'windowadduser'
        },
        //update role window
        {
            ref:'windowUpdateUser',
            selector:'windowupdateuser',
            autoCreate:true,
            xtype:'windowupdateuser'
        }
    ],
    init:function () {
        var me = this;
        this.componentUtils = Ext.create("Ext.utils.ComponentUtils");
        this.permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        this.commonUtils = Ext.create("Ext.utils.Common");
        this.control({
            'sysuserview':{
                beforerender:function (view, eOpts) {
                    if (!me.permissionUtils.hasPermission("CRM_sys_department")) {
                        Ext.getCmp("departmentView").hide();
                        Ext.getCmp("departmentView").setDisabled(true);
                    }
                }
            },
            'sysuserview sysuserlist':{
                afterrender:function () {
                    me.getUserList().store.proxy.extraParams = {};
                    me.getUserList().store.loadPage(1);
                },
                selectionchange:function (view, records) {
                    var enable = !records.length;
                    Ext.getCmp('delUsersButton').setDisabled(enable);
                    Ext.getCmp('enableUsersButton').setDisabled(enable);
                    Ext.getCmp('forbiddenUsersButton').setDisabled(enable);
                }
            },
            //用户修改 删除
            'sysuserview sysuserlist actioncolumn':{
                beforerender:function (view, eOpts) {
                    if (!me.permissionUtils.hasPermission("CRM_sys_user_update")) {
                        view.hide();
                    }
                },
                click:function (grid, cell, row, col, e) {
                    var rec = grid.getStore().getAt(row);
                    var index = me.componentUtils.getActionColumnItemsIndex(e);
                    if (index === Ext.utils.ComponentUtils.ACTION_COLUMN_FIRST) {
                        me.editUser(grid, row, col);
                    } else if (index === Ext.utils.ComponentUtils.ACTION_COLUMN_SECOND) {
                        me.deleteUser(grid, row, col);
                    }
                }
            },
            'sysuserview sysuserlist departmentTreePicker':{
                select:function (picker, record, eOpts) {
                    if (record.get('type') === "OCCUPATION") {
                        picker.setOccupation(record);
                        picker.setDepartment(record.parentNode);
                    } else {
                        picker.setOccupation(null);
                        picker.setDepartment(record);
                    }
                }
            },
            //show add user window
            'sysuserview sysuserlist button[action=addUser]':{
                beforerender:function (view, eOpts) {
                    return me.permissionUtils.hasPermission("CRM_sys_user_add");
                },
                click:function () {
                    me.commonUtils.mask();
                    me.getWindowAddUser().show();
                }
            },
            'sysuserview #departmentView':{
                beforerender:function (view, eOpts) {
                    return me.permissionUtils.hasPermission("CRM_sys_department");
                }
            },
            'sysuserview departmentTree':{
                afterrender:function (grid, opts) {
//                    me.getDepartmentTree().getStore().load();
                },
                selectionchange:function (view, records) {
//                    me.showResources(records[0].data.id)
                }
            },
            'sysuserview #searchUserComponent':{
                beforerender:function (view, eOpts) {
                    if (!me.permissionUtils.hasPermission("CRM_sys_user_search")) {
                        view.hide();
                    }
                }
            },
            //查询
            'sysuserview sysuserlist button[action=search]':{
                beforerender:function (view, eOpts) {
                    if (!me.permissionUtils.hasPermission("CRM_sys_user_search")) {
                        view.hide();
                    }
                },
                click:me.onSearchUsers
            },
            //批量删除
            'sysuserview sysuserlist button[action=delUsers]':{
                beforerender:function (view, eOpts) {
                    if (!me.permissionUtils.hasPermission("CRM_sys_user_delete")) {
                        view.hide();
                    }
                },
                click:me.deleteUsers
            },
            //批量开启
            'sysuserview sysuserlist button[action=enableUsers]':{
                beforerender:function (view, eOpts) {
                    if (!me.permissionUtils.hasPermission("CRM_sys_user_enable")) {
                        view.hide();
                    }
                },
                click:me.enableSomeUsers
            },
            //批量禁用
            'sysuserview sysuserlist button[action=forbiddenUsers]':{
                beforerender:function (view, eOpts) {
                    if (!me.permissionUtils.hasPermission("CRM_sys_user_disable")) {
                        view.hide();
                    }
                },
                click:me.forbiddenSomeUsers
            },
            "windowadduser formuser":{
                beforerender:function () {
                    me.getWindowAddUser().down("form").down('#resetUserPassword').hide();
                }
            },
            "windowupdateuser formuser":{
                beforerender:function () {
                    me.getWindowUpdateUser().down("form").down("[name='userNo']").hide().setReadOnly(true);
                }
            },
            'formuser button[action=resetPassword]':{
                click:me.resetUserPassword
            },
            //form 下的role 下拉框
            'formuser userGroupSelect':{
                select:function (combo, records, eOpts) {
//                    combo.up("form").form.getRecord().data.userGroupName = records[0].data.name;
                }
            },
            //表单中的save按钮
            'windowupdateuser formuser button[action=save]':{
                beforerender:function (view, eOpts) {
                    if (!me.permissionUtils.hasPermission("CRM_sys_user_update")) {
                        view.hide();
                    }
                },
                click:me.updateUser
            },
            //form tree 下拉建议
            'formuser treepicker':{
                select:function (view, records, eOpts) {
                    if (records.data.type == "DEPARTMENT" || records.data.id === -1) {
                        Ext.Msg.alert('警告', "请选择职位！");
                        view.setRawValue("");
                    } else {
                        view.up("form").down("#departmentId").setValue(records.data.parentId);
                    }
                }
            },
            //表单中的save按钮
            'windowadduser formuser button[action=save]':{
                beforerender:function (view, eOpts) {
                    return me.permissionUtils.hasPermission("CRM_sys_user_add");
                },
                click:me.addUser
            },
            'formuser #userNo':{   //user form 用户名 验证
                blur:me.checkUserNo
            },
            "sysuserview departmentLeaders actioncolumn":{
                click:function (grid, cell, row, col, e) {
                    var rec = grid.getStore().getAt(row);
                    me.updateUserDepartmentResponsibility(rec.get("id"), "MEMBER");
                }
            },
            "sysuserview departmentMembers actioncolumn":{
                click:function (grid, cell, row, col, e) {
                    var rec = grid.getStore().getAt(row);
                    me.updateUserDepartmentResponsibility(rec.get("id"), "LEADER");
                }
            },
            //部门 tree
            "departmentTree":{
                itemcontextmenu:me.treeMenu,
                edit:me.updateDepartmentOrOccupation,
                itemclick:function (view, record, item, rowIndex, e) {
                    var contextMenu = this.getDepartmentTreeMenu();
                    if (record.get('type') == "DEPARTMENT" && record.data.id != -1) {
                        contextMenu.setDepartment(record);
                        me.showDepartmentDetails(record.get("id"))
                    }
                }
            },
            //department tree menu
            "[id=addDepartment]":{
                click:me.addDepartment
            },
            "[id=addOccupation]":{
                click:me.addOccupation
            },
            "[id=editDepartment]":{
                click:me.editDepartment
            },
            "[id=editOccupation]":{
                click:me.editOccupation
            },
            //删除部门
            "[id=deleteDepartment]":{
                click:me.deleteDepartment
            },
            //删除职位
            "[id=deleteOccupation]":{
                click:me.deleteOccupation
            }
        });
    },
    checkUserNo:function (view, rec) {
        var me = this;
        me.commonUtils.ajax({
            url:"user.do?method=checkUserNo",
            params:{
                userNo:view.getValue()
            },
            method:'post',
            success:function (isDuplicate) {
                if (isDuplicate) {
                    view.duplicating = "用户名已存在！";
                } else {
                    view.duplicating = false;
                }
                view.validate();
            }
        });
    },
    treeMenu:function (view, record, item, rowIndex, e) {
        var contextMenu = this.getDepartmentTreeMenu(),
            addDepartment = contextMenu.down("#addDepartment"),
            addOccupation = contextMenu.down("#addOccupation"),
            editDepartment = contextMenu.down("#editDepartment"),
            editOccupation = contextMenu.down("#editOccupation"),
            deleteOccupation = contextMenu.down("#deleteOccupation"),
            deleteDepartment = contextMenu.down("#deleteDepartment");
        //如果是根节点 隐藏 添加 部门
        if (record.get("text") === "统购平台") {
            addDepartment.show();
            addOccupation.hide();
            editDepartment.hide();
            editOccupation.hide();
            deleteOccupation.hide();
            deleteDepartment.hide();
        } else if (record.get('type') === "DEPARTMENT") {
            addDepartment.show();
            addOccupation.show();
            editDepartment.show();
            editOccupation.hide();
            deleteOccupation.hide();
            deleteDepartment.show();
        } else if (record.get('type') === "OCCUPATION") {
            addDepartment.hide();
            addOccupation.hide();
            editDepartment.hide();
            editOccupation.show();
            deleteDepartment.hide();
            deleteOccupation.show();
        }
        contextMenu.setDepartment(record);
        contextMenu.showAt(e.getX(), e.getY());
        e.preventDefault();
    },
    addOccupation:function (component, e) {
        var newDepartment = Ext.create('Ext.model.sys.Department', {
            text:'新职位',
            leaf:true,
            type:"OCCUPATION",
            iconCls:'icon-user',
            parentId:component.ownerCt.getDepartment().getId("id"),
            loaded:true // set loaded to true, so the tree won't try to dynamically load children for this node when expanded
        });
        var listTree = this.getDepartmentTree(),
            cellEditingPlugin = listTree.cellEditingPlugin,
            selectionModel = listTree.getSelectionModel(),
            selectedList = selectionModel.getSelection()[0],
            parentList = selectedList.isLeaf() ? selectedList.parentNode : selectedList,
            expandAndEdit = function () {
                if (parentList.isExpanded()) {
                    selectionModel.select(newDepartment);
                    cellEditingPlugin.startEdit(newDepartment, 0);
                } else {
                    listTree.on('afteritemexpand', function startEdit(list) {
                        if (list === parentList) {
                            selectionModel.select(newDepartment);
                            cellEditingPlugin.startEdit(newDepartment, 0);
                            // remove the afterexpand event listener
                            listTree.un('afteritemexpand', startEdit);
                        }
                    });
                    parentList.expand();
                }
            };

        parentList.appendChild(newDepartment);
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
    addDepartment:function (component, e) {
        var newDepartment = Ext.create('Ext.model.sys.Department', {
            text:'新部门 ',
            leaf:false,
            type:"DEPARTMENT",
            iconCls:'icon-hr',
            parentId:component.ownerCt.getDepartment().getId("id"),
            loaded:true // set loaded to true, so the tree won't try to dynamically load children for this node when expanded
        });
        var listTree = this.getDepartmentTree(),
            cellEditingPlugin = listTree.cellEditingPlugin,
            selectionModel = listTree.getSelectionModel(),
            selectedList = selectionModel.getSelection()[0],
            parentList = selectedList.isLeaf() ? selectedList.parentNode : selectedList,
            expandAndEdit = function () {
                if (parentList.isExpanded()) {
                    selectionModel.select(newDepartment);
                    cellEditingPlugin.startEdit(newDepartment, 0);
                } else {
                    listTree.on('afteritemexpand', function startEdit(list) {
                        if (list === parentList) {
                            selectionModel.select(newDepartment);
                            cellEditingPlugin.startEdit(newDepartment, 0);
                            // remove the afterexpand event listener
                            listTree.un('afteritemexpand', startEdit);
                        }
                    });
                    parentList.expand();
                }
            };

        parentList.appendChild(newDepartment);
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
    editDepartment:function (component, e) {
        var listTree = this.getDepartmentTree(),
            cellEditingPlugin = listTree.cellEditingPlugin;
        cellEditingPlugin.startEdit(component.ownerCt.getDepartment(), 0);
    },
    editOccupation:function (component, e) {
        var listTree = this.getDepartmentTree(),
            cellEditingPlugin = listTree.cellEditingPlugin;
        cellEditingPlugin.startEdit(component.ownerCt.getDepartment(), 0);
    },
    deleteDepartment:function () {
        var me = this,
            listTree = this.getDepartmentTree(),
            selectionModel = listTree.getSelectionModel().getSelection()[0],
            departmentId = selectionModel.get("id");
        if (selectionModel.hasChildNodes()) {
            Ext.Msg.alert('警告', "请先删除子节点！");
        } else {
            me.commonUtils.ajax({
                url:'user.do?method=checkDepartmentBeforeDelete',
                params:{
                    departmentId:departmentId
                },
                success:function (result) {
                    if (result.hasBeUsed) {
                        Ext.Msg.alert('警告', "尚有用户使用该部门!");
                    } else {
                        me.deleteDepartmentImplementor(departmentId);
                    }
                }
            });
        }
    },
    deleteOccupation:function () {
        var me = this,
            listTree = this.getDepartmentTree(),
            selectionModel = listTree.getSelectionModel().getSelection()[0],
            occupationId = selectionModel.get("id");
        me.commonUtils.ajax({
            url:'user.do?method=checkOccupationBeforeDelete',
            params:{
                occupationId:occupationId
            },
            success:function (result) {
                if (result.hasBeUsed) {
                    Ext.Msg.alert('警告', "该职位尚有职员!");
                } else {
                    me.deleteOccupationImplementor(occupationId);
                }
            }
        });
    },
    updateUserDepartmentResponsibility:function (id, departmentResponsibility) {
        var me = this;
        me.commonUtils.ajax({
            url:'user.do?method=updateUserDepartmentResponsibility',
            params:{
                userId:id,
                departmentResponsibility:departmentResponsibility
            },
            success:function (result) {
                me.showDepartmentDetails(me.getDepartmentTreeMenu().getDepartment().get("id"));
            }
        });
    },
    showDepartmentDetails:function (id) {
        var me = this;
        me.getDepartmentMember().store.proxy.extraParams = {
            departmentId:id/*,
             departmentResponsibility:"MEMBER"*/
        };
        me.getDepartmentMember().store.load();
        me.getDepartmentLeader().store.proxy.extraParams = {
            departmentId:id,
            departmentResponsibility:"LEADER"
        };
        me.getDepartmentLeader().store.load();
    },
    resetUserPassword:function (combo, records, eOpts) {
        var me = this,
            rec = combo.up("form").form.getRecord();
        me.commonUtils.ajax({
            url:"user.do?method=resetUserPassword",
            params:{
                userId:rec.get("id")
            },
            success:function (result) {
                rec.set("password", result.password);
                rec.commit();
                Ext.Msg.alert('返回结果', "重置密码成功！");
            }
        });
    },
    updateDepartmentOrOccupation:function (editor, e, eOpts) {
        var me = this, url,
            rec = e.record, message,
            listTree = me.getDepartmentTree();
        if (rec.get("type") == 'DEPARTMENT') {
            url = 'user.do?method=updateDepartment';
            message = "部门重复！";
        } else {
            url = 'user.do?method=updateOccupation';
            message = "职位重复！";
        }
        me.commonUtils.ajax({
            url:url,
            params:{
                id:rec.get("id"),
                parentId:rec.get("parentId"),
                text:rec.get("text")
            },
            success:function (result) {
                if (result.duplicate) {

                    Ext.Msg.alert('警告', message, function () {
                        var listTree = me.getDepartmentTree(),
                            cellEditingPlugin = listTree.cellEditingPlugin;
                        rec.set("text", rec.get("text") + "-新");
                        cellEditingPlugin.startEdit(rec, 0);
                    });
                } else {
                    rec.set("id", result.node.id);
                    rec.set("name", result.node.name);
                    rec.set("sort", result.node.sort);
                    rec.commit();
                    listTree.store.sort('sort', 'ASC');
                }
            }
        });
    },
    //增加用户
    addUser:function () {
        var me = this,
            form = me.getAddUserForm(),
            formEl = form.getEl(),
            thisWin = me.getWindowAddUser(),
            baseForm = form.form;
        if (baseForm.isValid()) {
            formEl.mask('正在保存 . . .');
            me.commonUtils.ajax({
                url:'user.do?method=saveOrUpdateUser',
                params:baseForm.getFieldValues(),
                success:function (result) {
                    me.getUserList().store.loadPage(1);
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
    updateUser:function (view, keyCode) {
        var me = this,
            form = me.getUpdateUserForm(),
            formEl = form.getEl(),
            thisWin = me.getWindowUpdateUser(),
            baseForm = form.form;
        if (baseForm.isValid()) {
            formEl.mask('正在保存 . . .');
            me.commonUtils.ajax({
                url:'user.do?method=saveOrUpdateUser',
                params:baseForm.getFieldValues(),
                success:function (result) {
                    Ext.Msg.alert('返回结果', result.message, function () {
                        thisWin.close();
//                        console.log(baseForm.getRecord().data)
//                        baseForm.updateRecord(baseForm.getRecord()); //todo ext 直接修改record
//                        me.getUserList().store.commitChanges();
                        me.getUserList().store.load();
                        formEl.unmask();
                    });
                },
                failure:function (response) {
                    formEl.unmask();
                }
            });
        }
    },
    deleteUser:function (grid, rowIndex, colIndex) {
        this.deleteUsersImplementor(grid.getStore().getAt(rowIndex).data.id)
    },
    deleteOccupationImplementor:function (occupationId) {
        var me = this;
        Ext.MessageBox.confirm('确认', '确认删除职位?', function (btn) {
            if (btn == "yes") {
                me.commonUtils.ajax({
                    url:'user.do?method=deleteOccupation',
                    params:{occupationId:occupationId},
                    success:function (result) {
//                        me.getDepartmentTree().store.load();
                        me.getDepartmentTree().getStore().getNodeById(occupationId).remove();
                    }
                });
            }
        });
    },
    deleteDepartmentImplementor:function (departmentId) {
        var me = this;
        Ext.MessageBox.confirm('确认', '确认删除部门?', function (btn) {
            if (btn == "yes") {
                me.commonUtils.ajax({
                    url:'user.do?method=deleteDepartment',
                    params:{departmentId:departmentId},
                    success:function (result) {
//                        me.getDepartmentTree().store.load();
                        me.getDepartmentTree().getStore().getNodeById(departmentId).remove();
                    }
                });
            }
        });
    },
    deleteUsersImplementor:function (ids) {
        var me = this;
        Ext.MessageBox.confirm('确认', '确认删除用户?', function (btn) {
            if (btn == "yes") {
                me.commonUtils.ajax({
                    url:'user.do?method=deleteUser',
                    params:{userIds:ids},
                    success:function (result) {
                        me.getUserList().store.loadPage(1);
                    }
                });
            }
        });
    },
    editUser:function (grid, rowIndex, colIndex) {
        var me = this,
            rec = grid.getStore().getAt(rowIndex),
            win = me.getWindowUpdateUser();
        me.commonUtils.mask();
        win.down("form").loadRecord(rec);
//        win.down("form").form.getRecord().data.occupationName = rec.data.occupationId;
        if (rec.getData().userGroupId) {
            //选中角色
            this.getUserGroupUpdateSelect().store.load();
            this.getUserGroupUpdateSelect().setValue(rec.getData().userGroupId);
            this.getUserGroupUpdateSelect().unsetActiveError();
        }
        //禁止修改密码
        win.down("form").down("[name='password']").hide();
        me.commonUtils.unmask();
        win.show();

    },
    //search action
    onSearchUsers:function () {
        var me = this.getUserList(),
            department = Ext.getCmp('departmentTreePicker').getDepartment(),
            occupation = Ext.getCmp('departmentTreePicker').getOccupation(),
            departmentName = department ? department.get("text") : "",
            occupationName = occupation ? occupation.get("text") : "";

        me.store.proxy.extraParams = {
            userNo:Ext.getCmp('searchUserNo').getValue(),
            roleName:Ext.getCmp('searchRoleName').getValue(),
            name:Ext.getCmp('searchName').getValue(),
            departmentName:departmentName === "统购平台" ? "" : departmentName,
            occupationName:occupationName,
            status:Ext.getCmp('searchStatus').getValue()
        };
        me.selModel.deselectAll(true)
        me.store.loadPage(1);
    },
    //get selected role ids
    getSelectionIds:function () {
        var me = this;
        return me.commonUtils.getSelectionIds(this.getUserList())
    },
    //update user status in memory
    updateStatusMemory:function (status) {
        var me = this.getUserList();
        var selects = me.getSelectionModel().getSelection();
        if (selects) {
            for (var i = 0, max = selects.length; i < max; i++) {
                selects[i].data.statusValue = status;
            }
        }
        me.store.loadData(me.store.data.items, false);
    },
    deleteUsers:function () {
        var me = this;
        this.deleteUsersImplementor(me.getSelectionIds())
    },
    //禁用用户
    forbiddenSomeUsers:function () {
        var me = this;
        me.commonUtils.ajax({
            url:'user.do?method=updateUsersStatus',
            params:{ids:me.getSelectionIds(), status:'inActive'},
            success:function (result) {
//                me.updateStatusMemory("禁用");
                me.getUserList().store.load();
            }
        });
    },
    //启用用户
    enableSomeUsers:function () {
        var me = this;
        me.commonUtils.ajax({
            url:'user.do?method=updateUsersStatus',
            params:{ids:me.getSelectionIds(), status:'active'},
            success:function (result) {
                me.getUserList().store.load();
//                me.updateStatusMemory("启用");
            }
        });
    }
});