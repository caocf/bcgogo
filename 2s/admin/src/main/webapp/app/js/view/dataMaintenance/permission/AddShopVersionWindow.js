/**
 * 权限维护 - 版本维护 - 新增版本
 * 新增资源窗口
 * @author:zhangjuntao
 */
Ext.define('Ext.view.dataMaintenance.permission.AddShopVersionWindow', {
    alias:'widget.addShopVersionWindow',
    extend:'Ext.window.Window',
    iconCls:'icon-user',
    layout:'fit',
    collapsible:true,
    initComponent:function () {
        var me = this;
        Ext.apply(me, {
            items:Ext.widget('shopVersionForm')
        });
        me.callParent();
    },
    title:'增加版本',
    close:function () {
        Ext.create("Ext.utils.Common").unmask();
        this.doClose();
    }
});

