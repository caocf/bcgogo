Ext.define('Ext.controller.finance.BcgogoSmsAccountController', {
    extend: 'Ext.app.Controller',

    models: [
        'Ext.model.finance.BcgogoSmsAccount',
        'Ext.model.finance.BcgogoSmsRecord'
    ],

    stores: [
        'Ext.store.finance.BcgogoSmsAccounts',
        'Ext.store.finance.BcgogoSmsRecords'
    ],

    views: [
        'Ext.view.finance.account.BcgogoSmsView'
    ],

    requires: [
        "Ext.view.finance.account.BcgogoRechargeWindow",
        "Ext.utils.ComponentUtils",
        "Ext.utils.PermissionUtils",
        "Ext.utils.Common"
    ],

    refs: [
        {ref: 'bcgogoSmsAccountList', selector: 'bcgogoSmsAccountList'},
        {id: 'bcgogoRechargeWindow', ref: 'bcgogoRechargeWindow', selector: 'bcgogoRechargeWindow', xtype: 'bcgogoRechargeWindow', autoCreate: true},
        {ref: 'bcgogoSmsRecordList', selector: 'bcgogoSmsRecordList'}
    ],

    init: function () {
        var me = this;
        this.componentUtils = Ext.create("Ext.utils.ComponentUtils");
        this.permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        this.commonUtils = Ext.create("Ext.utils.Common");
        this.control({
            'bcgogoSmsView': {
//                beforerender: function (view) {
//                    if (!me.permissionUtils.hasPermission("CRM.FINANCE_MANAGER.ACCOUNT.BCGOGO_SMS.LIST")) {
//                        alert("权限配置异常!");
//                        view.down('bcgogoSmsAccountList]').hide();
//                        view.down('bcgogoSmsRecordList]').hide();
//                    }
//                }
            },
            'bcgogoSmsAccountList': {
                afterrender: function () {
                    me.getBcgogoSmsAccountList().store.load();
                }
            },
            'bcgogoSmsRecordList': {
                beforerender: function (view) {
                    if (!me.permissionUtils.hasPermission("CRM.FINANCE_MANAGER.ACCOUNT.BCGOGO_SMS_RECHARGE.ADD")) {
                        view.down('button[action=bcgogoRecharge]').hide();
                    }
                },
                afterrender: function () {
                    me.getBcgogoSmsRecordList().store.load();
                }
            },
            'bcgogoSmsRecordList button[action=search]': {
                click: function () {
                    me.getBcgogoSmsRecordList().onSearch(function () {
                        me.getBcgogoSmsAccountList().store.load();
                    });
                }
            },
            'bcgogoSmsRecordList button[action=bcgogoRecharge]': {
                click: function () {
                    me.getBcgogoRechargeWindow().show();
                }
            },
            'bcgogoRechargeWindow button[action=save]': {
                click: function () {
                    var form = me.getBcgogoRechargeWindow().down('form');
                    form.save(form, function () {
                        me.getBcgogoRechargeWindow().close();
                        me.getBcgogoSmsRecordList().store.load();
                        me.getBcgogoSmsAccountList().store.load();
                    })
                }
            }
        });
    }
});