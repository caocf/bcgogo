Ext.define('Ext.view.sys.reminder.AnnouncementEditor', {
  extend:'Ext.form.Panel',
  alias:'widget.sysannouncementEditor',
  bodyPadding:5,
  autoScroll:true,
  width:700,
  border:false,
  layout:'anchor',
  defaults:{
    anchor:'100%'
  },
  store:'Ext.store.sys.Announcement',

  initComponent:function () {
    var me = this;
    Ext.apply(me, {
      items: [
        {
          xtype:'fieldcontainer',
          style:{
            align: 'top'
          }
        },
        {
          xtype: 'hiddenfield',
          name: 'idStr',
          value: '',
          id:'announcementId'
        },
        {
          xtype: 'fieldcontainer',
          style:{
            width:'100%'
          },
          border:false,
          fieldLabel:'标题',
          labelWidth:80,
          items:[
            {
              xtype: 'textfield',
              name:'title',
              hideLabel: true,
//              width: '500px',
              id:'titleInput'
            }

          ]
        },
        {
          xtype: 'fieldcontainer',
          style:{
            width:'100%'
          },
          border:true,
          fieldLabel:'发布内容',
          labelWidth:80,
          frame : true,
          layout:'fit',
          items:[
            {
              xtype: 'htmleditor',
              name:'content',
              id:'announceEditor',
              hideLabel :false,
              enableFont :true,//是否启用字体选择按钮 默认为true
              enableFontSize :true,//是否启用字体加大缩小按钮
              enableFormat :true,//是否启用加粗斜体下划线按钮
              enableLists :false,//是否启用列表按钮t
              fontFamilies :['宋体','隶书','黑体'],
              frameSize:{
                top:10,
                bottom:10
              },
              enableSourceEdit :true,//是否启用代码编辑按钮
              anchor : '100%'         ,
              style:{
                width:'450px',
                align:'top'
              }
            }]
        },
        {
          xtype: 'fieldcontainer',
          layout: {
            type: 'hbox',
            align: 'middle'
          },
          style:{
            top:'20px'
          },
          border:false,
          fieldLabel:'发布时间',
          labelWidth:80,
          items:[
            {
              xtype:"datefield",
              format: 'Y-m-d',
              name:'releaseDate',
              value: new Date(),
              minValue: new Date(),
              editable :false,
              activeError:'',
              id:'releaseDateInput'
            }

          ]
        },
        {
          xtype: 'panel',
//          style:{
//            width:'50%'
//          },
          border:true,
          fieldLabel:'',
          labelWidth:80,
          frame : false,
          border:false,
          layout: {
            type: 'hbox',
            align:'middle'
          },
          buttons:[
            {
              id:"saveOrUpdateAnnouncement",
              xtype:"button",
              style:{
                width:'50px',
                left:50
              },
              text:'保存',
              action:'saveOrUpdateAnnouncement'
            },

            {
              id:"cancellAnnouncement",
              xtype:"button",
              left:50,
              style:{
                width:'50px'
              },
              text:'取消',
              action:'cancellAnnouncement'
            }
          ]
        }
      ]
    })
    Ext.QuickTips.init();
    this.callParent();

  },
  listeners: {
    afterrender: function(){
      Ext.getCmp('announceEditor').setHeight(300);
    }
  },
  editAnnouncement:function(){
    var me = this;
    var users = me.up('panel');
    me.setActiveTab(users)
  }
});