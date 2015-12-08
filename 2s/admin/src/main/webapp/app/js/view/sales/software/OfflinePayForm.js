Ext.define('Ext.view.sales.software.OfflinePayForm', {
    extend: 'Ext.form.Panel',
    bodyPadding: 5,
    width: 600,
    alias: 'widget.softwareOfflinePayForm',
    layout: 'anchor',
    defaults: {
        anchor: '100%'
    },
    fieldDefaults: {
        labelWidth: 125,
        msgTarget: 'under ',
        autoFitErrors: false
    },

    buttons: [
        {
            text: '保存',
            action: 'save'
        }
    ],
    requires: [
        "Ext.view.sys.user.UserSelect",
        "Ext.view.sales.InstalmentPlanAlgorithmSelect",
        "Ext.view.customerMange.existingCustomerManage.Select"
    ],
    initComponent: function () {
        var me = this,
            userSelect = Ext.widget("userSelect", {
                typeAhead: true,
                triggerAction: 'all',
                lazyRender: true,
                fieldLabel: '收取人',
                labelAlign:'right',
                labelWidth: 70,
                name: 'payeeId',
                xtype: "userSelect",
                valueField: 'id',
                msgTarget: 'qtip',
//                readOnly:true,
                allowBlank: false,
                listeners: {
                    blur: function (comp, e, eOpts) {
                        comp.blurFn(comp, me,
                            function () {
                                me.down("button[action=save]").enable();
                            },
                            function () {
                                Ext.Msg.alert('返回结果', " 您输入的销售人员名不正确！", function () {
                                    me.down("button[action=save]").disable();
                                });
                            });
                    }
                }
            });
        userSelect.store.proxy.extraParams = {
            operateScene: ""
        };
        me.commonUtils = Ext.create("Ext.utils.Common");
        me.addEvents('create');

        var instalmentPlanAlgorithmPanel = Ext.create('Ext.view.View', {
            name: 'instalmentPlanAlgorithmId',
            hidden: true
        });
        Ext.apply(me, {
            items: [
                {
                    xtype: 'fieldset',
                    layout: 'hbox',
                    anchor: '100%',
                    padding: 0,
                    margin: '10 0 5 0',
                    border: false,
                    items: [
                        {
                            xtype: 'displayfield',
                            fieldLabel: '店铺名',
                            labelAlign:'right',
                            labelWidth: 60,
                            width: 400,
                            margin: '0 10 0 0',
                            name: 'shopName'
                        }
                    ]
                },
                {
                    xtype: 'fieldset',
                    layout: 'hbox',
                    anchor: '100%',
                    padding: 0,
                    margin: '10 0 5 0',
                    border: false,
                    items: [
                        {
                            xtype: 'displayfield',
                            fieldLabel: '支付类型',
                            labelAlign:'right',
                            labelWidth: 60,
                            width: 250,
                            margin: '0 10 0 0',
                            name: 'paymentType',
                            renderer: function (val, style, rec, index) {
                                return val == 'HARDWARE' ? "硬件购买费用" : "软件购买费用";
                            }
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '软件版本',
                            labelAlign:'right',
                            labelWidth: 60,
                            width: 200,
                            name: 'shopVersion'
                        }
                    ]
                },
                {
                    xtype: 'fieldset',
                    layout: 'hbox',
                    anchor: '100%',
                    padding: 0,
                    margin: '10 0 5 0',
                    border: false,
                    items: [
                        {
                            xtype: 'displayfield',
                            fieldLabel: '总价',
                            labelAlign:'right',
                            labelWidth: 60,
                            width: 250,
                            margin: '0 10 0 0',
                            name: 'totalAmount'
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '付款情况',
                            labelAlign:'right',
                            labelWidth: 60,
                            width: 250,
                            margin: '0 10 0 0',
                            value: '试用期未付款'
                        }
                    ]
                },
                {
                    xtype: 'fieldset',
                    layout: 'hbox',
                    anchor: '100%',
                    padding: 0,
                    margin: '10 0 5 0',
                    border: false,
                    items: [
                        {
                            xtype: 'radiogroup',
                            fieldLabel: '付款方式',
                            labelAlign:'right',
                            margin: '0 10 0 0',
                            labelWidth: 60,
                            width: 250,
                            items: [
                                {boxLabel: '全额付款', name: "receivableMethod", inputValue: "FULL", checked: true},
                                {boxLabel: '分期付款', name: "receivableMethod", inputValue: "INSTALLMENT"}
                            ],
                            listeners: {
                                change: function (cmp, newValue, oldValue, eOpts) {
                                    var amount = me.down("[name=totalAmount]").getValue();
                                    //分期付款
                                    if (newValue['receivableMethod'] == "INSTALLMENT") {
                                        me.down("instalmentPlanAlgorithmSelect").show();
                                        me.down("[name=paidAmount]").setValue(null);
                                        me.down("[name=paidAmount]").setReadOnly(false);
                                    } else if (newValue['receivableMethod'] == "FULL") {
                                        //全额付款
                                        me.down("instalmentPlanAlgorithmSelect").hide();
                                        me.down("instalmentPlanAlgorithmSelect").setValue(null);
                                        instalmentPlanAlgorithmPanel.hide();
                                        //收取金额
                                        me.down("[name=paidAmount]").setValue(amount);
                                        me.down("[name=paidAmount]").setReadOnly(true);
                                    }
                                    me.down("[name=paidAmount]").firstAmount = undefined;
                                }
                            }
                        },
                        {
                            fieldLabel: '分期期数',
                            labelAlign:'right',
                            labelWidth: 60,
                            width: 150,
                            name: 'instalmentPlanAlgorithmId',
                            xtype: "instalmentPlanAlgorithmSelect",
//                            msgTarget: 'qtip',
//                            allowBlank: false,
                            hidden: true,
                            listeners: {
                                select: function (combo, records, eOpts) {
                                    var amount = me.down("[name=totalAmount]").getValue(),
                                        radios = records[0].get("terminallyRatio").split(","), radiosArray = [radios.length];
                                    for (var i = 0, max = radios.length; i < max; i++) {
                                        radiosArray[i] = {radio: radios[i]}
                                    }
                                    var firstAmount = (Number(amount) * Number(radiosArray[0].radio)).toFixed(0);
                                    me.down("[name=paidAmount]").setValue(firstAmount);
                                    me.down("[name=paidAmount]").firstAmount = firstAmount;
                                    instalmentPlanAlgorithmPanel.store = Ext.create('Ext.data.Store', {
                                        fields: ['radio'],
                                        data: radiosArray
                                    });
                                    instalmentPlanAlgorithmPanel.tpl = Ext.create('Ext.XTemplate',
                                        '<table cellpadding="0" cellspacing="0" class="software_receivable_form_table">',
                                        '<tr>',
                                        '<td style="width:70px;"><table cellpadding="0" cellspacing="0"><tr><td>期数</td></tr><tr><td>支付比例</td></tr><tr><td>应付金额</td></tr></table></td>',
                                        '<tpl for="."><td><table cellpadding="0" cellspacing="0"><tr><td>{#}期</td></tr><tr><td>{radio:this.radio()}</td></tr><tr><td>{radio:this.amount()}</td></tr></table></td></tpl>',
                                        '</tr>',
                                        '<tr><td>合计</td><td colspan="' + radios.length + '">' + amount + '</td></tr>',
                                        '</table>', {
                                            amount: function (radio) {
                                                return Number(amount * Number(radio)).toFixed(0);
                                            },
                                            radio: function (radio) {
                                                return Number(radio) * 100 + "%";
                                            }
                                        });
                                    instalmentPlanAlgorithmPanel.refresh();
                                    instalmentPlanAlgorithmPanel.show();
                                }
                            }
                        }
                    ]
                },
                instalmentPlanAlgorithmPanel,
                {
                    xtype: 'fieldset',
                    title: '本次支付内容',
                    layout: 'anchor',
                    margin: "10 0 0 0",
                    collapsible: false,
                    collapsed: false,
                    items: [
                        userSelect,
                        {
                            xtype: 'textfield',
                            fieldLabel: '收取金额',
                            labelAlign:'right',
                            name: 'paidAmount',
                            vtype: 'money',
                            msgTarget: 'qtip',
                            allowBlank: false,
                            readOnly: true,
                            labelWidth: 70,
                            listeners: {
                                blur: function () {
                                    var receivableMethod = me.down("[name=receivableMethod][checked=true]").getSubmitData()['receivableMethod'];
                                    if (receivableMethod == "INSTALLMENT") {
                                        var amount = me.down("[name=totalAmount]").getValue();
                                        var paidAmount = me.down("[name=paidAmount]").getValue();
                                        if (Number(amount) <= Number(paidAmount)) {
                                            //全额付款
                                            me.down("instalmentPlanAlgorithmSelect").hide();
                                            me.down("instalmentPlanAlgorithmSelect").setValue(null);
                                            instalmentPlanAlgorithmPanel.hide();
                                            //收取金额
                                            me.down("[name=paidAmount]").setValue(amount);
                                            me.down("[name=paidAmount]").setReadOnly(true);
                                            me.down("[name=receivableMethod]").setValue("FULL");
                                        }
                                    }
                                }
                            }
                        },
                        {
                            fieldLabel: '收取时间',
                            labelAlign:'right',
                            xtype: "datefield",
                            name: 'paymentTime',
                            msgTarget: 'qtip',
                            format: 'Y-m-d H:i',
                            labelWidth: 70,
                            allowBlank: false ,
                            listeners: {
                                beforerender: function (comp, e, eOpts) {
                                    comp.setMaxValue(new Date(new Date().getTime() + 24 * 60 * 60 * 1000));
                                    comp.setValue(new Date());
                                }
                            }
                        },
                        {
                            name: 'bcgogoReceivableOrderRecordRelationId',
                            xtype: "hiddenfield"
                        },
                        {
                            name: 'orderId',
                            xtype: "hiddenfield"
                        }
                    ]
                }
            ]
        });
        this.callParent();
    },
    save: function (form, callback) {
        var baseForm = form.form;
        if (baseForm.isValid() && form.validate(form)) {
            form.mask('正在保存 . . .');
            var params = baseForm.getValues();
            params['paymentTime'] = params['paymentTime'] ? Date.parse(params['paymentTime'].replace(/-/g, "/")) : "";
            params['totalAmount'] = form.down("[name=totalAmount]").getValue();
            params['paymentMethod'] = "DOOR_CHARGE";
            form.commonUtils.ajax({
                url: 'bcgogoReceivable.do?method=softwareOfflineReceivable',
                params: params,
                success: function (result) {
                    Ext.Msg.alert('返回结果', "保存成功！", function () {
                        baseForm.reset();
                        callback();
                    });
                },
                failure: function () {
                    form.unmask();
                }
            });
        }
    },
    validate: function (form) {
        var baseForm = form.form,
            params = baseForm.getValues(),
            paidAmount = form.down("[name=paidAmount]");
        if (!params['paymentTime']) {
            Ext.MessageBox.show({
                title: '提示',
                msg: '保存失败，收取时间不能为空！!',
                buttons: Ext.MessageBox.OK,
                icon: Ext.MessageBox.WARNING
            });
            return false;
        }
        if (form.down("radiogroup").getValue()['receivableMethod'] == "INSTALLMENT") {
            if (paidAmount.firstAmount > Number(paidAmount.getValue())) {
                Ext.MessageBox.show({
                    title: '提示',
                    msg: '首次支付不得小于￥' + paidAmount.firstAmount + '！',
                    buttons: Ext.MessageBox.OK,
                    fn: function () {
                        paidAmount.setValue(paidAmount.firstAmount);
                    },
                    icon: Ext.MessageBox.WARNING
                });
                return false;
            }
            if (!params['instalmentPlanAlgorithmId']) {
                Ext.MessageBox.show({
                    title: '提示',
                    msg: '请选择分期期数！',
                    buttons: Ext.MessageBox.OK,
                    icon: Ext.MessageBox.WARNING
                });
                return false;
            }
        } else {
            var totalAmount = form.down("[name=totalAmount]").getValue();
            if (Number(totalAmount) < Number(paidAmount.getValue())) {
                Ext.MessageBox.show({
                    title: '提示',
                    msg: '全额支付不得大于￥' + totalAmount + '！',
                    buttons: Ext.MessageBox.OK,
                    fn: function () {
                        paidAmount.setValue(totalAmount);
                    },
                    icon: Ext.MessageBox.WARNING
                });
                return false;
            }
        }
        return true;
    }
});