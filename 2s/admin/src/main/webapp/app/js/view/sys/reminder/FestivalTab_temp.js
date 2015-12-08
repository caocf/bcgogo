Ext.define('Ext.view.sys.reminder.FestivalTab_temp', {
  extend:'Ext.tab.Panel',
  alias:'widget.sysfestivalTab',
  forceFit:true,
  frame:true,
  autoHeight:true,
  activeTab: 0,
  autoScroll:true,
  id:'sysfestivalTabId',
  requires:[
//    "Ext.view.sys.announcement.AnnouncementList" ,
//    'Ext.view.sys.announcement.AnnouncementEditor'
  ],
  initComponent:function (){
    var me = this;
    Ext.apply(me, {
      items:[
        {
          title:'节日列表',
          xtype:"sysfestivalList",
          id:'annList'
        },
        {
          title:'编辑节日',
          xtype:"festivalEditor",
          id:'editorId'
        }
      ]
    });

    me.callParent();
  }

});