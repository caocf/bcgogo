Ext.define('Ext.view.customerMange.existingCustomerManage.ExtensionShopForm', {
    extend: 'Ext.form.Panel',
    bodyPadding: 5,
    width: 570,
    autoScroll: true,
    alias: 'widget.shopExtensionShopForm',
    layout: 'anchor',
    defaults: {
        anchor: '100%'
    },
    buttons: [
        {
            text: '提交确认',
            action: 'save'
        }
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
                    padding: 0,
                    anchor: '100%',
                    border: false,
                    items: [
                        {
                            xtype: 'displayfield',
                            fieldLabel: '店铺名',
                            name: 'name',
                            flex: 5,
                            labelWidth: 60
                        },
                        {
                            xtype: 'displayfield',
                            flex: 5,
                            fieldLabel: '店主',
                            name: 'owner',
                            labelWidth: 50
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '联系方式',
                            flex: 5,
                            name: 'mobile',
                            labelWidth: 60
                        }
                    ]
                },
                {
                    xtype: 'fieldset',
                    layout: 'hbox',
                    padding: 0,
                    anchor: '100%',
                    border: false,
                    items: [
                        {
                            xtype: 'displayfield',
                            fieldLabel: '状态',
                            labelWidth: 60,
                            flex: 5,
                            name: 'shopState',
                            renderer: function (val, style, rec, index) {
                                if (val == "ACTIVE") {
                                    return "试用中";
                                } else if (val == "ARREARS") {
                                    return "过期禁用";
                                } else {
                                    return "--";
                                }
                            }
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '注册时间',
                            labelWidth: 60,
                            flex: 5,
                            name: 'registrationDate',
                            renderer: function (val, style, rec, index) {
                                return val ? Ext.Date.format(new Date(Number(val)), 'Y-m-d H:i') : "";
                            }
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '试用截止时间',
                            labelWidth: 80,
                            flex: 5,
                            name: 'trialEndTime',
                            renderer: function (val, style, rec, index) {
                                return val ? Ext.Date.format(new Date(Number(val)), 'Y-m-d H:i') : "";
                            }
                        }
                    ]
                },
                {
                    xtype: 'radiogroup',
                    fieldLabel: '审核结果',
                    labelWidth: 60,
                    name: 'extensionDayGroup',
                    columns: 5,
                    items: [
                        {boxLabel: '延长1天', name: 'extensionDays', inputValue: '1', checked: true, width: 80},
                        {boxLabel: '延长5天', name: 'extensionDays', inputValue: '5', width: 80},
                        {boxLabel: '延长10天', name: 'extensionDays', inputValue: '10', width: 80},
                        {boxLabel: '延长15天', name: 'extensionDays', inputValue: '15', width: 80} ,
                        {boxLabel: '延长20天', name: 'extensionDays', inputValue: '20', width: 80}
                    ]
                },
                {
                    xtype: 'textareafield',
                    name: 'reason',
                    fieldLabel: '理由',
                    labelWidth: 60,
                    allowBlank: false
                },
                {
                    name: 'shopId',
                    xtype: "hiddenfield"
                }
            ]
        });
        this.callParent();
    },
    save: function (callback) {
        var form = this, baseForm = form.form;
        if (baseForm.isValid()) {
            form.mask('正在保存 . . .');
            var params = baseForm.getValues();
            form.commonUtils.ajax({
                url: 'shopExtension.do?method=createShopExtensionLog',
                params: params,
                success: function (result) {
                    Ext.Msg.alert('返回结果', result.msg, function () {
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
    freshShopBargainRecordPanel: function (shopId) {
        var panel = this.down("[name=shopBargainRecords]");
        panel.store.proxy.extraParams = {
            shopId: shopId
        };
        panel.store.load();
        panel.refresh();
    }
});