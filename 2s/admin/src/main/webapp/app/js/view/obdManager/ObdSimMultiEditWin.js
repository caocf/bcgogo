/**
 * obd 批量编辑
 */
Ext.define("Ext.view.obdManager.ObdSimMultiEditWin", {
    extend: "Ext.window.Window",
    alias: "widget.ObdSimMultiEditWin",
    layout: 'fit',
    width: 300,
    height: 300,
    collapsible: true,
    closeAction: 'hide',
    title: "批量编辑",
    requires: [
        'Ext.ux.date.MonthField'
    ],
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
                    fieldLabel: "<span style='color: red'>友情提示</span>",
                    labelStyle:"text-align:right",
                    xtype: "displayfield",
                    value: "<span style='color: red'>不输入内容的项目不做批量修改！</span>",
                    labelSeparator:"<span style='color: red'>:</span>"
                },
                {
                    fieldLabel: '软件版本',
                    name: 'obdVersion',
                    maxLength: 30
                },
                {
                    fieldLabel: '规格',
                    name: 'spec',
                    maxLength: 30
                },
                {
                    fieldLabel: '颜色',
                    name: 'color',
                    maxLength: 30
                },
                {
                    fieldLabel: '包装',
                    name: 'pack',
                    maxLength: 30
                } ,
                {
                    fieldLabel: '碰撞报警',
                    xtype: 'combobox',
                    forceSelection: true,
                    mode: 'local',
                    name: 'openCrash',
                    store: Ext.create("Ext.data.SimpleStore", {
                        fields: ['value', 'text'],
                        data: [
                            ['YES', 'YES'],
                            ['NO', 'NO']
                        ]
                    })

                },
                {
                    fieldLabel: '震动报警',
                    xtype: 'combobox',
                    forceSelection: true,
                    mode: 'local',
                    name: 'openShake',
                    store: Ext.create("Ext.data.SimpleStore", {
                        fields: ['value', 'text'],
                        data: [
                            ['YES', 'YES'],
                            ['NO', 'NO']
                        ]
                    })
                },
                {
                    xtype: 'monthfield',
                    format: 'Y-m',
                    fieldLabel: '开通年月',
                    name: 'useDateStr'
                },
                {
                    xtype: 'numberfield',
                    fieldLabel: '服务期(年)',
                    name: 'usePeriod',
                    allowDecimals: false,//不允许输入小数
                    allowNegative: false,//不允许输入负数
                    nanText: '请输入有效的整数'//无效数字提示
                }
            ],
            buttons: [
                {
                    text: '确定',
                    action: 'saveMultiObdSimUpdate',
                    handler: function () {
                        var thisBtn = this;
                        var newValues = this.up("form").getForm().getValues();
                        if (!this.up("form").getForm().isValid()) {
                            return;
                        }
                        var dataView = this.up("ObdSimMultiEditWin").getParentTargetWin().getObdSimBindView();
                        var selections = dataView.getSelectionModel().getSelection();
                        var isAllEmpty = true;
                        var validateMsg = "";
                        var isValidate = true;
                        var params = {};
                        if (!selections || selections.length == 0) {
                            isValidate = false;
                            validateMsg += "请选择需要编辑的OBD SIM 信息！<br>";
                        }
                        if (!Ext.isEmpty(newValues["color"])) {
                            isAllEmpty = false;
                            params["newObdSimBindDTO.color"] = newValues["color"];
                        }
                        if (!Ext.isEmpty(newValues["obdVersion"])) {
                            isAllEmpty = false;
                            params["newObdSimBindDTO.obdVersion"] = newValues["obdVersion"];
                        }
                        if (!Ext.isEmpty(newValues["pack"])) {
                            isAllEmpty = false;
                            params["newObdSimBindDTO.pack"] = newValues["pack"];
                        }
                        if (!Ext.isEmpty(newValues["spec"])) {
                            isAllEmpty = false;
                            params["newObdSimBindDTO.spec"] = newValues["spec"];
                        }
                        if (!Ext.isEmpty(newValues["useDateStr"])) {
                            isAllEmpty = false;
                            var useDateArr = newValues["useDateStr"].split("-");
                            if (useDateArr != null && useDateArr.length == 2
                                && Ext.num(useDateArr[0])&& useDateArr[0].length == 4
                                && Ext.num(useDateArr[1]) && useDateArr[1].length == 2
                                && Ext.num(useDateArr[1]) >= 1 && Ext.num(useDateArr[1]) <= 12) {
                                params["newObdSimBindDTO.useDateStr"] = newValues["useDateStr"];
                            } else {
                                isValidate = false;
                                validateMsg += "请填写正确的开通年月！<br>";
                            }
                        }
                        if (!Ext.isEmpty(newValues["usePeriod"])) {
                            isAllEmpty = false;
                            if (newValues["usePeriod"] <= 0) {
                                isValidate = false;
                                validateMsg += "请填写正确的服务期！<br>";
                            } else {
                                params["newObdSimBindDTO.usePeriod"] = newValues["usePeriod"];
                            }
                        }
                        if (!Ext.isEmpty(newValues["openCrash"])) {
                            isAllEmpty = false;
                            params["newObdSimBindDTO.openCrash"] = newValues["openCrash"];
                        }
                        if (!Ext.isEmpty(newValues["openShake"])) {
                            isAllEmpty = false;
                            params["newObdSimBindDTO.openShake"] = newValues["openShake"];
                        }
                        if (isAllEmpty) {
                            isValidate = false;
                            validateMsg += "请填写需要编辑的内容！<br>"
                        }
                        if (!isValidate) {
                            Ext.MessageBox.alert("提示", validateMsg);
                        } else {
                            for (var i = 0; i < selections.length; i++) {
                                var obdIdStr = selections[i].data['obdIdStr'];
                                var simIdStr = selections[i].data['simIdStr'];
                                if (!Ext.isEmpty(obdIdStr)) {
                                    params['toUpdateObdSimBindDTO[' + i + '].obdId'] = obdIdStr;
                                }
                                if (!Ext.isEmpty(simIdStr)) {
                                    params['toUpdateObdSimBindDTO[' + i + '].simId'] = simIdStr;
                                }
                            }
                            Ext.create("Ext.utils.Common").ajax({
                                url: 'obdManage.do?method=updateMultiObdSim',
                                params: params,
                                success: function (result) {
                                    if(result && result.success){
                                        Ext.MessageBox.alert("提示", "更新成功");
                                        dataView.down("pagingtoolbar").doRefresh();
                                        thisBtn.up("ObdSimMultiEditWin").close();
                                    }else{
                                        var msg = Ext.isEmpty(result.msg)?"网络异常":result.msg;
                                        Ext.MessageBox.alert("更新失败", msg);
                                    }
                                },
                                failure: function () {
                                    Ext.MessageBox.alert("提示", "更新失败");
                                }
                            });
                        }
                    }
                },
                {
                    text: '清空',
                    action: 'cancelMultiObdSimUpdate',
                    handler: function () {
                        this.up("ObdSimMultiEditWin").down("form").getForm().reset();
                    }
                }
            ]
        }
    ],
    close: function () {
        Ext.create("Ext.utils.Common").unmask();
        this.down("form").getForm().reset();
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