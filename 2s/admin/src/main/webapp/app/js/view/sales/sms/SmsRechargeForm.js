/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 14-1-8
 * Time: 下午2:19
 * To change this template use File | Settings | File Templates.
 */
Ext.define('Ext.view.sales.sms.SmsRechargeForm', {
    extend: 'Ext.form.Panel',
    bodyPadding: 5,
    width: 400,
    alias: 'widget.smsRechargeForm',
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
            text: '确定',
            action: 'save'
        },
        {
            text: '取消',
            action: 'close'
        }
    ],
    requires: [
        "Ext.view.sys.user.UserSelect",
        "Ext.view.customerMange.existingCustomerManage.Select"
    ],
    initComponent: function () {
        var me = this,
            userSelect = Ext.widget("userSelect", {
                typeAhead: true,
                triggerAction: 'all',
                lazyRender: true,
                fieldLabel: '收款人',
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
            }),
            shopSelect = Ext.widget("shopSelect", {
                typeAhead: true,
                triggerAction: 'all',
                lazyRender: true,
                fieldLabel: '充值店铺',
                labelWidth: 70,
                name: 'shopId',
                xtype: "shopSelect",
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
                                Ext.Msg.alert('返回结果', " 您输入的店铺名不存在！", function () {
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
        Ext.apply(me, {
            items: [
                {
                    xtype: 'fieldset',
                    title: '短信充值信息',
                    layout: 'anchor',
                    margin: "10 0 0 0",
                    collapsible: false,
                    collapsed: false,
                    labelAlign:'right',
                    items: [
                        shopSelect,
                        {
                            xtype: 'textfield',
                            fieldLabel: '充值金额',
                            name: 'rechargeAmount',
                            allowBlank: false,
                            labelWidth: 70,
                            vtype : 'integer',
                            listeners: {
                                blur: function () {
                                    me.commonUtils.ajax({
                                        url: 'shopSmsAccount.do?method=getPresentAmountByRechargeAmount',
                                        params: {rechargeAmount:me.down("[name=rechargeAmount]").getValue()},
                                        success: function (result) {
                                            if(result["success"]) {
                                                 if(result["data"]*1 > 0) {
                                                     me.down("[name=presentAmount]").setValue(result["data"]);
                                                 } else {
                                                     me.down("[name=presentAmount]").setValue(0);
                                                 }
                                                me.down("button[action=save]").enable();
                                            } else {
                                                Ext.Msg.alert('返回结果',result["msg"]);
                                                me.down("button[action=save]").disable();
                                            }
                                        }
                                    });

                                }
                            }
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '享受优惠',
                            labelWidth: 70,
                            name: 'presentAmount',
                            value: '--'
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '收款方式',
                            labelWidth: 70,
                            name: 'paymentWay',
                            value: '现金'
                        },
                        userSelect,
                        {
                            xtype: 'displayfield',
                            fieldLabel: '录入人',
                            labelWidth: 70,
                            name: 'submitorName',
                            value: Ext.getDom("userNameForHeader").value
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '录入时间',
                            labelWidth: 70,
                            name: 'submitTime',
                            value: Ext.Date.format(new Date(), 'Y-m-d H:i')
                        }
                    ]
                }
            ]
        });
        this.callParent();
    }

});
