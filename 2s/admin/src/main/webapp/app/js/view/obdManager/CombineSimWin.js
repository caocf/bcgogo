/**
 * OBD组装SIM
 */
Ext.define("Ext.view.obdManager.CombineSimWin", {
    extend: "Ext.window.Window",
    alias: "widget.CombineSimWin",
    layout: 'fit',
    width: 300,
    height: 150,
    collapsible: true,
    closeAction: 'hide',
    title: "组装SIM",
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
                    fieldLabel: "IMEI号",
                    xtype: "displayfield",
                    name:"displayImei"
                },
                {
                    fieldLabel: '手机号',
                    name: 'mobile',
                    xtype: 'combobox',
                    maxLength: 11,
                    valueField:'mobile',
                    displayField: 'mobile',
                    pageSize: 10,
                    minChars: 3,
                    triggerAction: "all",
                    queryParam: 'queryMobile',
                    queryMode: 'remote',
                    allowBlank: false,
                    store: Ext.create('Ext.store.obdManager.ObdImeiStore', {
                        proxy: {
                            type:'ajax',
                            api:{
                                read:'obdManage.do?method=getSimMobileSuggestion'
                            },
                            reader:{
                                root:'results',
                                type:'json',
                                totalProperty:"totals"
                            },
                            extraParams : {
                                scene : "SINGLE_SIM"
                            }
                        }
                    })
                },
                {
                    name: 'imei',
                    xtype: 'hiddenfield'
                }
            ],
            buttons: [
                {
                    text: '确定',
                    handler: function () {
                        var thisBtn = this;
                        var newValues = this.up("form").getForm().getValues();
                        if (!this.up("form").getForm().isValid()) {
                            return;
                        }
                        var mobile = newValues["mobile"];
                        var imei = newValues["imei"];
                        var dataView = this.up("CombineSimWin").getParentTargetWin().getObdSimBindView();
                        Ext.create("Ext.utils.Common").ajax({
                            url: 'obdManage.do?method=combineObdSim',
                            params: {mobile:mobile,imei:imei},
                            success: function (result) {
                                if(result && result.success){
                                    Ext.MessageBox.alert("提示", "组装成功");
                                    dataView.down("pagingtoolbar").doRefresh();
                                    thisBtn.up("CombineSimWin").close();
                                    Ext.create("Ext.utils.Common").unmask();
                                }else{
                                    var msg = Ext.isEmpty(result.msg)?"网络异常":result.msg;
                                    Ext.MessageBox.alert("组装失败", msg);
                                }
                            },
                            failure: function () {
                                Ext.MessageBox.alert("提示", "组装失败");
                            }
                        });
                    }
                },
                {
                    text: '清空',
                    handler: function () {
                        this.up("form").getForm().findField("mobile").setValue("");
                    }
                }
            ]
        }
    ],
    close: function () {
        this.down("form").getForm().findField("mobile").setValue("");
        Ext.create("Ext.utils.Common").unmask();
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