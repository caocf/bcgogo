Ext.define('Ext.controller.obdManager.OBDStorageManager', {
  extend: 'Ext.app.Controller',

  stores: [
    "Ext.store.obdManager.ObdSimBindStore"
  ],

  models: [
    "Ext.model.obdManager.ObdSimBindModel"
  ],

  views: [
    "Ext.view.obdManager.ObdSimBindView"
  ],

  requires: [
    "Ext.utils.ComponentUtils",
    "Ext.utils.PermissionUtils",
    "Ext.utils.Common",
    "Ext.ux.date.MonthField"
  ],
  uploadObdInventoryWin: Ext.create("Ext.view.obdManager.ObdImportInventoryWin"),
  obdSimLogWin: Ext.create("Ext.view.obdManager.ObdSimOperationLogWin"),
  obdSimMultiEditWin: Ext.create("Ext.view.obdManager.ObdSimMultiEditWin"),
  combineSimWin: Ext.create("Ext.view.obdManager.CombineSimWin"),
  combineObdWin: Ext.create("Ext.view.obdManager.CombineObdWin"),
  obdSimOutStorageWin: Ext.create("Ext.view.obdManager.ObdSimOutStorageWin"),
  obdSimSellWin: Ext.create("Ext.view.obdManager.ObdSimSellWin"),
  obdSimReturnWin: Ext.create("Ext.view.obdManager.ObdSimReturnWin"),
  refs:[
    {ref: 'ObdSimBindView', selector: 'ObdSimBindView'}
  ],
  init:function(){

    this.commonUtils = Ext.create("Ext.utils.Common");
    var self = this;
    self.control({
      "ObdSimBindView button[action=search]" : {
        click : function() {
          self.onSearch();
        }
      },
      "ObdSimBindView button[action=reset]" : {
        click : function() {
          self.resetSearch();
        }
      },
      "ObdSimBindView button[action=downOBDSimTemplateDown]" : {
        click : function(btn) {
          window.open("download.do?method=downloadStaticFile&relativePath=downloadFile/excel/BCGOGO_OBDSIM导入.xls&fileName=BCGOGO_OBDSIM导入");
        }
      },
      "ObdSimBindView button[action=importInventory]" : {
        click : function(btn) {
          self.commonUtils.mask();
          self.uploadObdInventoryWin.setParentTargetWin(self);
          self.uploadObdInventoryWin.show();
        }
      },
      "ObdSimBindView button[action=obdSimMultiEdit]": {
        click: function (btn) {
          var view = this.getObdSimBindView();
          var selections = view.getSelectionModel().getSelection();
          if (!selections || selections.length == 0) {
            Ext.MessageBox.alert("批量编辑", "请选择需要编辑的OBD、SIM、后视镜！")
          } else {
            self.commonUtils.mask();
            self.obdSimMultiEditWin.setParentTargetWin(self);
            self.obdSimMultiEditWin.show();
          }
        }
      },
      "ObdSimBindView actiontextcolumn#obdManagerOperation" :{
        editSingleObdSimClick: self.editSingleObdSim,
        obdSimLogClick: self.showObdSimLog,
        splitObdSimClick: self.splitObdSim,
        combineClick: self.combineObdSim,
        deleteObdSimClick: self.deleteObdSim,
        outStorageClick: self.outStorage,
        obdSimSellClick: self.obdSimSell,
        obdReturnClick: self.obdReturn
      },
      "ObdSimBindView toolbar combobox":{
        select:function(){
          self.onSearch();
        }
      },
      "ObdSimBindView toolbar checkboxgroup":{
        change:function(){
          self.onSearch();
        }
      },
      "ObdSimBindView button[action=batchOutStorage]": {
        click: function (btn) {
          self.commonUtils.mask();
          self.obdSimOutStorageWin.setParentTargetWin(self);
          var baseForm =self.obdSimOutStorageWin.down("form").getForm();

          baseForm.findField("outStorageDate").setValue(Ext.Date.format(new Date(), 'Y-m-d'));
          baseForm.findField("outStorageOperationTime").setValue(Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
          baseForm.findField("outStorageOperationName").setValue(Ext.getDom("userNameForHeader").value);
          var selections = self.getObdSimBindView().getSelectionModel().getSelection();
          var selectImeis = '';
          if(selections && selections.length >0){
            for(var i=0;i<selections.length;i++){
              if(!Ext.isEmpty(selections[i].get("imei"))){
                selectImeis += selections[i].get("imei")+"\r\n";
              }
            }
          }
          baseForm.findField("outStorageImei").setValue(selectImeis);
          self.obdSimOutStorageWin.show();
        }
      },
      "ObdSimBindView button[action=batchSale]": {
        click: function (btn) {
          self.commonUtils.mask();
          self.obdSimSellWin.setParentTargetWin(self);
          var baseForm =self.obdSimSellWin.down("form").getForm();

          baseForm.findField("outStorageDate").setValue(Ext.Date.format(new Date(), 'Y-m-d'));
          baseForm.findField("outStorageOperationTime").setValue(Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
          baseForm.findField("outStorageOperationName").setValue(Ext.getDom("userNameForHeader").value);
          var selections = self.getObdSimBindView().getSelectionModel().getSelection();
          var selectImeis = '';
          if(selections && selections.length >0){
            for(var i=0;i<selections.length;i++){
              if(!Ext.isEmpty(selections[i].get("imei"))){
                selectImeis += selections[i].get("imei")+"\r\n";
              }
            }
          }
          baseForm.findField("outStorageImei").setValue(selectImeis);
          self.obdSimSellWin.show();
        }
      }
    });

  },
  onSearch: function () {

    var self = this.getObdSimBindView();
    var imei = self.down("[name=imei]").getValue();
    var mobile = self.down("[name=mobile]").getValue();
    var startUserDateStr = self.down("[name=startUserDateStr]").getValue();
    var endUserDateStr = self.down("[name=endUserDateStr]").getValue();
    var ownerName = self.down("[name=ownerName]").getValue();
    var obdSimTypeStrArr = self.down("[name=obdSimTypeStrArr]").getValue()["obdSimTypeStr"];
    var obdVersion = self.down("[name=obdVersion]").getValue();
    var obdSimStatusStrArr = self.down("[name=obdSimStatusStrArr]").getValue()["obdSimStatusStr"];
    var params = {
      imei:imei,
      mobile:mobile,
      startUserDateStr:startUserDateStr,
      endUserDateStr:endUserDateStr,
      ownerName:ownerName,
      obdSimTypeStrArr:obdSimTypeStrArr,
      obdVersion:obdVersion,
      obdSimStatusStrArr:obdSimStatusStrArr
    };
    self.store.proxy.extraParams = params;
    self.store.loadPage(1);
  },
  resetSearch: function () {
    var self = this.getObdSimBindView();
    self.down("[name=imei]").setValue(null);
    self.down("[name=mobile]").setValue(null);
    self.down("[name=startUserDateStr]").setValue(null);
    self.down("[name=endUserDateStr]").setValue(null);
    self.down("[name=ownerName]").getValue();
    self.down("[name=obdSimTypeStrArr]").setValue(null);
    self.down("[name=obdVersion]").setValue(null);
    self.down("[name=obdSimStatusStrArr]").setValue(null);
  },
  editSingleObdSim:function(grid, cell, row){
    var rowEditing = this.getObdSimBindView().editingPlugin;
    rowEditing.startEdit(row,row);
  },
  showObdSimLog:function(grid, cell, row){
    var self = this;
    var data = grid.getStore().getRange(row,row)[0];
    var param = {};
    if(data.get('simIdStr')){
      param['simId'] = data.get('simIdStr');
    }
    if(data.get('obdIdStr')){
      param['obdId'] = data.get('obdIdStr');
    }
    self.commonUtils.mask();
    var _grid = self.obdSimLogWin.down("grid");
    _grid.store.proxy.extraParams = param;
    _grid.store.loadPage(1);

    self.obdSimLogWin.show();
  },
  splitObdSim:function(grid, cell, row){
    var self = this;
    var data = grid.getStore().getRange(row,row)[0];
    var params = {};
    if(data.get('simIdStr')){
      params['simId'] = data.get('simIdStr');
    }
    if(data.get('obdIdStr')){
      params['obdId'] = data.get('obdIdStr');
    }
    Ext.Msg.show({
      title:'提示',
      msg: '确认是否要拆分?',
      buttons: Ext.Msg.YESNO,
      icon: Ext.Msg.QUESTION,
      fn:function(buttonId){
        if(buttonId == 'yes'){
          Ext.create("Ext.utils.Common").ajax({
            url: 'obdManage.do?method=splitObdSim',
            params: params,
            success: function (result) {
              if(result && result.success){
                Ext.MessageBox.alert("提示", "拆分成功");
                self.getObdSimBindView().down("pagingtoolbar").doRefresh();
              }else{
                var msg = Ext.isEmpty(result.msg)?"网络异常":result.msg;
                Ext.MessageBox.alert("拆分失败", msg);
              }
            },
            failure: function () {
              Ext.MessageBox.alert("提示", "拆分失败");
            }
          });
        }
      }
    });
  },
  deleteObdSim:function(grid, cell, row){
    var self = this;
    var data = grid.getStore().getRange(row,row)[0];
    var params = {};
    if(data.get('imei')){
      params['imei'] = data.get('imei');
    }
    if(data.get('mobile')){
      params['mobile'] = data.get('mobile');
    }
    Ext.Msg.show({
      title:'提示',
      msg: '确认是否要删除?',
      buttons: Ext.Msg.YESNO,
      icon: Ext.Msg.QUESTION,
      fn:function(buttonId){
        if(buttonId == 'yes'){
          Ext.create("Ext.utils.Common").ajax({
            url: 'obdManage.do?method=deleteObdSim',
            params: params,
            success: function (result) {
              if(result && result.success){
                Ext.MessageBox.alert("提示", "删除成功！");
                self.getObdSimBindView().down("pagingtoolbar").doRefresh();
              }else{
                var msg = Ext.isEmpty(result.msg)?"网络异常":result.msg;
                Ext.MessageBox.alert("删除失败", msg);
              }
            },
            failure: function () {
              Ext.MessageBox.alert("提示", "删除失败");
            }
          });
        }
      }
    });
  },
  combineObdSim:function(grid, cell, row){
    var self = this;
    var data = grid.getStore().getRange(row,row)[0];
    var imei = data.get("imei");
    var mobile = data.get("mobile");
    var obdSimType = data.get("obdSimType");
    self.combineSimWin.setParentTargetWin(self);
    self.combineObdWin.setParentTargetWin(self);
    if(obdSimType && obdSimType=="SINGLE_GSM_OBD"){
      self.commonUtils.mask();
      var form = self.combineSimWin.down("form").getForm();
      form.findField("imei").setValue(imei);
      form.findField("displayImei").setValue(imei);
      self.combineObdWin.hide();
      self.combineSimWin.show();
    }else if(obdSimType && obdSimType=="SINGLE_SIM"){
      self.commonUtils.mask();
      var form = self.combineObdWin.down("form").getForm();
      form.findField("mobile").setValue(mobile);
      form.findField("displayMobile").setValue(mobile);
      self.combineSimWin.hide();
      self.combineObdWin.show();
    } else if(obdSimType && obdSimType=="SINGLE_MIRROR_OBD"){
      self.commonUtils.mask();
      var form = self.combineSimWin.down("form").getForm();
      form.findField("imei").setValue(imei);
      form.findField("displayImei").setValue(imei);
      self.combineObdWin.hide();
      self.combineSimWin.show();
    }

  },
  outStorage:function(grid, cell, row){
    var self = this;
    var data = grid.getStore().getRange(row,row)[0];
    var imei = data.get("imei");
    self.commonUtils.mask();
    self.obdSimOutStorageWin.setParentTargetWin(self);
    var baseForm =self.obdSimOutStorageWin.down("form").getForm();
    baseForm.findField("outStorageDate").setValue(Ext.Date.format(new Date(), 'Y-m-d'));
    baseForm.findField("outStorageOperationTime").setValue(Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
    baseForm.findField("outStorageOperationName").setValue(Ext.getDom("userNameForHeader").value);
    baseForm.findField("outStorageImei").setValue(imei);
    self.obdSimOutStorageWin.show();
  },
  obdSimSell:function(grid, cell, row){
    var self = this;
    var data = grid.getStore().getRange(row,row)[0];
    var imei = data.get("imei");
    self.commonUtils.mask();
    self.obdSimSellWin.setParentTargetWin(self);
    var baseForm =self.obdSimSellWin.down("form").getForm();
    baseForm.findField("outStorageDate").setValue(Ext.Date.format(new Date(), 'Y-m-d'));
    baseForm.findField("outStorageOperationTime").setValue(Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
    baseForm.findField("outStorageOperationName").setValue(Ext.getDom("userNameForHeader").value);
    baseForm.findField("outStorageImei").setValue(imei);
    self.obdSimSellWin.show();
  },
  obdReturn:function(grid, cell, row){
    var self = this;
    var data = grid.getStore().getRange(row,row)[0];
    var imei = data.get("imei");
    self.commonUtils.mask();
    self.obdSimReturnWin.setParentTargetWin(self);
    var baseForm =self.obdSimReturnWin.down("form").getForm();
    baseForm.findField("returnDate").setValue(Ext.Date.format(new Date(), 'Y-m-d'));
    baseForm.findField("returnOperationTime").setValue(Ext.Date.format(new Date(), 'Y-m-d'));
    baseForm.findField("returnOperationName").setValue(Ext.getDom("userNameForHeader").value);
    baseForm.findField("returnImei").setValue(imei);
    self.obdSimReturnWin.show();
  }


});