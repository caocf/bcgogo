/**
 * OBD 出库
 */
Ext.define("Ext.view.obdManager.ObdSimOutStorageWin", {
    extend: "Ext.window.Window",
    alias: "widget.ObdSimOutStorageWin",
    layout: 'fit',
    width: 300,
    height: 370,
    collapsible: true,
    closeAction: 'hide',
    title: "OBD出库",
    items: [
        {
            xtype: 'form',
            frame: true,
            border: false,
            defaultType: 'textfield',
            layout: 'form',
            fieldDefaults: {
                labelWidth: 70,
                xtype: 'textfield',
                frame: true,
                width: 200,
                height: 20
            },
            items: [
                {
                    fieldLabel: "出库对象",
                    xtype: "radiogroup",
                    name: "outStorageTargetGroup",
                    items: [
                        {boxLabel: '店铺', name: 'outStorageTarget', inputValue: "SHOP", checked: true},
                        {boxLabel: '代理商', name: 'outStorageTarget', inputValue: "AGENT"},
                        {boxLabel: '员工', name: 'outStorageTarget', inputValue: "EMPLOYEE"}
                    ],
                    listeners:{
                        change:function(radiogroup,newVal,oldVal){
                            var outStorageTargetCombo = radiogroup.up("form").items.get("outStorageTargetCombo");
                            if(newVal["outStorageTarget"] =="SHOP"){
                                var store = Ext.create("Ext.store.obdManager.ObdShopNameSuggestion");
                                outStorageTargetCombo.reset();
                                store.loadPage(1);
                                outStorageTargetCombo.bindStore(store);
                            }else if(newVal["outStorageTarget"]=="AGENT"){
                                var store = Ext.create("Ext.store.obdManager.AgentNameSuggestion");
                                outStorageTargetCombo.reset();
                                store.loadPage(1);
                                outStorageTargetCombo.bindStore(store);
                            }else if(newVal["outStorageTarget"]=="EMPLOYEE"){
                                var store = Ext.create("Ext.store.obdManager.StaffNameSuggestion");
                                outStorageTargetCombo.reset();
                                store.loadPage(1);
                                outStorageTargetCombo.bindStore(store);
                            }
                        }
                    }

                },
                {
                    fieldLabel: '对象名称',
                    name: 'outStorageTargetId',
                    xtype: 'combobox',
                    id:"outStorageTargetCombo",
                    maxLength: 30,
                    valueField: 'idStr',
                    displayField: 'name',
                    pageSize: 10,
                    minChars: 1,
                    triggerAction: "all",
                    queryParam: 'queryWord',
                    allowBlank: false,
                    queryMode : 'remote',
                    store: Ext.create("Ext.store.obdManager.ObdShopNameSuggestion")
                },

                {
                    xtype: 'textarea',
                    allowBlank: false,
                    emptyText: '请输入设备IMIE号，每行输入一个！',
                    fieldLabel: '出库设备',
                    name: 'outStorageImei',
                    height:150
                },
                {
                    xtype: 'datefield',
                    allowBlank: false,
                    format: 'Y-m-d',
                    fieldLabel: '出库日期',
                    name: 'outStorageDate'
                },
                {
                    fieldLabel: "出库人",
                    xtype: "displayfield",
                    name: "outStorageOperationName"
                },
                {
                    fieldLabel: "操作时间",
                    xtype: "displayfield",
                    name: "outStorageOperationTime"
                }
            ],
            buttons: [
                {
                    text: '确定',
                    handler: function () {
                        var thisBtn = this;
                        var newValues = thisBtn.up("form").getForm().getValues();
                        if (!this.up("form").getForm().isValid()) {
                            return;
                        }
                        var params = {};
                        params["outStorageType"] = newValues["outStorageTarget"];
                        params["outStorageTargetIdStr"] = newValues["outStorageTargetId"];
                        params["outStorageDateStr"] = newValues["outStorageDate"];

                        var imeisStr = newValues["outStorageImei"];
                        imeisStr = imeisStr.replace(new RegExp("\r\n", "gm"), ",");
                        imeisStr = imeisStr.replace(new RegExp("\n", "gm"), ",");

                        var imeiArr = imeisStr.split(",");
                        var imeiNotEmptyArr = [];
                        for (var i = 0; i < imeiArr.length; i++) {
                            if (!Ext.isEmpty(imeiArr[i])) {
                                imeiNotEmptyArr.push(imeiArr[i]);
                            }
                        }

                        params["outStorageImeis"] = imeiNotEmptyArr;
                        var isValidate = true;
                        var message = "";
                        if(Ext.isEmpty(newValues["outStorageTarget"])){
                            isValidate = false;
                            message += "请选择出库对象！\r\n";
                        }
                        if(Ext.isEmpty(newValues["outStorageTargetId"])){
                            isValidate = false;
                            message += "请选择对象名称！\r\n";
                        }

                        if(!imeiNotEmptyArr || imeiNotEmptyArr.length == 0){
                            isValidate = false;
                            message += "请选择出库设备号！\r\n";
                        }

                        if(Ext.isEmpty(newValues["outStorageDate"])){
                            isValidate = false;
                            message += "请填写出库日期！\r\n";
                        }
                        if(!isValidate){
                            Ext.MessageBox.alert("出库失败", message);
                            return;
                        }


                        var dataView = this.up("ObdSimOutStorageWin").getParentTargetWin().getObdSimBindView();
                        Ext.create("Ext.utils.Common").ajax({
                            url: 'obdManage.do?method=obdSimOutStorage',
                            params: params,
                            success: function (result) {
                                if (result && result.success) {
                                    Ext.Msg.show({
                                        title:'提示',
                                        msg: '出库成功，是否需要继续出库?',
                                        buttons: Ext.Msg.YESNO,
                                        icon: Ext.Msg.QUESTION,
                                        fn:function(buttonId){
                                            if(buttonId == 'yes'){
                                                thisBtn.up("form").getForm().findField("outStorageImei").setValue("");
                                                dataView.down("pagingtoolbar").doRefresh();
                                            }else{
                                                dataView.down("pagingtoolbar").doRefresh();
                                                thisBtn.up("ObdSimOutStorageWin").close();
                                                Ext.create("Ext.utils.Common").unmask();
                                            }
                                        }
                                    });

                                } else {
                                    var msg = Ext.isEmpty(result.msg) ? "网络异常" : result.msg;
                                    Ext.MessageBox.alert("出库失败", msg);
                                }
                            },
                            failure: function () {
                                Ext.MessageBox.alert("出库失败", "网络异常");
                            }
                        });
                    }
                },
                {
                    text: '清空',
                    handler: function () {
                        var baseForm = this.up("form").getForm();
                        baseForm.findField("outStorageDate").setValue("");
                        baseForm.findField("outStorageTargetId").setValue("");
                        baseForm.findField("outStorageImei").setValue("");
                    }
                }
            ]
        }
    ],
    close: function () {
        Ext.create("Ext.utils.Common").unmask();
        var baseForm = this.down("form").getForm();
        baseForm.findField("outStorageDate").setValue("");
        baseForm.findField("outStorageTargetId").setValue("");
        baseForm.findField("outStorageImei").setValue("");
        this.doClose();
    },
    initComponent: function () {
        var self = this;

        self.callParent(arguments);
    },
    setParentTargetWin: function (parentTargetWin) {
        this.parentTargetWin = parentTargetWin;
    },

    getParentTargetWin: function () {
        return this.parentTargetWin;
    }
});