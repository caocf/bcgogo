Ext.define('Ext.view.sys.userGroup.Update', {
    alias:'widget.windowUpdateUserGroup',
    extend:'Ext.window.Window',
    iconCls:'icon-user',
    layout:'fit',
    requires:["Ext.view.sys.userGroup.Form"],
    initComponent:function () {
        var me = this;
        Ext.apply(me, {
            items:Ext.widget('formUserGroup', {})
        });
        me.callParent();
    },

    title:'修改角色',
    close:function () {
        Ext.create("Ext.utils.Common").unmask();
        this.doClose();
    }
});

