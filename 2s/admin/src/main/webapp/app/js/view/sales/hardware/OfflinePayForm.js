Ext.define('Ext.view.sales.hardware.OfflinePayForm', {
    extend: 'Ext.form.Panel',
    bodyPadding: 5,
    width: 478,
    alias: 'widget.hardwareOfflinePayForm',
    layout: 'anchor',
    defaults: {
        anchor: '100%'
    },
    fieldDefaults: {
        labelWidth: 125,
        msgTarget: 'side',
        autoFitErrors: false
    },
    buttons: [
        {
            text: '付款',
            action: 'save'
        }
    ],
    requires: [
        "Ext.view.sys.user.UserSelect",
        "Ext.view.finance.payment.PaymentMethod",
        "Ext.view.customerMange.existingCustomerManage.Select"
    ],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        me.addEvents('create');
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
                            margin: '0 20 0 0',
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
                            fieldLabel: '付款情况',
                            labelAlign:'right',
                            labelWidth: 60,
                            margin: '0 20 0 0',
                            width: 230,
                            value: '未支付'
                        } ,
                        {
                            xtype: 'displayfield',
                            fieldLabel: '支付类型',
                            labelAlign:'right',
                            labelWidth: 60,
                            width: 230,
                            margin: '0 20 0 20',
                            name: 'paymentType'
                        }
                    ]
                },
                {
                    xtype: 'fieldset',
                    title: '本次支付内容',
                    layout: 'anchor',
                    margin: "10 0 0 0",
                    collapsible: false,
                    collapsed: false,
                    items: [
                        {
                            xtype:'userSelect',
                            name:'payeeId',
                            displayField:'name',
                            valueField:'id',
                            labelWidth: 70,
                            fieldLabel:'收款人',
                            labelAlign:'right',
                            allowBlank:false,
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
                        },
                        {
                            xtype: 'textfield',
                            fieldLabel: '收取金额',
                            labelAlign:'right',
                            name: 'paidAmount',
                            readOnly: true,
                            labelWidth: 70
                        },
                        {
                            fieldLabel: '收取时间',
                            labelAlign:'right',
                            xtype: "datefield",
                            name: 'paymentTime',
                            msgTarget: 'under',
                            format: 'Y-m-d H:i',
//                            maxValue: new Date(),
                            labelWidth: 70,
                            allowBlank: false,
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
                            name: 'bcgogoReceivableOrderId',
                            xtype: "hiddenfield"
                        },
                        {
                            name: 'totalAmount',
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
        if (baseForm.isValid()) {
            form.mask('正在保存 . . .');
            var params = baseForm.getValues();
            if (!params['paymentTime']) {
                Ext.Msg.alert('返回结果', "保存失败！", function () {
                    form.unmask();
                });
                return;
            }
            params['paymentTime'] = params['paymentTime'] ? Date.parse(params['paymentTime'].replace(/-/g, "/")) : "";
            params['paymentMethod'] = "DOOR_CHARGE";
            form.commonUtils.ajax({
                url: 'bcgogoReceivable.do?method=hardwareOfflineReceivable',
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
    }
});