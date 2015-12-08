/**
 * OBD 出库
 */
Ext.define("Ext.view.obdManager.ObdSimReturnWin", {
    extend: "Ext.window.Window",
    alias: "widget.ObdSimReturnWin",
    layout: 'fit',
    width: 300,
    height: 250,
    collapsible: true,
    closeAction: 'hide',
    title: "OBD归还",
    items: [
        {
            xtype: 'form',
            frame: true,
            border: false,
            layout: 'form',
            items: [
                {
                    fieldLabel: '归还原因',
                    xtype: 'combobox',
                    allowBlank: false,
                    displayField: 'text',
                    valueField: 'value',
                    mode: 'local',
                    name: 'returnMessage',
                    store: Ext.create("Ext.data.SimpleStore", {
                        fields: ['value', 'text'],
                        data: [
                            ['RETURN_SAMPLE', '样品归还'],
                            ['CHANGE_DEFECTIVE', '残次品换货'],
                            ['RETURN_STORAGE', '退货']
                        ]
                    })

                },

                {
                    xtype: 'hiddenfield',
                    name: 'returnImei'
                },
                {
                    xtype: 'datefield',
                    allowBlank: false,
                    format: 'Y-m-d',
                    fieldLabel: '归还日期',
                    name: 'returnDate'
                },
                {
                    fieldLabel: "操作人",
                    xtype: "displayfield",
                    name: "returnOperationName"
                },
                {
                    fieldLabel: "操作日期",
                    xtype: "displayfield",
                    name: "returnOperationTime"
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
                        params["returnMsgStr"] = newValues["returnMessage"];
                        params["returnDateStr"] = newValues["returnDate"];
                        params["returnImei"] =  newValues["returnImei"];
                        var isValidate = true;
                        var message = "";
                        if(Ext.isEmpty(newValues["returnMessage"])){
                            isValidate = false;
                            message += "请选择归还原因！\r\n";
                        }

                        if(Ext.isEmpty(newValues["returnDate"])){
                            isValidate = false;
                            message += "请填写归还日期！\r\n";
                        }
                        if(!isValidate){
                            Ext.MessageBox.alert("归还失败", message);
                            return;
                        }


                        var dataView = this.up("ObdSimReturnWin").getParentTargetWin().getObdSimBindView();
                        Ext.create("Ext.utils.Common").ajax({
                            url: 'obdManage.do?method=obdSimReturn',
                            params: params,
                            success: function (result) {
                                if (result && result.success) {
                                    dataView.down("pagingtoolbar").doRefresh();
                                    thisBtn.up("ObdSimReturnWin").close();
                                    Ext.create("Ext.utils.Common").unmask();

                                    Ext.Msg.show({
                                        title:'提示',
                                        msg: '归还成功',
                                        buttons: Ext.Msg.YES
                                    });

                                } else {
                                    var msg = Ext.isEmpty(result.msg) ? "网络异常" : result.msg;
                                    Ext.MessageBox.alert("归还失败", msg);
                                }
                            },
                            failure: function () {
                                Ext.MessageBox.alert("归还失败", "网络异常");
                            }
                        });
                    }
                },
                {
                    text: '清空',
                    handler: function () {
                        var baseForm = this.up("form").getForm();
                        baseForm.findField("returnMessage").setValue("");
                        baseForm.findField("returnDate").setValue("");
                    }
                }
            ]
        }
    ],
    close: function () {
        Ext.create("Ext.utils.Common").unmask();
        var baseForm = this.down("form").getForm();
        baseForm.findField("returnMessage").setValue("");
        baseForm.findField("returnDate").setValue("");
        baseForm.findField("returnImei").setValue("");
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