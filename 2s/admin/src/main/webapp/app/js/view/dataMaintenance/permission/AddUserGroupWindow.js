/**
 * 权限维护 - 版本维护 - 新增用户组
 * 新增用户组窗口
 * @author:zhangjuntao
 */
Ext.define('Ext.view.dataMaintenance.permission.AddUserGroupWindow', {
    alias:'widget.addUserGroupWindow',
    extend:'Ext.window.Window',
    iconCls:'icon-user',
    layout:'fit',
    collapsible:true,
    requires:["Ext.view.dataMaintenance.permission.UserGroupForm"],
    initComponent:function () {
        var me = this;
        Ext.apply(me, {
            items:Ext.widget('userGroupForm')
        });
        me.callParent();
    },
    title:'新增用户组',
    close:function () {
        Ext.create("Ext.utils.Common").unmask();
        this.doClose();
    }
});

