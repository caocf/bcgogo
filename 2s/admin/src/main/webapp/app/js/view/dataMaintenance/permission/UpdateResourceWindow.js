/**
 * 权限维护 - 资源模块 - 编辑
 * 编辑资源窗口
 * @author zhangjuntao
 */
Ext.define('Ext.view.dataMaintenance.permission.UpdateResourceWindow', {
    alias:'widget.updateResourceWindow',
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
    title:'修改资源',
    close:function () {
        Ext.create("Ext.utils.Common").unmask();
        this.doClose();
    }
});

