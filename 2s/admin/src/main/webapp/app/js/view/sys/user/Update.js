Ext.define('Ext.view.sys.user.Update', {
    alias:'widget.windowupdateuser',
    extend:'Ext.window.Window',
    iconCls:'icon-user',
    layout:'fit',
    collapsible:true,
    requires:["Ext.view.sys.user.Form"],
    initComponent:function () {
        var me = this;
        Ext.apply(me, {
            items:Ext.widget('formuser')
        });
        me.callParent();
    },
    title:'修改用户信息',
    close:function () {
        Ext.create("Ext.utils.Common").unmask();
        this.doClose();
    }
});

