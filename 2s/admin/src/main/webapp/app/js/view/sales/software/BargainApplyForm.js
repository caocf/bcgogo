Ext.define('Ext.view.sales.software.BargainApplyForm', {
    extend: 'Ext.form.Panel',
    bodyPadding: 5,
    alias: 'widget.bargainApplyForm',
    layout: 'anchor',
    width: 450,
    defaults: {
        anchor: '100%'
    },
    fieldDefaults: {
        msgTarget: 'under'
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
                            labelAlign:'right',
                            name: 'shopName',
                            flex: 4,
                            labelWidth: 60
                        },
                        {
                            xtype: 'displayfield',
                            flex: 2.5,
                            fieldLabel: '店主',
                            labelAlign:'right',
                            name: 'shopOwner',
                            labelWidth: 50
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '联系方式',
                            labelAlign:'right',
                            flex: 3.5,
                            name: 'shopMobile',
                            labelWidth: 60
                        }
                    ]
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
                            fieldLabel: '软件版本',
                            labelAlign:'right',
                            labelWidth: 60,
                            flex: 4,
                            name: 'shopVersion'
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '原价',
                            labelAlign:'right',
                            labelWidth: 50,
                            flex: 2.5,
                            name: 'softPrice'
                        },
                        {
                            xtype: 'textfield',
                            fieldLabel: '期望金额',
                            labelAlign:'right',
                            labelWidth: 60,
                            flex: 3.5,
                            name: 'applicationPrice',
                            msgTarget: 'under',
                            vtype: 'money',
                            allowBlank: false
                        }
                    ]
                },
                {
                    fieldLabel: '理由',
                    labelAlign:'right',
                    anchor: '100%',
                    labelWidth: 60,
                    xtype: 'textareafield',
                    name: 'applicationReason',
                    allowBlank: false
                },
                {
                    name: 'shopId',
                    xtype: "hiddenfield"
                },
                {
                    name: 'orderId',
                    xtype: "hiddenfield"
                }
            ]
        });
        this.callParent();
    },

    save: function (form, callback) {
        var baseForm = form.form;
        if (baseForm.isValid()) {
            var params = baseForm.getValues();
            Ext.getBody().mask('审核处理中....');
            var me = this;
            me.commonUtils.ajax({
                url: 'shopBargain.do?method=createShopBargainRecord',
                params: params,
                success: function (result) {
                    if (result.success) {
                        Ext.Msg.alert('返回结果', result.msg,function () {
                            callback();
                            Ext.getBody().unmask();
                        }).getEl().setStyle('z-index', '80000');
                    } else {
                        Ext.Msg.alert("警告", result.msg,function () {
                            Ext.getBody().unmask();
                        }).getEl().setStyle('z-index', '80000');
                    }
                }
            });
        }
    }
});