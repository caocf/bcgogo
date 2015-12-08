Ext.define('Ext.view.sys.reminder.FestivalEditor', {
  extend:'Ext.form.Panel',
  alias:'widget.sysfestivalEditor',
  bodyPadding:5,
  autoScroll:true,
  width:500,
  heigth:500,
  border:false,
  layout:'anchor',
  defaults:{
    anchor:'100%'
  },
  store:'Ext.store.sys.Festival',

  initComponent:function () {
    var me = this;
//    me.addEvents('create');
    Ext.apply(me, {
      items: [
        {
          xtype:'fieldcontainer',
          style:{
            heigth:'150px',
            align: 'top'

          }
        },
        {
          xtype: 'hiddenfield',
          name: 'idStr',
          value: '',
          id:'festivalId'
        },
        {
          xtype: 'fieldcontainer',
          style:{
            width:400
          },
          border:false,
          fieldLabel:'节日名',
          labelWidth:80,
          items:[
            {
              xtype: 'textfield',
              name:'title',
              hideLabel: true,
              width: '100px',
              id:'festivalTitleInput'
            },
            {
              xtype: 'label',
              text: '(节日名不超过三个字)'
            }

          ]
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
          fieldLabel:'节日时间',
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
              id:'festivalReleaseDateInput'
            }

          ]
        },

        {
          xtype: 'fieldcontainer',
          style:{
//            height:'320px',
            width:'100%'
          },
          heigth:300,
          border:true,
          fieldLabel:'提醒提前天数',
          labelWidth:80,
          frame : true,
          layout:'fit',
          items:[
            {
              xtype: 'combo',
              displayField: 'name',
              name:'preDay',
              editable :false,
              allowBlank : false,
              forceSelection: true,
              id:'festivalPreDay',
              width:30,
              valueField: 'day',
              store:Ext.create('Ext.data.Store', {
                fields: ['day', 'name'],
                data : [
                  {"day":"0", "name":"0天"},
                  {"day":"1", "name":"1天"},
                  {"day":"2", "name":"2天"},
                  {"day":"3", "name":"3天"}
                  //...
                ]
              })

            }]
        },

        {
          xtype: 'panel',
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
              id:"saveOrUpdateFestival",
              xtype:"button",
              style:{
                width:'50px',
                left:50
              },
              text:'保存',
              action:'saveOrUpdateFestival'
            },

            {
              id:"cancellFestival",
              xtype:"button",
              left:50,
              style:{
                width:'50px'
              },
              text:'取消',
              action:'cancellFestival'
            }
          ]
        }
      ]
    })
    Ext.QuickTips.init();
    this.callParent();

  },
  editAnnouncement:function(){
    var me = this;
    var users = me.up('panel');
    me.setActiveTab(users)
  }
});