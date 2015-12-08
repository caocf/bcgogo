/**
 * 权限维护 - 资源模块 - 详细（角色列表)
 * 对应角色被哪些Role使用
 * @author zhangjuntao
 */
Ext.define('Ext.view.dataMaintenance.permission.ResourceRoleWindow', {
    alias:'widget.resourceRoleWindow',
    extend:'Ext.window.Window',
    iconCls:'icon-user',
    layout:'fit',
    collapsible:true,
    uses:[
        "Ext.view.dataMaintenance.permission.ResourceRoleList",
        "Ext.view.dataMaintenance.permission.AddRoleToResourceWindow"
    ],
    initComponent:function () {
        var me = this;
        Ext.apply(me, {
            items:Ext.widget('resourceRoleList')
        });
        me.callParent();
    },
    title:'角色列表',
    close:function () {
        Ext.create("Ext.utils.Common").unmask();
        this.doClose();
    }
});

