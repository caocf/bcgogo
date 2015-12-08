Ext.define('Ext.view.customerMange.existingCustomerManage.OperatorForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.existingCustomerManageOperatorForm',
    initComponent: function () {
        Ext.apply(this, {
            activeRecord: null,
            iconCls: 'icon-user',
            frame: true,
            bodyPadding: 5,
            layout:'anchor',
            items: [
                {
                    xtype: 'fieldset',
                    anchor: '100%',
                    padding: 0,
                    border: false,
                    fieldDefaults: {
                        anchor: '100%',
                        labelAlign: 'right'
                    },
                    items: [
                        {
                            fieldLabel: '理由',
                            xtype: 'textareafield',
                            name: 'reason',
                            allowBlank: false,
                            labelWidth: 30,
                            grow: true,
                            height: 40,
                            enforceMaxLength: true,
                            maxLength: 100,
                            anchor: '100%'
                        }
                    ]
                },
                {
                    xtype: 'fieldset',
                    padding: 0,
                    anchor:'50%',
                    border: false,
                    items: [
                        {
                            fieldLabel: '若为继续使用，则填写使用期限',
                            name: 'trialTime',
                            labelWidth: 200,
                            width:150,
                            xtype: "datefield",
                            format: 'Y-m-d',
                            anchor: '100%'
                        }
                    ]
                }
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [
                        '->',
                        {
                            iconCls: 'icon-del',
                            action: 'disable',
                            text: '禁用',
                            scope: this
                        },
                        {
                            iconCls: 'icon-enable',
                            action: 'enable',
                            text: '启用',
                            scope: this
                        },
                        {
                            action: 'trial',
                            text: '继续试用',
                            scope: this
                        },
                        {
                            action: 'update',
                            text: '升级',
                            scope: this
                        }
                    ]
                }
            ]
        });
        this.callParent();
    }
});