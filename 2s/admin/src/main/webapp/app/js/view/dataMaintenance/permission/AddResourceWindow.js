/**
 * 权限维护 - 资源模块 - 新增资源
 * 新增资源窗口
 * @author:zhangjuntao
 */
Ext.define('Ext.view.dataMaintenance.permission.AddResourceWindow', {
    alias:'widget.addResourceWindow',
    extend:'Ext.window.Window',
    iconCls:'icon-user',
    layout:'fit',
    collapsible:true,
    requires:["Ext.view.dataMaintenance.permission.ResourceForm"],
    initComponent:function () {
        var me = this;
        Ext.apply(me, {
            items:Ext.widget('resourceForm')
        });
        me.callParent();
    },
    title:'增加资源',
    close:function () {
        Ext.create("Ext.utils.Common").unmask();
        this.doClose();
    }
});

