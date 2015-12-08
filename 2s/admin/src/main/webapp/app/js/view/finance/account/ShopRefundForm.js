Ext.define('Ext.view.finance.payment.ShopRefundForm', {
    extend: 'Ext.form.Panel',
    bodyPadding: 5,
    width: 300,
    alias: 'widget.shopRefundForm',
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
            text: '确定',
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
                    xtype: 'textfield',
                    fieldLabel: '退回条数',
                    vtype: 'integer',
                    name: 'number',
                    labelWidth: 70,
                    msgTarget: 'under',
                    allowBlank: false,
                    listeners: {
                        blur: function (comp, e, eOpts) {
                            me.down("[name=balance]").setValue(Number(comp.getValue()) / 10.0)
                        }
                    }
                },
                {
                    xtype: 'textfield',
                    name: 'balance',
                    fieldLabel: '退回金额',
                    vtype: 'money',
                    labelWidth: 70,
                    msgTarget: 'under',
                    readOnly: true,
                    allowBlank: false
                },
                {
                    fieldLabel: '退回日期',
                    xtype: "datefield",
                    name: 'refundTime',
                    msgTarget: 'under',
                    format: 'Y-m-d H:i',
                    labelWidth: 70,
                    allowBlank: false
                },
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
        });
        this.callParent();
    },
    setShopId: function (shopId) {
        this.shopId = shopId;
    },

    getShopId: function () {
        return this.shopId;
    },
    save: function (callback) {
        var form = this, baseForm = form.form;
        if (baseForm.isValid() && form.getShopId()) {
            form.mask('正在保存 . . .');
            var params = baseForm.getValues();
            params['refundTime'] = params['refundTime'] ? Date.parse(params['refundTime'].replace(/-/g, "/")) : "";
            params['shopId'] = form.getShopId();
            form.commonUtils.ajax({
                url: 'shopSmsAccount.do?method=createShopSmsRefund',
                params: params,
                success: function (result) {
                    if(result.success) {
                        Ext.Msg.alert('返回结果', "退款成功！", function () {
                            baseForm.reset();
                            callback();
                        });
                    } else {
                        Ext.Msg.alert('返回结果', result.msg, function () {
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