Ext.define('Ext.view.sales.hardware.CancelHardwareOrderForm', {
    extend: 'Ext.form.Panel',
    bodyPadding: 5,
    width: 478,
    alias: 'widget.cancelHardwareOrderForm',
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
                    xtype: 'fieldset',
                    layout: 'hbox',
                    anchor: '100%',
                    padding: 0,
                    margin: '10 0 5 0',
                    border: false,
                    items: [
                        {
                            xtype: 'textareafield',
                            grow: true,
                            maxLength: 200,
                            enforceMaxLength:true,
                            name: 'cancelReason',
                            fieldLabel: '原因',
                            labelAlign:'right',
                            labelWidth:60,
                            width: 420,
                            allowBlank: false
                        },{
                            name: 'bcgogoReceivableOrderId',
                            xtype: "hiddenfield"
                        }
                    ]
                }
            ]
        });
        this.callParent();
    },
    save: function (form, callback) {
        var me = this,
        baseForm = form.form;
        if (baseForm.isValid()) {
            form.mask('正在保存 . . .');
            var params = baseForm.getValues();
            form.commonUtils.ajax({
                async:false,
                url: 'bcgogoReceivable.do?method=cancelBcgogoReceivableOrder',
                params: params,
                success: function (result) {
                    Ext.Msg.alert('返回结果', "保存成功！", function () {
                        baseForm.reset();
                        form.unmask();
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