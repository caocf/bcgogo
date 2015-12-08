Ext.define('Ext.view.sys.reminder.AnnouncementList', {
  extend:'Ext.grid.Panel',
  alias:'widget.sysannouncementList',
  store:'Ext.store.sys.Announcement',
  autoScroll:true,
  columnLines:true,
  stripeRows:true,            //每列是否是斑马线分开
//    enableKeyNav:true,          //允许键盘操作，即上下左右移动选中点
  forceFit:true,              //自动填充，即让所有列填充满gird宽度
  multiSelect:true,           //可以多选
  autoHeight:true,

  dockedItems:[
    {
      xtype:'toolbar',
      items:[
        {
          text:'新增',
          xtype:'button',
          tooltip:'新增公告',
          action:'addAnnouncement',
          scope:this,
          iconCls:'icon-add'
        }
      ]
    },
    {
      dock:'bottom',
      xtype:'pagingtoolbar',
      store:'Ext.store.sys.Announcement',
      displayInfo:true
    },
    {
      xtype:'toolbar',
      dock:'top',
      id:'searchUserComponent'
    }
  ],
  initComponent:function () {
    var me = this;
    Ext.apply(me, {
      selModel:Ext.create('Ext.selection.CheckboxModel', {}),
      columns:[
        {
          header:'No.',
          xtype:'rownumberer',
          sortable:false,
          width:25
        },
        {
          header:'发布时间',
          dataIndex:'releaseDate'
        },
        {
          header:'标题',
          dataIndex:'title'
        },
        {
          header:'发布人',
          dataIndex:'releaseMan'
        },
        {
          header:'状态',
          dataIndex:'status'
        },

        {
          xtype:'actioncolumn',
          header:'操作',
          width:60,
          items:[
            {
              text:'编辑',
              tooltip:'编辑公告',
              scope:me,
              icon:'app/images/icons/edit.png'
            }
//            ,
//            {
//              text:'删除',
//              tooltip:'删除公告',
//              scope:me,
//              action:'deleteAnnouncement',
////              id:'deleteAnnouncement',
//              icon:'app/images/icons/delete.png'
//            }
          ]
        }
      ]
    });
    this.callParent(arguments);
  }
});