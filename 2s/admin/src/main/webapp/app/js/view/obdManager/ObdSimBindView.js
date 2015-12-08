Ext.define('Ext.view.obdManager.ObdSimBindView', {
  extend: 'Ext.grid.Panel',
  alias: 'widget.ObdSimBindView',
  store:'Ext.store.obdManager.ObdSimBindStore',
  forceFit: true, //自动填充，即让所有列填充满gird宽度
  multiSelect: true, //可以多选
  frame: true,
  autoHeight: true,
  autoScroll: true,
  columnLines: true,
  stripeRows: true, //每列是否是斑马线分开
  selModel:{
    selType:"checkboxmodel"
  },
  border: 0,
  requires: [
    'Ext.app.ActionTextColumn',
    'Ext.ux.date.MonthField'
  ],
  plugins: [
    Ext.create('Ext.grid.plugin.RowEditing', {
      pluginId:'obdSimRowEditing',
      saveBtnText: '保存',
      cancelBtnText: '取消',
      errorsText:'错误',
      dirtyText:"你要确认或取消更改",
      listeners:{
        beforeedit:function(editor,e){
          var rec = e.record;
          if(!rec.get("edit")){
            return false;
          }
          var columns = e.grid.columns;
          var obdStatus = rec.get("obdStatus");
          var obdSimType = rec.get("obdSimType");
          var isMeiEdit = false,isObdVersionEdit = false,isSpecEdit = false, isColorEdit = false, isPackEdit = false,
              isOpenCrashEdit = false,isOpenShakeEdit = false,isSimNoEdit = false,isMobileEdit = false,
              isUserDateEdit = false,isUsePeriodEdit = false;
          if(obdSimType == "SINGLE_GSM_OBD"){
            isMeiEdit = true;
            isObdVersionEdit = true;
            isSpecEdit = true;
            isColorEdit = true;
            isPackEdit = true;
            isOpenCrashEdit = true;
            isOpenShakeEdit = true;
          }else if(obdSimType == "SINGLE_SIM"){
            isSimNoEdit = true;
            isMobileEdit = true;
            isUserDateEdit = true;
            isUsePeriodEdit = true;
          }else if(obdSimType == "COMBINE_GSM_OBD_SIM"){
            isObdVersionEdit = true;
            isSpecEdit = true;
            isColorEdit = true;
            isPackEdit = true;
            isOpenCrashEdit = true;
            isOpenShakeEdit = true;
            isUserDateEdit = true;
            isUsePeriodEdit = true;
          }else if(obdSimType == "COMBINE_MIRROR_OBD_SIM"){
            isObdVersionEdit = true;
            isSpecEdit = true;
            isColorEdit = true;
            isPackEdit = true;
            isOpenCrashEdit = true;
            isOpenShakeEdit = true;
            isUserDateEdit = true;
            isUsePeriodEdit = true;
          }else if(obdSimType == "SINGLE_MIRROR_OBD"){
            isMeiEdit = true;
            isObdVersionEdit = true;
            isSpecEdit = true;
            isColorEdit = true;
            isPackEdit = true;
            isOpenCrashEdit = true;
            isOpenShakeEdit = true;
          }
          var editorForm = editor.editor.form;
          if (isMeiEdit) {
            editorForm.findField("imei").enable();
          } else {
            editorForm.findField("imei").disable();
          }
          if (isObdVersionEdit) {
            editorForm.findField("obdVersion").enable();
          } else {
            editorForm.findField("obdVersion").disable();
          }
          if (isSpecEdit) {
            editorForm.findField("spec").enable();
          } else {
            editorForm.findField("spec").disable();
          }
          if (isColorEdit) {
            editorForm.findField("color").enable();
          } else {
            editorForm.findField("color").disable();
          }
          if (isPackEdit) {
            editorForm.findField("pack").enable();
          } else {
            editorForm.findField("pack").disable();
          }
          if (isOpenCrashEdit) {
            editorForm.findField("openCrash").enable();
          } else {
            editorForm.findField("openCrash").disable();
          }
          if (isOpenShakeEdit) {
            editorForm.findField("openShake").enable();
          } else {
            editorForm.findField("openShake").disable();
          }
          if (isSimNoEdit) {
            editorForm.findField("simNo").enable();
          } else {
            editorForm.findField("simNo").disable();
          }
          if (isMobileEdit) {
            editorForm.findField("mobile").enable();
          } else {
            editorForm.findField("mobile").disable();
          }
          if (isUserDateEdit) {
            editorForm.findField("useDateStr").enable();
          } else {
            editorForm.findField("useDateStr").disable();
          }
          if (isUsePeriodEdit) {
            editorForm.findField("usePeriod").enable();
          } else {
            editorForm.findField("usePeriod").disable();
          }
        },
        edit:function (editor, e) {
          var params = e.record.getData();
          if(params["useDateStr"]){
            params["useDateStr"]= Ext.util.Format.date(params["useDateStr"],'Y-m')
          }
          Ext.create("Ext.utils.Common").ajax({
            url:'obdManage.do?method=updateSingleObdSim',
            params:params,
            success:function (result) {
              if(result.success){
                e.record.commit();
              }else{
                Ext.Msg.alert("更新失败",result.msg);
                e.record.reject();
              }

              console.log(result);
            },
            fail:function(response){
              Ext.Msg.alert("更新失败",result.msg);
            }
          });
        }
      }
    })
  ],
  dockedItems: [
    {
      xtype: 'toolbar',
      dock: 'top',
      items: [
        {
          xtype: "combobox",
          name: "imei",
          fieldLabel: "IMIE号",
          labelWidth: 50,
          labelAlign: "right",
          width: 200,
          valueField: 'imei',
          displayField: 'imei',
          minChars: 1,
          triggerAction: "all",
          queryParam: 'queryImei',
          store: Ext.create('Ext.store.obdManager.ObdImeiStore', {
            proxy: {
              type: 'ajax',
              api: {
                read: 'obdManage.do?method=getImeiSuggestion'
              },
              reader: {
                root: 'results',
                type: 'json',
                totalProperty: "totals"
              },
              extraParams: {
                scene: "ALL"
              }
            }
          })
        },
        {
          xtype: "combobox",
          name: "mobile",
          fieldLabel: "手机号",
          labelAlign: "right",
          labelWidth: 50,
          width: 170,
          valueField: 'mobile',
          displayField: 'mobile',
          minChars: 1,
          triggerAction: "all",
          queryParam: 'queryMobile',
          queryMode: 'remote',
          store: Ext.create('Ext.store.obdManager.ObdImeiStore', {
            proxy: {
              type: 'ajax',
              api: {
                read: 'obdManage.do?method=getSimMobileSuggestion'
              },
              reader: {
                root: 'results',
                type: 'json',
                totalProperty: "totals"
              },
              extraParams: {
                scene: "ALL"
              }
            }
          })
        }
        ,
        {
          fieldLabel: '开通日期',
          labelWidth: 70,
          labelAlign:"right",
          xtype: "monthfield",
          emptyText: "开始",
          format: 'Y-m',
          width: 170,
          name: 'startUserDateStr'
        },
        "至",
        {
          xtype: "monthfield",
          emptyText: "结束",
          width: 100,
          format: 'Y-m',
          name: 'endUserDateStr'
        } ,
        {
          xtype: "textfield",
          name: "ownerName",
          fieldLabel: "当前归属",
          labelAlign:"right",
          labelWidth: 70,
          width: 170
        }
      ]
    },
    {
      xtype: 'toolbar',
      dock: 'top',
      items: [
        {
          xtype: "checkboxgroup",
          name: "obdSimTypeStrArr",
          fieldLabel: "类型",
          labelAlign:"right",
          labelWidth: 40,
          width: 280,
          columns: 3,
          items: [
            {
//                            listeners: {change: me.toggleCheckBox},
              boxLabel: 'OBD单品',
              name: 'obdSimTypeStr',
              inputValue: 'SINGLE_GSM_OBD',
              width: 80},
            {
//                            listeners: {change: me.toggleCheckBox},
              boxLabel: 'SIM卡单品',
              name: 'obdSimTypeStr',
              inputValue: 'SINGLE_SIM',
              width: 80},
            {
//                            listeners: {change: me.toggleCheckBox},
              boxLabel: 'OBD成品',
              name: 'obdSimTypeStr',
              inputValue: 'COMBINE_GSM_OBD_SIM',
              width: 80},
            {
//                            listeners: {change: me.toggleCheckBox},
              boxLabel: '后视镜单品',
              name: 'obdSimTypeStr',
              inputValue: 'SINGLE_MIRROR_OBD',
              width: 80},
            {
              //                        listeners: {change: me.toggleCheckBox},
              boxLabel: '后视镜成品',
              name: 'obdSimTypeStr',
              inputValue: 'COMBINE_MIRROR_OBD_SIM',
              width: 80}
          ]

        },
        {
          xtype: "combobox",
          name: "obdVersion",
          fieldLabel: "软件版本",
          labelAlign: "right",
          labelWidth: 70,
          width: 170,
          valueField: 'obdVersion',
          displayField: 'obdVersion',
          minChars: 1,
          triggerAction: "all",
          queryParam: 'queryObdVersion',
          queryMode: 'remote',
          store: Ext.create('Ext.store.obdManager.ObdImeiStore', {
            proxy: {
              type: 'ajax',
              api: {
                read: 'obdManage.do?method=getObdVersionSuggestion'
              },
              reader: {
                root: 'results',
                type: 'json',
                totalProperty: "totals"
              }
            }
          })
        },
        '',
        {
          xtype: "checkboxgroup",
          name: "obdSimStatusStrArr",
          fieldLabel: "状态",
          labelAlign:"right",
          labelWidth: 40,
          width: 280,
          columns: 6,
          items: [
            {
//                            listeners: {change: me.toggleCheckBox},
              boxLabel: '未组装',
              name: 'obdSimStatusStr',
              inputValue: 'UN_ASSEMBLE',
              width: 70},
            {
//                            listeners: {change: me.toggleCheckBox},
              boxLabel: '待出库',
              name: 'obdSimStatusStr',
              inputValue: 'WAITING_OUT_STORAGE',

              width: 70},
            {
//                            listeners: {change: me.toggleCheckBox},
              boxLabel: '已领出',
              name: 'obdSimStatusStr',
              inputValue: 'PICKED',
              width: 70},
            {
//                            listeners: {change: me.toggleCheckBox},
              boxLabel: '已代理',
              name: 'obdSimStatusStr',
              inputValue: 'AGENT',
              width: 70},
            {
//                            listeners: {change: me.toggleCheckBox},
              boxLabel: '销售中',
              name: 'obdSimStatusStr',
              inputValue: 'ON_SELL',
              width: 70},
            {
//                            listeners: {change: me.toggleCheckBox},
              boxLabel: '已售出',
              name: 'obdSimStatusStr',
              inputValue: 'SOLD',
              width: 70}

          ]

        }
        ,
        "->",
        {
          text: "查询",
          xtype: 'button',
          action: 'search',
          iconCls: "icon-search"
        },
        {
          text: "重置",
          xtype: 'button',
          action: 'reset',
          iconCls: "icon-reset"
        }
      ]
    },
    {
      xtype: 'toolbar',
      dock: 'top',
      id:"obdSimOperationToolbar",
      items: [
        "->",
        {
          text: "导入库存",
          xtype: 'button',
          action: 'importInventory',
          id:"importInventoryBtn",
          hidden:true
        },
        {
          text: "模板下载",
          xtype: 'button',
          action: 'downOBDSimTemplateDown',
          id: 'downOBDSimTemplateDownBtn',
          hidden:true
        },
        {
          text: "批量编辑",
          xtype: 'button',
          action: 'obdSimMultiEdit',
          id: 'obdSimMultiEditBtn',
          hidden:true
        },
        {
          text: "批量出库",
          xtype: 'button',
          action: 'batchOutStorage',
          id: 'batchOutStorageBtn',
          hidden:true
        },
        {
          text: "批量销售",
          xtype: 'button',
          action: 'batchSale',
          id: 'batchSaleBtn',
          hidden:true
        }
      ]
    }
  ],
  bbar:{
    xtype:'pagingtoolbar',
    id:'obdManager_page',
    store:'Ext.store.obdManager.ObdSimBindStore',
    dock:'bottom',
    displayInfo:true
  },
  columns:[
    {xtype: 'rownumberer',width:50,text:"No"},
    {text:"类型",dataIndex:"obdSimType",width:70,border: 50,renderer:function(value){
      if(value == "SINGLE_GSM_OBD"){
        return "OBD单品";
      }else if(value == "SINGLE_SIM"){
        return "SIM单品";
      }else if(value == "COMBINE_GSM_OBD_SIM"){
        return "OBD成品";
      }else if(value == "SINGLE_MIRROR_OBD"){
        return "后视镜单品";
      }else if(value == "COMBINE_MIRROR_OBD_SIM"){
        return "后视镜成品";
      } else if(value == "COMBINE_GSM_POBD_SIM"){
        return "（P）OBD成品";
      }else if(value == "COMBINE_GSM_OBD_SSIM"){
        return "(2s)OBD成品";
      }
    }},
    {text: "IMIE号", dataIndex: "imei", width: 120, border: 50,
      editor: {
        xtype: 'textfield',
        allowBlank: false
      }
    },
    {text:"软件版本",dataIndex:"obdVersion",width:120,border: 50,
      editor: {
        xtype: 'textfield',
        allowBlank: false
      }
    },
    {text:"规格",dataIndex:"spec",width:70,border: 50,
      editor: {
        xtype: 'textfield',
        allowBlank: false
      }
    },
    {text:"颜色",dataIndex:"color",width:70,border: 50,
      editor: {
        xtype: 'textfield',
        allowBlank: false
      }
    },
    {text:"包装",dataIndex:"pack",width:70,border: 50,
      editor: {
        xtype: 'textfield',
        allowBlank: false
      }
    },
    {text:"碰撞报警",dataIndex:"openCrash",width:50,border: 50,
      editor: {
        xtype: 'combobox',
        forceSelection:true,
        allowBlank: false,
        mode:'local',
        store:Ext.create("Ext.data.SimpleStore",{
          fields:['value','text'],
          data:[
            ['YES','YES'],
            ['NO','NO']
          ]
        })
      }
    },
    {text:"震动报警",dataIndex:"openShake",width:50,border: 50,
      editor: {
        xtype: 'combobox',
        forceSelection:true,
        allowBlank: false,
        mode:'local',
        store:Ext.create("Ext.data.SimpleStore",{
          fields:['value','text'],
          data:[
            ['YES','YES'],
            ['NO','NO']
          ]
        })
      }
    },
    {text:"SIM卡编号",dataIndex:"simNo",width:140,border: 50,
      editor: {
        xtype: 'textfield',
        allowBlank: false
      }
    },
    {text:"手机号码",dataIndex:"mobile",width:90,border: 50,
      editor: {
        xtype: 'textfield',
        allowBlank: false
      }
    },
    {text:"开通年月",dataIndex:"useDateStr",width:70,border: 50,xtype:'datecolumn',format:'Y-m',
      editor: {
        xtype: 'monthfield',
        allowBlank: false,
        format: 'Y-m'
      }
    },
    {text: "服务期", dataIndex: "usePeriod", width: 60, border: 50,
      editor: {
        xtype: 'numberfield',
        allowBlank: false
      }
    },
    {text: "状态", dataIndex: "obdStatusStr", width: 60, border: 50
    },
    {text:"当前归属",dataIndex:"ownerName",width:70,border: 50},
    {
      id:"obdManagerOperation",
      xtype:"actiontextcolumn",
      header:"操作",
      width: 70,
      items: [
        {
          getClass: function (v, meta, rec) {
            if (rec.get('edit')) {
              this.items[0].text = '编辑';
              return '';
            } else {
              this.items[0].text = '';
              return '';
            }
          },
          handler: function (gridview, rowIndex, colIndex) {
            this.fireEvent('editSingleObdSimClick', gridview, colIndex, rowIndex);
          }
        },
        {
          getClass: function (v, meta, rec) {
            if (rec.get('delete')) {
              this.items[1].text = '删除';
              return '';
            } else {
              this.items[1].text = '';
              return '';
            }
          },
          handler: function (gridview, rowIndex, colIndex) {
            this.fireEvent('deleteObdSimClick', gridview, colIndex, rowIndex);
          }
        },{
          getClass: function (v, meta, rec) {
            if (rec.get('package')) {
              this.items[2].text = '组装';
              return '';
            } else {
              this.items[2].text = '';
              return '';
            }
          },
          handler: function (gridview, rowIndex, colIndex) {
            this.fireEvent('combineClick', gridview, colIndex, rowIndex);
          }
        },{
          getClass: function (v, meta, rec) {
            if (rec.get('outStorage')) {
              this.items[3].text = '出库';
              return '';
            } else {
              this.items[3].text = '';
              return '';
            }
          },
          handler: function (gridview, rowIndex, colIndex) {
            this.fireEvent('outStorageClick', gridview, colIndex, rowIndex);
          }
        },{
          getClass: function (v, meta, rec) {
            if (rec.get('split')) {
              this.items[4].text = '拆分';
              return '';
            } else {
              this.items[4].text = '';
              return '';
            }
          },
          handler: function (gridview, rowIndex, colIndex) {
            this.fireEvent('splitObdSimClick', gridview, colIndex, rowIndex);
          }
        },{
          getClass: function (v, meta, rec) {
            if (rec.get('sell')) {
              this.items[5].text = '销售';
              return '';
            } else {
              this.items[5].text = '';
              return '';
            }
          },
          handler: function (gridview, rowIndex, colIndex) {
            this.fireEvent('obdSimSellClick', gridview, colIndex, rowIndex);
          }
        },{
          getClass: function (v, meta, rec) {
            if (rec.get('return')) {
              this.items[6].text = '归还';
              return '';
            } else {
              this.items[6].text = '';
              return '';
            }
          },
          handler: function (gridview, rowIndex, colIndex) {
            this.fireEvent('obdReturnClick', gridview, colIndex, rowIndex);
          }
        },{
          getClass: function (v, meta, rec) {
            if (rec.get('log')) {
              this.items[7].text = '日志';
              return '';
            } else {
              this.items[7].text = '';
              return '';
            }
          },
          handler: function (gridview, rowIndex, colIndex) {
            this.fireEvent('obdSimLogClick', gridview, colIndex, rowIndex);
          }
        }
      ],
      editRenderer: function (value, metaData, record, rowIdx, colIdx, store, view) {
        return "";

      }
    }

  ],
  initComponent:function(){
    var _store = Ext.create('Ext.store.obdManager.ObdSimBindStore');
    _store.loadPage(1);
    var self = this;
    self.store = _store;
    self.bbar.store = _store;
//        this.callParent(arguments);
    Ext.create("Ext.utils.Common").ajax({
      url: 'obdManage.do?method=getOBDSimOperation',
      success: function (result) {
        if(result && result.success){
          var permission = result.data;
          var obdSimOperationToolbarItems = self.getComponent("obdSimOperationToolbar").items;
          if(permission && permission["import"]){
            obdSimOperationToolbarItems.get("importInventoryBtn").show();
            obdSimOperationToolbarItems.get("downOBDSimTemplateDownBtn").show();
          }else{
            obdSimOperationToolbarItems.get("importInventoryBtn").hide();
            obdSimOperationToolbarItems.get("downOBDSimTemplateDownBtn").hide();
          }
          if(permission && permission["edit"]){
            obdSimOperationToolbarItems.get("obdSimMultiEditBtn").show();
          }else{
            obdSimOperationToolbarItems.get("obdSimMultiEditBtn").hide();
          }
          if(permission && permission["outStorage"]){
            obdSimOperationToolbarItems.get("batchOutStorageBtn").show();
          }else{
            obdSimOperationToolbarItems.get("batchOutStorageBtn").hide();
          }
          if(permission && permission["sell"]){
            obdSimOperationToolbarItems.get("batchSaleBtn").show();
          }else{
            obdSimOperationToolbarItems.get("batchSaleBtn").hide();
          }

        }else{
        }
      },
      failure: function () {
      }
    });
    this.callParent(arguments);
  }

});
