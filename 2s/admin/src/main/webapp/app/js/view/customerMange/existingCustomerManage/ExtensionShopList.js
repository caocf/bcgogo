Ext.define('Ext.view.customerMange.existingCustomerManage.ExtensionShopList', {
    extend: 'Ext.form.Panel',
    bodyPadding: 5,
    width: 700,
    autoScroll: true,
    alias: 'widget.shopExtensionShopList',
    layout: 'anchor',
    defaults: {
        anchor: '100%'
    },
    buttons: [
        {
            text: '再次延期',
            action: 'extensionAgain'
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
                    xtype: 'grid',
                    forceFit: true,
                    maxHeight: 400,
                    autoHeight: true,
                    autoScroll: true,
                    stripeRows: true, //每列是否是斑马线分开
                    columnLines: true,
                    store: Ext.create("Ext.store.customerMange.ShopExtensionLogs"),
                    columns: [
                        {
                            header: 'No.',
                            xtype: 'rownumberer',
                            sortable: false,
                            width: 25
                        },
                        {
                            header: '操作时间',
                            dataIndex: 'operateTime',
                            renderer: function (val, style, rec, index) {
                                return val ? Ext.Date.format(new Date(Number(val)), 'Y-m-d H:i') : "";
                            }
                        },
                        {
                            header: '操作人',
                            dataIndex: 'operatorName'
                        },
                        {
                            header: '延期时间',
                            dataIndex: 'extensionDays',
                            renderer: function (val, style, rec, index) {
                                return "延期" + val + "天";
                            }
                        },
                        {
                            header: '试用截止',
                            dataIndex: 'trialEndTime',
                            renderer: function (val, style, rec, index) {
                                return val ? Ext.Date.format(new Date(Number(val)), 'Y-m-d H:i') : "";
                            }
                        },
                        {
                            header: '理由',
                            dataIndex: 'reason'
                        }
                    ]
                }
            ]
        });
        this.callParent();
    },
    freshShopExtensionLogPanel: function (shopId) {
        var panel = this.down("grid");
        panel.store.proxy.extraParams = {
            shopId: shopId
        };
        panel.store.load();
    }
});