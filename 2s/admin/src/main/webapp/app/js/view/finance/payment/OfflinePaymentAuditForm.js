Ext.define('Ext.view.finance.payment.OfflinePaymentAuditForm', {
    extend: 'Ext.form.Panel',
    bodyPadding: 5,
    width: 478,
    alias: 'widget.offlinePaymentAuditForm',
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
            text: '审核通过',
            action: 'save'
        }
    ],
    requires: [
        "Ext.view.finance.payment.PaymentMethod"
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
                            labelWidth: 50,
                            margin: '0 20 0 0',
                            name: 'shopName'
                        }
                    ]
                },{
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
                            labelWidth: 60,
                            width: 230,
                            margin: '0 20 0 0',
                            name: 'orderPaymentType',
                            renderer: function (val, style, rec, index) {
                                return val == 'HARDWARE' ? "硬件购买费用" : (val == 'SOFTWARE' ? "软件购买费用" : "短信购买费用");
                            }
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '总价',
                            width: 230,
                            name: 'orderTotalAmount',
                            labelWidth: 40,
                            margin: '0 20 0 20',
                            renderer: function (val, style, rec, index) {
                                return  "￥" + val;
                            }
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
                            xtype: 'displayfield',
                            name: 'receivableMethod',
                            fieldLabel: '支付方式',
                            labelWidth: 70
                        },
                        {
                            xtype: 'fieldset',
                            layout: 'hbox',
                            anchor: '100%',
                            padding: 0,
                            border: false,
                            items: [
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: '收取人',
                                    name: 'payeeName',
                                    readOnly: true,
                                    margin: '0 40 0 0',
                                    labelWidth: 70
                                },
                                {
                                    fieldLabel: '收取时间',
                                    xtype: 'displayfield',
                                    labelWidth: 60,
                                    name: 'recordPaymentTime',
                                    renderer: function (val, style, rec, index) {
                                        if (val) return Ext.util.Format.date(new Date(Number(val)), 'Y-m-d H:i');
                                        return "";
                                    }
                                }
                            ]
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '收取金额',
                            name: 'recordPaidAmount',
                            labelWidth: 70 ,
                            renderer: function (val, style, rec, index) {
                                return  "￥" + val;
                            }
                        },
                        {
                            xtype: 'fieldset',
                            layout: 'hbox',
                            anchor: '100%',
                            padding: 0,
                            border: false,
                            items: [
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: '录入人',
                                    name: 'submitterName',
                                    margin: '0 40 0 0',
                                    labelWidth: 70
                                },
                                {
                                    fieldLabel: '录入时间',
                                    xtype: 'displayfield',
                                    labelWidth: 60,
                                    name: 'submitTime',
                                    renderer: function (val, style, rec, index) {
                                        if (val) return Ext.util.Format.date(new Date(Number(val)), 'Y-m-d H:i');
                                        return "";
                                    }
                                }
                            ]
                        }
                    ]
                },
                {
                    name: 'bcgogoReceivableOrderRecordRelationId',
                    xtype: "hiddenfield"
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
            if (!params['bcgogoReceivableOrderRecordRelationId']) {
                return;
            }
            form.commonUtils.ajax({
                url: 'bcgogoReceivable.do?method=auditReceivable',
                params: params,
                success: function (result) {
                    Ext.Msg.alert('返回结果', "审核通过！", function () {
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