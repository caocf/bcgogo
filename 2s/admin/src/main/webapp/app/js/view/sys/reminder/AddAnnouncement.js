Ext.define('Ext.view.sys.reminder.AddAnnouncement', {
    alias:'widget.addAnnouncement',
    extend:'Ext.window.Window',
    iconCls:'icon-user',
    layout:'fit',
    collapsible:true,
    requires:["Ext.view.sys.reminder.AnnouncementEditor"],
    initComponent:function () {
        var me = this;
       Ext.apply(me, {
            items:[{
              xtype:"sysannouncementEditor"
            }]
        });
        me.callParent();
    },
    title:'新增公告',
    close:function () {
        Ext.create("Ext.utils.Common").unmask();
        this.doClose();
    }
});

