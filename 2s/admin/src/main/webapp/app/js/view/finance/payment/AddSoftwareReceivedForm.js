Ext.define('Ext.view.finance.payment.AddSoftwareReceivedForm', {
    extend: 'Ext.form.Panel',
    bodyPadding: 5,
    width: 350,
    alias: 'widget.addSoftwareReceivedForm',
    layout: 'anchor',
    defaults: {
        anchor: '100%'
    },
    buttons: [
        {
            text: '重置',
            tooltip: "重置",
            handler: function () {
                this.up("form").form.reset();
            }
        },
        {
            text: '保存',
            action: 'save'
        }
    ],
    requires: [
        "Ext.view.sys.user.UserSelect",
        "Ext.view.customerMange.existingCustomerManage.Select"
    ],
    initComponent: function () {
        var me = this,
            userSelect = Ext.widget("userSelect", {
                fieldLabel: '业务员',
                labelWidth: 70,
                name: 'payeeId',
                valueField: 'id',
                xtype: "userSelect",
                msgTarget: 'under',
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
        Ext.apply(me, {
            items: [
                {
                    fieldLabel: '购买店铺',
                    labelWidth: 70,
                    msgTarget: 'under',
                    xtype: "shopSelect",
                    store: Ext.create('Ext.store.customerMange.Shops', {
                        pageSize: 15,
                        proxy: {
                            extraParams: {
                                shopStatuses: "REGISTERED_TRIAL,REGISTERED_PAID"
                            },
                            type: 'ajax',
                            api: {
                                read: 'shopManage.do?method=getShopSuggestionByName'
                            },
                            reader: {
                                type: 'json',
                                root: "results",
                                totalProperty: "totalRows"
                            }
                        }}),
                    allowBlank: false,
                    name: 'shopId',
                    listeners: {
                        blur: function (comp, e, eOpts) {
                            comp.blurFn(comp, me,
                                function () {
                                    me.down("button[action=save]").enable();
                                },
                                function () {
                                    Ext.Msg.alert('返回结果', " 您输入的店铺名不正确！", function () {
                                        me.down("button[action=save]").disable();
                                    });
                                });
                        }
                    }
                },
                userSelect,
                {
                    xtype: 'datefield',
                    fieldLabel: '支付时间',
                    name: 'receivedTime',
                    msgTarget: 'under',
                    format: 'Y-m-d H:i',
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
                    xtype: 'textfield',
                    fieldLabel: '支付金额',
                    labelWidth: 70,
                    name: 'receivedAmount',
                    vtype: 'money',
                    margin: '0 0 0 0',
                    editable: false
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
            params['receivedTime'] = params['receivedTime'] ? Date.parse(params['receivedTime'].replace(/-/g, "/")) : "";
            form.commonUtils.ajax({
                url: 'bcgogoReceivable.do?method=addSoftwareReceived',
                params: params,
                success: function (result) {
                    if (result['success']) {
                        Ext.Msg.alert('返回结果', "保存成功！", function () {
                            baseForm.reset();
                            callback();
                        });
                    } else {
                        Ext.Msg.alert('返回结果', result['msg'], function () {
                            if (result['data']) form.down("[name=receivedAmount]").setValue(result['data']);
                            form.unmask();
                        });
                    }
                },
                failure: function () {
                    form.unmask();
                }
            });
        }
    }
});