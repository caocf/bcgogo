Ext.define('Ext.controller.finance.BcgogoHardSoftAccountController', {
    extend: 'Ext.app.Controller',

    models: [
        'Ext.model.finance.HardwareSoftwareAccount',
        'Ext.model.finance.HardwareSoftwareOrder'
    ],

    stores: ['Ext.store.finance.HardwareSoftwareAccounts'],

    views: [
        'Ext.view.finance.account.HardwareSoftwareAccountList'
    ],

    requires: [
        "Ext.utils.ComponentUtils",
        "Ext.utils.PermissionUtils",
        "Ext.utils.Common",
        'Ext.view.finance.account.HardwareSoftwareAccountList'
    ],

    refs: [
        {ref: 'hardwareSoftwareAccountList', selector: 'hardwareSoftwareAccountList'}
    ],

    init: function () {
        var me = this;
        this.componentUtils = Ext.create("Ext.utils.ComponentUtils");
        this.permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        this.commonUtils = Ext.create("Ext.utils.Common");
        this.control({
            //待支付
            'hardwareSoftwareAccountList': {
                afterrender: function () {
                    me.getHardwareSoftwareAccountList().store.load();
                    me.getHardwareSoftwareAccountList().countHardwareSoftwareAccount();
                }
            }
        });
    }
});