Ext.define('Ext.view.sys.userGroup.Add', {
    alias:'widget.windowAddUserGroup',
    extend:'Ext.window.Window',
    iconCls:'icon-user',
    layout:'fit',
    collapsible:true,
    requires:["Ext.view.sys.userGroup.Form"],
    initComponent:function () {
        var me = this;
        Ext.apply(me, {
            items:Ext.widget('formUserGroup', {})
        });
        me.callParent();
    },
    title:'增加角色',
    close:function () {
        Ext.create("Ext.utils.Common").unmask();
        this.doClose();
    }
});

