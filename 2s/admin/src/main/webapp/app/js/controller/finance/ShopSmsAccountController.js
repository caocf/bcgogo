Ext.define('Ext.controller.finance.ShopSmsAccountController', {
    extend: 'Ext.app.Controller',

    models: [
        'Ext.model.finance.ShopSmsAccount',
        'Ext.model.finance.ShopSmsRecord'
    ],

    stores: [
        'Ext.store.finance.ShopSmsAccounts',
        'Ext.store.finance.ShopSmsRecords'
    ],

    views: [
        'Ext.view.finance.account.ShopSmsView'
    ],

    requires: [
        "Ext.view.finance.account.ShopRefundWindow",
        "Ext.utils.ComponentUtils",
        "Ext.utils.PermissionUtils",
        "Ext.utils.Common"
    ],

    refs: [
        {id: 'shopRefundWindow', ref: 'shopRefundWindow', selector: 'shopRefundWindow', xtype: 'shopRefundWindow', autoCreate: true},
        {ref: 'shopSmsView', selector: 'shopSmsView'},
        {ref: 'shopSmsRecordList', selector: 'shopSmsRecordList'},
        {ref: 'shopSmsAccountList', selector: 'shopSmsAccountList'}
    ],

    init: function () {
        var me = this;
        this.componentUtils = Ext.create("Ext.utils.ComponentUtils");
        this.permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        this.commonUtils = Ext.create("Ext.utils.Common");
        this.control({
            'shopSmsView': {
                beforerender: function (view, eOpts) {
                    var hasOneTab = false;
                    if (!me.permissionUtils.hasPermission("CRM.FINANCE_MANAGER.ACCOUNT.SHOP_SMS.LIST")) {
                        view.remove(view.down("shopSmsAccountList"));
                    } else {
                        hasOneTab = true;
                    }
                    if (!me.permissionUtils.hasPermission("CRM.FINANCE_MANAGER.ACCOUNT.SHOP_SMS.DETAIL")) {
                        view.remove(view.down("shopSmsRecordList"));
                    } else {
                        hasOneTab = true;
                    }
                    if (!hasOneTab) {
                        alert("权限配置异常!");
                    }
                }
            },
            //短信详细账目
            'shopSmsRecordList': {
                afterrender: function () {
                    me.getShopSmsRecordList().onSearch();
                }
            },
            //客户短信账单
            'shopSmsAccountList': {
                afterrender: function () {
                    me.getShopSmsAccountList().onSearch();
                }
            },
            'shopSmsAccountList  actioncolumn[name=detail]': {
                click: function (grid, cell, row, col, e) {
                    var rec = grid.getStore().getAt(row),
                        recordList = me.getShopSmsRecordList();
                    recordList.down("[name=shopName]").setValue(rec.get("shopName"));
                    recordList.onSearch();
                    me.getShopSmsView().setActiveTab(recordList);
                }
            },
            'shopSmsAccountList actioncolumn[name=refund]': {
                beforerender: function (view) {
                    if (!me.permissionUtils.hasPermission("CRM.FINANCE_MANAGER.ACCOUNT.SHOP_SMS.REFUND")) {
                        view.hide();
                    }
                },
                click: function (grid, cell, row, col, e) {
                    var rec = grid.getStore().getAt(row);
                    me.getShopRefundWindow().down('form').setShopId(rec.get("shopId"));
                    me.getShopRefundWindow().show();
                }
            },
            'shopRefundWindow button[action=save]': {
                click: function () {
                    var form = me.getShopRefundWindow().down('form');
                    form.save(function () {
                        me.getShopRefundWindow().close();
                        me.getShopSmsAccountList().onSearch();
                    })
                }
            }
        });
    }
});