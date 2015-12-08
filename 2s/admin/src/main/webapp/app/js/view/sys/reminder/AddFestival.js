Ext.define('Ext.view.sys.reminder.AddFestival', {
    alias:'widget.addFestival',
    extend:'Ext.window.Window',
    iconCls:'icon-user',
    layout:'fit',
    collapsible:true,
    requires:["Ext.view.sys.reminder.FestivalEditor"],
    initComponent:function () {
        var me = this;
       Ext.apply(me, {
            items:[{
              xtype:"sysfestivalEditor"
            }]
        });
        me.callParent();
    },
    title:'新增节日',
    close:function () {
        Ext.create("Ext.utils.Common").unmask();
        this.doClose();
    }
});

