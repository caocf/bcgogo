Ext.define('Ext.view.finance.payment.BcgogoRechargeForm', {
    extend: 'Ext.form.Panel',
    bodyPadding: 5,
    width: 200,
    alias: 'widget.bcgogoRechargeForm',
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
                    name: 'balance',
                    fieldLabel: '充值金额',
                    vtype: 'money',
                    labelWidth: 70,
                    allowBlank: false
                },
                {
                    xtype: 'textfield',
                    fieldLabel: '短信条数',
                    vtype: 'integer',
                    name: 'number',
                    labelWidth: 70,
                    allowBlank: false
                },
                {
                    fieldLabel: '充值时间',
                    xtype: "datefield",
                    name: 'rechargeTime',
                    msgTarget: 'under',
                    format: 'Y-m-d H:i',
                    labelWidth: 70,
                    allowBlank: false
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
            params['rechargeTime'] = params['rechargeTime'] ? Date.parse(params['rechargeTime'].replace(/-/g, "/")) : "";
            form.commonUtils.ajax({
                url: 'bcgogoSmsAccount.do?method=createBcgogoRecharge',
                params: params,
                success: function (result) {
                    Ext.Msg.alert('返回结果', "录入成功！", function () {
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