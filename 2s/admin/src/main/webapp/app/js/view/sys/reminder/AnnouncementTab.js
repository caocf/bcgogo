Ext.define('Ext.view.sys.reminder.AnnouncementTab', {
  extend:'Ext.tab.Panel',
  alias:'widget.sysannouncementTab',
  forceFit:true,
  frame:true,
  autoHeight:true,
  activeTab: 0,
  autoScroll:true,
  id:'sysannouncementTabId',
  requires:[
//    "Ext.view.sys.announcement.AnnouncementList" ,
//    'Ext.view.sys.announcement.AnnouncementEditor'
  ],
  initComponent:function (){
    var me = this;
    Ext.apply(me, {
      items:[
        {
          title:'公告列表',
          xtype:"sysannouncementList",
          id:'annList'
        },
        {
          title:'编辑公告',
          xtype:"announcementEditor",
          id:'editorId'
        }
      ]
    });

    me.callParent();
    console.debug('begin:');
    console.debug(me.child('#editorId'));
  }

});